package com.egon.simulator;

import org.apache.kafka.clients.producer.*;
import redis.clients.jedis.Jedis;

import java.util.Properties;
import java.util.Random;

public class CheckPoint {
    public static void producer() {

        // Kafka生产者配置
        Properties props = new Properties();
        props.put("bootstrap.servers", "node1.itcast.cn:9092,node2.itcast.cn:9092,node3.itcast.cn:9092"); // Kafka broker地址
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("group.id", "group1");

        // 创建Kafka生产者
        Producer<String, String> producer = new KafkaProducer<>(props);

        // 定义Kafka主题
        String topic = "topic-car";

        // 用于生成随机数据
        Random random = new Random();

        Jedis jedis = new Jedis("127.0.0.1", 6379);

        try {
            // 生成一些数据
            for (;;) {
                //{"actionTime":1686647522,"monitorId":"0003","cameraId":"1","car":"苏D99999","speed":40,"roadId":"01","areaId":"20"}
                // 生成随机数据
                long actionTime = System.currentTimeMillis() / 1000; // 当前时间戳（秒）
                String monitorId = String.format("%04d", random.nextInt(10000)); // 随机的监测点ID
                String cameraId = "1";
                String car = jedis.srandmember("car"); // 随机的车牌号
                double speed = random.nextDouble() * 120; // 随机速度在0到100之间
                String roadId = String.format("%02d", random.nextInt(10) + 1); // 随机的道路ID
                String areaId = String.format("%02d", random.nextInt(6) + 1); // 随机的区域ID

                // 创建JSON字符串
                String jsonData = String.format("{\"actionTime\":%d,\"monitorId\":\"%s\",\"cameraId\":\"%s\",\"car\":\"%s\",\"speed\":%.1f,\"roadId\":\"%s\",\"areaId\":\"%s\"}",
                        actionTime, monitorId, cameraId, car, speed, roadId, areaId);

                // 创建带有键和值的Kafka记录
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonData);

                // 发送记录到Kafka
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            System.err.println("发送记录到Kafka时出错：" + exception.getMessage());
                        } else {
                            System.out.println("记录已发送到分区 " + metadata.partition() + jsonData);
                        }
                    }
                });

                // 在发送下一条记录之前休眠一段时间
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭Kafka生产者
            producer.close();
        }
    }
}
