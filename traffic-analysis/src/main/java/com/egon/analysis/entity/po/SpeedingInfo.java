package com.egon.analysis.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName t_speeding_info
 */
@TableName(value ="t_speeding_info")
@Data
public class SpeedingInfo implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;


    private String car;


    private Integer monitorId;


    private Integer roadId;


    private Double realSpeed;


    private Integer limitSpeed;


    private Long actionTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}