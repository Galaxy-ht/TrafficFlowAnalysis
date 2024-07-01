package com.egon.analysis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @ClassName: HbaseConnectionPool
 * @author: Leemon
 * @Description: TODO
 * @date: 2023/4/13 9:45
 * @version: 1.0
 */
@Component
@Slf4j
public class HbaseConnectionPool {

    /**
     * 连接池最大的大小
     */
    private int nMaxConnections = 20;

    /**
     * 连接池自动增加的大小
     */
    private int nIncrConnectionAmount = 3;

    /**
     * 连接池的初始大小
     */
    private int nInitConnectionAmount = 3;

    /**
     * 存放连接池中数据库连接的向量,初始时为null
     */
    private Vector vcConnections = null;

    @Resource
    private Configuration hbaseConfiguration;

    @PostConstruct
    public void init() {
        try {
            vcConnections = new Vector();
            createConnections(nInitConnectionAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Connection getConnection() {
        // 确保连接池己被创建
        if (vcConnections == null) {
            // 连接池还没创建,则返回null
            return null;
        }
        // 获得一个可用的数据库连接
        Connection conn = getFreeConnection();
        // 如果目前没有可以使用的连接，即所有的连接都在使用中
        while (conn == null) {
            // 等一会再试
            try {
                wait(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 重新再试，直到获得可用的连接，如果getFreeConnection()返回的为null,则表明创建一批连接后也不可获得可用连接
            conn = getFreeConnection();
        }

        // 返回获得的可用的连接
        return conn;

    }

    /**
     * 本函数从连接池向量 connections 中返回一个可用的的数据库连接，如果
     * 当前没有可用的数据库连接，本函数则根据 incrementalConnections 设置
     * 的值创建几个数据库连接，并放入连接池中。
     * 如果创建后，所有的连接仍都在使用中，则返回 null
     * @return
     * 		返回一个可用的数据库连接
     */
    private Connection getFreeConnection() {
        // 从连接池中获得一个可用的数据库连接
        Connection conn = findFreeConnection();
        if (conn == null) {
            // 如果目前连接池中没有可用的连接
            // 创建一些连接
            try {
                createConnections(nIncrConnectionAmount);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("create new connection fail.", e);
            }
            // 重新从池中查找是否有可用连接
            conn = findFreeConnection();
            if (conn == null) {
                // 如果创建连接后仍获得不到可用的连接，则返回 null
                return null;
            }
        }

        return conn;
    }

    /**
     * 创建由 numConnections 指定数目的数据库连接 , 并把这些连接
     * 放入 connections 向量中
     * @param _nNumConnections 要创建的数据库连接的数目
     * @throws Exception
     */
    private void createConnections(int _nNumConnections) throws Exception {
        // 循环创建指定数目的数据库连接
        for (int x = 0; x < _nNumConnections; x++) {
            // 是否连接池中的数据库连接的数量己经达到最大？最大值由类成员 maxConnections
            // 指出，如果 maxConnections 为 0 或负数，表示连接数量没有限制。
            // 如果连接数己经达到最大，即退出。
            if (this.nMaxConnections > 0  && this.vcConnections.size() >= this.nMaxConnections) {
                log.warn("已达到最大连接数，不能再增加连接");
                throw new Exception("已达到最大连接数"+ nMaxConnections+"，不能再增加连接");
            }

            // 增加一个连接到连接池中（向量 connections 中）
            vcConnections.addElement(new ConnectionWrapper(newConnection()));

            log.info("HBase数据库连接己创建 ...... " + x);
        }
    }

    /**
     * 查找池中所有的連接，查找一个可用的數據庫連接，
     * 如果没有可用的連結，返回null
     * @return
     * 		返回一個可用的數據庫連接
     */
    private Connection findFreeConnection() {
        Connection conn = null;
        ConnectionWrapper connWrapper = null;
        //獲得連接池向量中所有的對象
        Enumeration enumerate = vcConnections.elements();
        //遍歷所有的对象，看是否有可用的連接
        while (enumerate.hasMoreElements()) {
            connWrapper = (ConnectionWrapper) enumerate.nextElement();
            if (!connWrapper.isBusy()) {
                //如果此對象不忙，則獲得它的數據庫連接并把它設為忙
                conn = connWrapper.getConnection();
                connWrapper.setBusy(true);
                // 己经找到一个可用的連接，退出
                break;
            }
        }

        // 返回找到的可用連接
        return conn;
    }

    /**
     *创建一个新的数据库连接并返回它
     * @return
     * 		返回一个新创建的数据库连接
     */
    private Connection newConnection() {
        /** hbase 连接 */
        Connection conn = null;
        // 创建一个数据库连接
        try {
            conn = ConnectionFactory.createConnection(hbaseConfiguration);
        } catch (IOException e) {
            log.error("创建HBase数据库连接失败!");
            e.printStackTrace();
        }

        // 返回创建的新的数据库连接
        return conn;
    }

    public synchronized void releaseConnection(Connection conn) {
        if (this.vcConnections == null) {
            log.info("连接池不存在，无法返回此连接到连接池中!!");
        } else {
            ConnectionWrapper connWrapper = null;
            Enumeration enumerate = this.vcConnections.elements();

            while(enumerate.hasMoreElements()) {
                connWrapper = (ConnectionWrapper) enumerate.nextElement();
                if (conn == connWrapper.getConnection()) {
                    connWrapper.setBusy(false);
                    break;
                }
            }

        }
    }

    class ConnectionWrapper {

        /**
         * 数据库连接
         */
        private Connection connection = null;

        /**
         * 此连接是否正在使用的标志，默认没有正在使用
         */
        private boolean busy = false;

        /**
         * 构造函数，根据一个 Connection 构告一个 PooledConnection 对象
         */
        public ConnectionWrapper(Connection connection) {
            this.connection = connection;
        }

        /**
         * 返回此对象中的连接
         */
        public Connection getConnection() {
            return connection;
        }

        /**
         * 设置此对象的连接
         */
        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        /**
         * 获得对象连接是否忙
         */
        public boolean isBusy() {
            return busy;
        }

        /**
         * 设置对象的连接正在忙
         */
        public void setBusy(boolean busy) {
            this.busy = busy;
        }
    }

}
