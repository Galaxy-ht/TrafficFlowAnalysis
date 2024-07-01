package com.egon.analysis.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: HBaseConfig
 * @author: Leemon
 * @Description: TODO
 * @date: 2023/4/12 18:06
 * @version: 1.0
 */
@Configuration
public class HBaseConfig {

    @Value("${hbase.zookeeper.quorum}")
    private String zookeeperQuorum;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String clientPort;

    @Value("${hbase.master.port}")
    private String masterPort;

    @Bean
    public org.apache.hadoop.conf.Configuration hbaseConfiguration() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", zookeeperQuorum);
        conf.set("hbase.zookeeper.property.clientPort", clientPort);
        // 如果hbase是集群，这个必须加上
        // 这个ip和端口是在hadoop/mapred-site.xml配置文件配置的
        conf.set("hbase.master", zookeeperQuorum + ":" + masterPort);

        conf.set("hbase.client.keyvalue.maxsize", "20971520");
        conf = HBaseConfiguration.create(conf);
        return conf;
    }

}

