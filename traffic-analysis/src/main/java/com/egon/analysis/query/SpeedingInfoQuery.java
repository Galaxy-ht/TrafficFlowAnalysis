package com.egon.analysis.query;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName t_speeding_info
 */
@Data
public class SpeedingInfoQuery extends Query {
    private Integer id;


    private String car;


    private String monitorId;


    private String roadId;


    private Double realSpeed;


    private Integer limitSpeed;


    private Date actionTime;
}