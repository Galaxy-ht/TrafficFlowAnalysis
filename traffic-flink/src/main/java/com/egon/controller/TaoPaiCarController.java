package com.egon.controller;

import com.alibaba.fastjson.JSON;
import com.egon.bean.MonitorInfo;
import com.egon.bean.Violation;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 实时套牌分析
 *
 * 当某个卡口中出现一辆行驶的汽车，我们可以通过摄像头识别车牌号，
 * 然后在10秒内，另外一个卡口（或者当前卡口）也识别到了同样车牌的车辆，
 * 那么很有可能这两辆车之中有很大几率存在套牌车，因为一般情况下不可能有车辆在10秒内经过两个卡口。
 * 如果发现涉嫌套牌车，系统实时发出报警信息，同时这些存在套牌车嫌疑的车辆，写入Mysql数据库的结果表中，
 * 在后面的模块中，可以对这些违法车辆进行实时轨迹跟踪。
 */
public class TaoPaiCarController {
    public static void main(String[] args) throws Exception {

        //1. env-准备环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);

        //2. source-加载数据
        //2. source-加载数据  读取kafka中的消息
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "node1.itcast.cn:9092,node2.itcast.cn:9092,node3.itcast.cn:9092");
        properties.setProperty("group.id", "group1");
        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<String>("topic-car",new SimpleStringSchema(),properties);
        DataStreamSource<String> dataStreamSource = env.addSource(kafkaSource);

        //3. transformation-数据处理转换
        // 进行转换  将数据变为一个个的对象
        DataStream<MonitorInfo> mapStream = dataStreamSource.map(new MapFunction<String, MonitorInfo>() {
            @Override
            public MonitorInfo map(String jsonStr) throws Exception {

                // 通过代码可以观察到，json中即使字段不太一致，只要 action_time  类中的字段 actionTime
                MonitorInfo speedInfo = JSON.parseObject(jsonStr, MonitorInfo.class);

                return speedInfo;
            }
        });
        mapStream.print("套牌监控");
        //3. transformation-数据处理转换
        SingleOutputStreamOperator<Violation> resultDs = mapStream.keyBy(speedInfo -> speedInfo.getCar()).flatMap(new RichFlatMapFunction<MonitorInfo, Violation>() {

            ValueState<MonitorInfo> valueState;

            @Override
            public void open(Configuration parameters) throws Exception {
                ValueStateDescriptor<MonitorInfo> stateDescriptor = new ValueStateDescriptor<MonitorInfo>("vs1", MonitorInfo.class);
                valueState = getRuntimeContext().getState(stateDescriptor);
            }

            @Override
            public void flatMap(MonitorInfo speedInfo, Collector<Violation> collector) throws Exception {

                // 根据车牌号，获取上一个汽车的 时间，还有 摄像头的id
                MonitorInfo _speedInfo = valueState.value();
                valueState.update(speedInfo);
                if (_speedInfo != null && speedInfo.getActionTime() - _speedInfo.getActionTime() < 10 && speedInfo.getMonitorId() != _speedInfo.getMonitorId()) {
                    Violation violation = new Violation(0, speedInfo.getCar(), "涉嫌套牌", System.currentTimeMillis());
                    collector.collect(violation);
                }

            }
        });
        resultDs.print("套牌监控");
        //4. sink-数据输出
        resultDs.addSink(JdbcSink.sink("INSERT INTO `t_violation_list` VALUES (?,?,?)", new JdbcStatementBuilder<Violation>() {
            @Override
            public void accept(PreparedStatement ps, Violation value) throws SQLException {
                ps.setString(1,value.getCar());
                ps.setString(2,value.getViolation());
                ps.setLong(3,value.getCreateTime());
            }
        },JdbcExecutionOptions.builder().withBatchSize(1).withBatchIntervalMs(5000).build()
                ,new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withDriverName("com.mysql.cj.jdbc.Driver")
                        .withPassword("root")
                        .withUrl("jdbc:mysql://localhost:3306/traffic_flow_analysis")
                        .withUsername("root").build()));

        //5. execute-执行
        env.execute();
    }
}