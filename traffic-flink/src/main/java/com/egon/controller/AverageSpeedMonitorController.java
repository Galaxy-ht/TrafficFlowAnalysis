package com.egon.controller;
import com.alibaba.fastjson.JSON;
import com.egon.bean.AverageSpeed;
import com.egon.bean.MonitorInfo;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.sql.PreparedStatement;
import java.util.Properties;

/**
 * 实时卡口拥堵情况监控
 *
 * 卡口的实时拥堵情况，其实就是和通过卡口的车辆平均车速和通过的车辆的数量有关，为了统计实时的平均车速，
 * 我设定一个滑动窗口，窗口长度是为5分钟，滑动步长为1分钟。
 * 平均车速=当前窗口内通过车辆的车速之和 / 当前窗口内通过的车辆数量 ；
 * 并且在Flume采集数据的时候，我们发现数据可能出现时间乱序问题，最长迟到5秒。
 */
public class AverageSpeedMonitorController {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "node1.itcast.cn:9092,node2.itcast.cn:9092,node3.itcast.cn:9092");
        prop.setProperty("group.id", "car-group2");
       // prop.setProperty("auto.offset.reset","latest");
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<String>("topic-car",new SimpleStringSchema(),prop);

        // 将 json的字符串转换为 MonitorInfo 对象
        DataStream<String> ds1 = env.addSource(consumer);
        DataStream<MonitorInfo> mapStream = ds1.map(new MapFunction<String, MonitorInfo>() {
            @Override
            public MonitorInfo map(String s) throws Exception {
                return JSON.parseObject(s, MonitorInfo.class);
            }
        });
        mapStream.print("------");
        KeyedStream<MonitorInfo, String> keyedStream = mapStream.keyBy(new KeySelector<MonitorInfo, String>() {

            @Override
            public String getKey(MonitorInfo monitorInfo) throws Exception {
                return monitorInfo.getMonitorId();
            }
        });

        keyedStream.print("拥堵监控");

        // 每隔1分钟，统计5分钟的平均车速和车的数量   滑动窗口
        SingleOutputStreamOperator<AverageSpeed> resultStream = keyedStream.window(SlidingProcessingTimeWindows.of(Time.minutes(5), Time.minutes(1)))
                .apply(new WindowFunction<MonitorInfo, AverageSpeed, String, TimeWindow>() {

                    @Override
                    public void apply(String key, TimeWindow window, Iterable<MonitorInfo> input, Collector<AverageSpeed> out) throws Exception {

                        // 获取每个卡口的平均车速和车辆数量
                        // 所有汽车的速度之和 / 所有汽车的数量
                        double sumSpeed = 0;
                        int totalCar = 0;
                        for (MonitorInfo monitorInfo : input) {
                            sumSpeed += monitorInfo.getSpeed();
                            totalCar += 1;
                        }
                        double avgSpeed = sumSpeed / totalCar;
                        long start = window.getStart();
                        long end = window.getEnd();
                        out.collect(new AverageSpeed(0, start, end, key, avgSpeed, totalCar));

                    }
                });

        resultStream.addSink(JdbcSink.sink(
                "insert into t_average_speed(id,start_time,end_time,monitor_id,avg_speed,car_count) values(null,?,?,?,?,?)",
                (PreparedStatement ps, AverageSpeed averageSpeed) -> {
                    ps.setLong(1,averageSpeed.getStartTime());
                    ps.setLong(2,averageSpeed.getEndTime());
                    ps.setString(3,averageSpeed.getMonitorId());
                    ps.setDouble(4,averageSpeed.getAvgSpeed());
                    ps.setInt(5,averageSpeed.getCarCount());
                },
                JdbcExecutionOptions.builder().withBatchSize(1).withBatchIntervalMs(5000).build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://localhost:3306/traffic_flow_analysis?useSSL=false&useUnicode=true&characterEncoding=utf8")
                        .withDriverName("com.mysql.cj.jdbc.Driver")
                        .withUsername("root")
                        .withPassword("root")
                        .build()));

        env.execute();
    }

}
