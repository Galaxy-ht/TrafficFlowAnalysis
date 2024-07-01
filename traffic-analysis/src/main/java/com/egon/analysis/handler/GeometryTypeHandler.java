package com.egon.analysis.handler;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.*;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Auth:ZERO_LIGHT
 * GeometryHandler
 * 将字符串坐标转换为数据库中Geometry型的handler
 *
 * @author ZERO_LIGHT
 */
@MappedTypes({String.class})
public class GeometryTypeHandler extends BaseTypeHandler<String> {

    @Override
    @SneakyThrows
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) {
        if (!isGeometryData(s)) {
            preparedStatement.setString(i, s);
            return;
        }
        if ("".equals(s)) {
            preparedStatement.setBytes(i, null);
        } else {
            WKTReader wktReader = new WKTReader();

            Geometry pointGeo = wktReader.read(s);
            WKBWriter wkbWriter = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN);
            byte[] writer = wkbWriter.write(pointGeo);
            byte[] wkb = new byte[writer.length + 4];
            ByteOrderValues.putInt(0, wkb, ByteOrderValues.LITTLE_ENDIAN);//定义SRS,这里是0
            System.arraycopy(writer, 0, wkb, 4, writer.length);
            preparedStatement.setBytes(i, wkb);
        }
    }

    private boolean isGeometryData(String s) {
        return s.length() >= 30;
    }

    @Override
    @SneakyThrows
    public String getNullableResult(ResultSet resultSet, String s) {
        return getGeometryStr(resultSet.getBytes(s));
    }

    @Override
    @SneakyThrows
    public String getNullableResult(ResultSet resultSet, int i) {
        return getGeometryStr(resultSet.getBytes(i));
    }

    @Override
    @SneakyThrows
    public String getNullableResult(CallableStatement callableStatement, int i) {
        return getGeometryStr(callableStatement.getBytes(i));
    }

    /**
     * byte数组转字符串
     *
     * @param bytes byte数组
     * @return bute数组对应的字符串
     * @throws ParseException 转换异常
     */
    private String getGeometryStr(byte[] bytes) throws ParseException {
        if (bytes != null) {
            byte[] bytesN = new byte[bytes.length - 4];
            System.arraycopy(bytes, 4, bytesN, 0, bytes.length - 4);
            Geometry geometry = new WKBReader().read(bytesN);
            Coordinate[] coordinates = geometry.getCoordinates();
            StringBuilder sb = new StringBuilder();
            sb.append(geometry.getGeometryType()).append("(");
            for (Coordinate coordinate : coordinates) {
                sb.append(coordinate.y).append(" ").append(coordinate.x).append(",");//指定经度在前，纬度在后，防止出现倒置
            }
            sb.deleteCharAt(sb.length() - 1);  // 删除最后一个逗号
            sb.append(")");
            return sb.toString();
        }
        return null;
    }
}