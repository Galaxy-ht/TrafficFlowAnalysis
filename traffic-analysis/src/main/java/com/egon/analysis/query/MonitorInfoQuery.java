package com.egon.analysis.query;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MonitorInfoQuery extends Query {
    private Integer monitorId;

    private Integer roadId;

    private Integer speedLimit;

    private String areaId;

    private String theGeom;

    private String name;
}