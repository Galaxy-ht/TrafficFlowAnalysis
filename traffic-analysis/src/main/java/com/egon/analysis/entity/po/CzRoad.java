package com.egon.analysis.entity.po;

import com.egon.analysis.handler.GeometryTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName cz_road
 */
@TableName(value ="cz_road")
@Data
public class CzRoad implements Serializable {
    @TableId
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

    private String theGeom;

    private String areaId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}