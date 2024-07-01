package com.egon.analysis.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName area_info
 */
@Data
@TableName(value ="area_info")
public class AreaInfo implements Serializable {
    /**
     * 
     */
    private String areaId;

    /**
     * 
     */
    private String areaName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}