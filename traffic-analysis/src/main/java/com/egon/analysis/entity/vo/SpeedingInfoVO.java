package com.egon.analysis.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName t_speeding_info
 */
@Data
public class SpeedingInfoVO implements Serializable {
    private Integer id;


    private String car;


    private Integer monitorId;


    private Integer roadId;


    private Double realSpeed;


    private Integer limitSpeed;


    private Date actionTime;
}