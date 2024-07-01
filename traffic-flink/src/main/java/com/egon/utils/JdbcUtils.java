package com.egon.utils;

import java.sql.*;

public class JdbcUtils {


    public static Connection getconnection() {
        // 数据库连接信息
        String url = "jdbc:mysql://127.0.0.1:3306/traffic_flow_analysis";
        String user = "root";
        String password = "root";

        // 建立连接
        Connection conn =null;

        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    public static void release(ResultSet rs, PreparedStatement ps, Connection connection) {
        try{
            if(rs != null) {
                rs.close();
            }
            if(ps != null) {
                ps.close();
            }
            if(connection != null) {
                connection.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
