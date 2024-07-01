package com.egon.controller;

import com.egon.bean.MonitorInfo;
import com.egon.schema.JSONDeserializationSchema;
import lombok.SneakyThrows;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 实时危险驾驶分析
 *
 * 在本项目中，危险驾驶是指在道路上驾驶机动车：追逐超速竞驶。我们规定：如果一辆机动车在2分钟内，超速通过卡口超过3次以上;而且每次超速的超过了规定速度的20%以上;
 * 这样的机动车涉嫌危险驾驶。系统需要实时找出这些机动车，并报警，追踪这些车辆的轨迹。
 * 注意：如果有些卡口没有设置限速值，可以设置一个城市默认限速。
 * 这样的需求在Flink也是有两种解决思路，第一：状态编程（ListStat）。第二：CEP编程。但是当前的需求使用状态编程过于复杂了。所以我们采用第二种。
 * 同时还要注意：Flume在采集数据的过程中出现了数据乱序问题，一般最长延迟5秒。
 * 涉嫌危险驾驶的车辆信息保存到Mysql数据库表（t_violation_list）中，以便后面的功能中统一追踪这些车辆的轨迹。
 */
public class DangerousDriverController {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "node1.itcast.cn:9092,node2.itcast.cn:9092,node3.itcast.cn:9092");
        prop.setProperty("group.id", "group1");
        // prop.setProperty("auto.offset.reset","latest");
        FlinkKafkaConsumer<MonitorInfo> consumer = new FlinkKafkaConsumer<MonitorInfo>("topic-car",new JSONDeserializationSchema<MonitorInfo>(MonitorInfo.class),prop);

        DataStreamSource<MonitorInfo> ds1 = env.addSource(consumer);
        ds1.print("危驾监控1");
        /*
        // 我们写了一个map算子就是为了将json字符串转换为实体，太不划算了。
        ds1.map(new MapFunction<String, PayEvent>() {
            @Override
            public PayEvent map(String s) throws Exception {
                return JSON.parseObject(s, PayEvent.class);
            }
        }).print();*/
        SingleOutputStreamOperator<MonitorInfo> ds2 = ds1.assignTimestampsAndWatermarks(WatermarkStrategy.<MonitorInfo>forBoundedOutOfOrderness(Duration.ofSeconds(5)).withTimestampAssigner(
                new SerializableTimestampAssigner<MonitorInfo>() {
                    @SneakyThrows
                    @Override
                    public long extractTimestamp(MonitorInfo element, long recordTimestamp) {
                        return element.getActionTime();
                    }
                }
        ));

        ds2.print("危驾监控2");
        // 定义Pattern
        Pattern<MonitorInfo, MonitorInfo> pattern = Pattern.<MonitorInfo>begin("first", AfterMatchSkipStrategy.skipPastLastEvent())
                .where(new SimpleCondition<MonitorInfo>() {
                    @Override
                    public boolean filter(MonitorInfo value) throws Exception {
                        /**
                         *  此处需要调用GuavaCache ,因为每一个卡口的限速是不一样的
                         */
                        int limitSpeed = 60;
                        return value.getSpeed() > limitSpeed * 1.2;
                    }
                }).times(3)
                .within(Time.minutes(2));// 此处的10分钟，一定数据要触发该10分钟的时候才会有结果

        // 使用cep
        //在数据流上用模式匹配
        PatternStream<MonitorInfo> patternStream = CEP.pattern(ds2.keyBy(v -> v.getCar()), pattern);


        // 输出数据
        //选择输出数据
        SingleOutputStreamOperator<String> ds3 = patternStream.select(new PatternSelectFunction<MonitorInfo, String>() {
            @Override
            public String select(Map<String, List<MonitorInfo>> map) throws Exception {
                // String userId = map.get("first").get(0).getUserId();
                // return userId;
                System.out.println("select.......");
                System.out.println(map);
                return map.get("first").get(0).getCar();
            }
        });
        ds3.print("危驾监控：超速的车牌号是：");

        env.execute();

    }
}
