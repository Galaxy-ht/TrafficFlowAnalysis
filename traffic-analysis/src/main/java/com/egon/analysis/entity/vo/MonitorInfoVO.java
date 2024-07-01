package com.egon.analysis.entity.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @TableName t_monitor_info
 */
@Data
public class MonitorInfoVO implements Serializable {
    private Integer monitorId;

    private Integer roadId;

    private Integer speedLimit;

    private String areaId;

    private List<List<String>> theGeom;

    private String name;
}