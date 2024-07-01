package com.egon.analysis.service.impl;

import com.egon.analysis.config.HbaseConnectionPool;
import com.egon.analysis.service.HbaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * @ClassName: HbaseServiceImpl
 * @author: Leemon
 * @Description: TODO
 * @date: 2023/4/12 18:13
 * @version: 1.0
 */
@Slf4j
@Service
public class HbaseServiceImpl implements HbaseService {

    @Resource
    private HbaseConnectionPool pool;

    @Override
    public Map<String,Map<String,String>> getResultScanner(String tableName, String startRowKey, String stopRowKey){
        Scan scan = new Scan();

        if(StringUtils.isNotBlank(startRowKey) && StringUtils.isNotBlank(stopRowKey)){
            scan.withStartRow(Bytes.toBytes(startRowKey));
            scan.withStopRow(Bytes.toBytes(stopRowKey));
        }

        return this.queryData(tableName,scan);
    }

    public Map<String,Map<String,String>> getResultScannerPrefixFilter(String tableName, String prefix){
        Scan scan = new Scan();

        if(StringUtils.isNotBlank(prefix)){
            Filter filter = new PrefixFilter(Bytes.toBytes(prefix));
            scan.setFilter(filter);
        }

        return this.queryData(tableName,scan);
    }


    @Override
    public Map<String,Map<String,String>> queryData(String tableName, Scan scan){

        Map<String,Map<String,String>> result = new HashMap<>();

        ResultScanner rs = null;
        // 获取表
        Table table= null;
        Connection connection = null;
        try {
            connection = pool.getConnection();
            table = getTable(connection, tableName);
            rs = table.getScanner(scan);
            for (Result r : rs) {
                //每一行数据
                Map<String,String> columnMap = new HashMap<>();
                String rowKey = null;
                for (Cell cell : r.listCells()) {
                    if(rowKey == null){
                        rowKey = Bytes.toString(cell.getRowArray(),cell.getRowOffset(),cell.getRowLength());
                    }
                    columnMap.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()), Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                }

                if(rowKey != null){
                    result.put(rowKey,columnMap);
                }
            }
        }catch (IOException e) {
            log.error(MessageFormat.format("遍历查询指定表中的所有数据失败,tableName:{0}"
                    ,tableName),e);
        }finally {
            close(null, rs, table, connection);
        }

        return result;
    }

    @Override
    public Map<String,String> getRowData(String tableName, String rowKey){
        //返回的键值对
        Map<String,String> result = new HashMap<>();

        Get get = new Get(Bytes.toBytes(rowKey));
        // 获取表
        Table table= null;
        Connection connection = null;
        try {
            connection = pool.getConnection();
            table = getTable(connection, tableName);
            Result hTableResult = table.get(get);
            if (hTableResult != null && !hTableResult.isEmpty()) {
                for (Cell cell : hTableResult.listCells()) {
                    result.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()), Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                }
                // 某些应用场景需要插入到数据库的时间
                if (hTableResult.listCells().size() > 0) {
                    result.put("Timestamp", hTableResult.listCells().get(0).getTimestamp() + "");
                }
            }
        }catch (IOException e) {
            log.error(MessageFormat.format("查询一行的数据失败,tableName:{0},rowKey:{1}"
                    ,tableName,rowKey),e);
        }finally {
            close(null,null, table, connection);
        }

        return result;
    }

    @Override
    public Map<String,String> getFamilyValue(String tableName, String rowKey, String familyName){
        //返回的键值对
        Map<String,String> result = new HashMap<>(2);

        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes(familyName));
        // 获取表
        Table table= null;
        Connection connection = null;
        try {
            connection = pool.getConnection();
            table = getTable(connection, tableName);
            Result getResult = table.get(get);
            if (getResult != null && !getResult.isEmpty()) {
                for (Cell cell : getResult.listCells()) {
                    result.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()), Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                }
            }
        } catch (IOException e) {
            log.error(MessageFormat.format("查询指定单元格的数据失败,tableName:{0},rowKey:{1},familyName:{2}"
                    , tableName, rowKey, familyName), e);
        }finally {
            close(null,null, table, connection);
        }

        return result;
    }

    @Override
    public String getColumnValue(String tableName, String rowKey, String familyName, String columnName){
        String str = null;
        Get get = new Get(Bytes.toBytes(rowKey));
        // 获取表
        Table table= null;
        Connection connection = null;
        try {
            connection = pool.getConnection();
            table = getTable(connection, tableName);
            Result result = table.get(get);
            if (result != null && !result.isEmpty()) {
                Cell cell = result.getColumnLatestCell(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
                if(cell != null){
                    str = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                }
            }
        } catch (IOException e) {
            log.error(MessageFormat.format("查询指定单元格的数据失败,tableName:{0},rowKey:{1},familyName:{2},columnName:{3}"
                    ,tableName,rowKey,familyName,columnName),e);
        }finally {
            close(null,null, table, connection);
        }

        return str;
    }

    private Table getTable(Connection connection, String tableName) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        return table;
    }

    private void close(Admin admin, ResultScanner rs, Table table, Connection connection){
        if(admin != null){
            try {
                admin.close();
            } catch (IOException e) {
                log.error("关闭Admin失败",e);
            }
        }

        if(rs != null){
            rs.close();
        }

        if(table != null){
            try {
                table.close();
            } catch (IOException e) {
                log.error("关闭Table失败",e);
            }
        }

        // 释放连接
        if (Objects.nonNull(connection)) {
            pool.releaseConnection(connection);
        }
    }

}
