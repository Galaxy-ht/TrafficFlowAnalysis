package com.egon.analysis.query;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName cz_road
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CzRoadQuery extends Query {
    private Integer id;

    private String osmId;

    private Integer code;

    private String fclass;

    private String name;

    private String ref;

    private String oneway;

    private Integer maxspeed;

    private Long layer;

    private String bridge;

    private String tunnel;

    private String areaId;
}