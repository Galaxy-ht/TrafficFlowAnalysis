package com.egon.analysis.entity.vo;


import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * @TableName cz_road
 */
@Data
public class CzRoadVO implements Serializable {
    private static final long serialVersionUID = 1L;

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

    private List<List<String>> theGeom;

    private String areaId;
}