package com.egon.analysis.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OverSpeedVO {
    private int id;

    private Date actionTime;

    private String car;

    private String areaName;

    private Integer roadId;

    private String roadName;

    private Double realSpeed;

    private Integer limitSpeed;
}
