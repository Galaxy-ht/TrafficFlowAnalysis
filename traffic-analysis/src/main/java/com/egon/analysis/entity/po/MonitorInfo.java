package com.egon.analysis.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName t_monitor_info
 */
@TableName(value ="t_monitor_info")
@Data
public class MonitorInfo implements Serializable {
    @TableId
    private Integer monitorId;

    private Integer roadId;

    private Integer speedLimit;

    private String areaId;

    private String theGeom;

    private String name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}