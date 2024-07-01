package com.egon.analysis.service;

import org.apache.hadoop.hbase.client.Scan;

import java.util.Map;

/**
 * @InterfaceName: HbaseService
 * @author: Leemon
 * @Description: TODO
 * @date: 2023/4/12 18:11
 * @version: 1.0
 */
public interface HbaseService {

    Map<String,Map<String,String>> getResultScanner(String tableName, String startRowKey, String stopRowKey);

    Map<String,String> getRowData(String tableName, String rowKey);

    Map<String,String> getFamilyValue(String tableName, String rowKey, String familyName);

    String getColumnValue(String tableName, String rowKey, String familyName, String columnName);

    Map<String,Map<String,String>> queryData(String tableName, Scan scan);

}
