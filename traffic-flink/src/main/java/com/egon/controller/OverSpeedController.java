package com.egon.controller;

import com.alibaba.fastjson.JSON;
import com.egon.bean.MonitorInfo;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.shaded.guava18.com.google.common.cache.CacheBuilder;
import org.apache.flink.shaded.guava18.com.google.common.cache.CacheLoader;
import org.apache.flink.shaded.guava18.com.google.common.cache.LoadingCache;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.egon.utils.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实时车辆超速监控
 *
 * 在城市交通管理数据库中，存储了每个卡口的限速信息，但是不是所有卡口都有限速信息，其中有一些卡口有限制。
 * 我们通过实时计算，需要把所有超速超过10%的车辆找出来，并写入关系型数据库中。
 **/
public class OverSpeedController {

    private static final Logger log = LoggerFactory.getLogger(OverSpeedController.class);

    public static void main(String[] args) throws Exception {
        //1.创建env对象
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 这句话可以省略
        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);

        //2.消费kafka中的数据
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "node1.itcast.cn:9092,node2.itcast.cn:9092,node3.itcast.cn:9092");
        properties.setProperty("group.id", "group1");

        FlinkKafkaConsumer consumer = new FlinkKafkaConsumer<>("topic-car", new SimpleStringSchema(), properties);
        DataStream<String> ds1 = env.addSource(consumer);

        ds1.print("超速监控1");

        //3.将字符串转换成MonitorInfo对象
        SingleOutputStreamOperator<MonitorInfo> ds2 = ds1.map(new MapFunction<String, MonitorInfo>() {
            @Override
            public MonitorInfo map(String s) throws Exception {
                return JSON.parseObject(s, MonitorInfo.class);
            }
        });

        ds2.print("超速监控2");
        SingleOutputStreamOperator<MonitorInfo> overSpeedCars = ds2.filter(new RichFilterFunction<MonitorInfo>() {

            Connection connection;
            PreparedStatement ps;
            ResultSet rs;
            LoadingCache<String, Integer> cache;

            @Override
            public void open(Configuration parameters) throws Exception {
                connection = JdbcUtils.getconnection();
                ps = connection.prepareStatement("select speed_limit from t_monitor_info where monitor_id = ?");
                cache = CacheBuilder.newBuilder()
                        .maximumSize(100)                                   //缓存最大存储条数
                        .expireAfterWrite(100, TimeUnit.MINUTES)    //缓存过期时间
                        .build(new CacheLoader<String, Integer>() {
                            // load 是在 别人问cache 要数据，假如缓存没有，就会触发 load 执行
                            @Override
                            public Integer load(String monitorId) throws Exception {
                                System.out.println("查询数据库.......");
                                ps.setString(1, monitorId);
                                rs = ps.executeQuery();
                                //如果t_monitor_info没有记录该卡口限速，则给定一个60的限速
                                int speed_limit = 60;
                                if (rs.next()) {
                                    speed_limit = rs.getInt("speed_limit");
                                }
                                return speed_limit;
                            }
                        });
            }

            @Override
            public void close() throws Exception {
                JdbcUtils.release(rs, ps, connection);

            }

            @Override
            public boolean filter(MonitorInfo monitorInfo) throws Exception {
                Integer speed_limit = cache.get(monitorInfo.getMonitorId());
                System.out.println(speed_limit);
                monitorInfo.setSpeedLimit(speed_limit);
                return monitorInfo.getSpeed() > speed_limit * 1.1; //超速10%，判定为超速
            }
        });

        overSpeedCars.print("超速监控3");

        overSpeedCars.addSink(JdbcSink.sink(
                "insert into t_speeding_info values(null,?,?,?,?,?,?)",
                (PreparedStatement ps, MonitorInfo monitorInfo) -> {
                    ps.setString(1, monitorInfo.getCar());
                    ps.setString(2, monitorInfo.getMonitorId());
                    ps.setString(3, monitorInfo.getRoadId());
                    ps.setDouble(4, monitorInfo.getSpeed());
                    ps.setInt(5, monitorInfo.getSpeedLimit());
                    ps.setLong(6, monitorInfo.getActionTime());
                },
                JdbcExecutionOptions.builder().withBatchSize(100).withBatchIntervalMs(5000).build()
                ,
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://127.0.0.1:3306/traffic_flow_analysis?useSSL=false&useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true")
                        .withDriverName("com.mysql.cj.jdbc.Driver")
                        .withUsername("root")
                        .withPassword("root")
                        .build()));
        env.execute();
    }
}
