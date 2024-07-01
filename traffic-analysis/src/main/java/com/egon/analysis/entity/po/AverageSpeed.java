package com.egon.analysis.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName t_average_speed
 */
@TableName(value ="t_average_speed")
@Data
public class AverageSpeed implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Long startTime;

    /**
     * 
     */
    private Long endTime;

    /**
     * 
     */
    private String monitorId;

    /**
     * 
     */
    private Double avgSpeed;

    /**
     * 
     */
    private Integer carCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        AverageSpeed other = (AverageSpeed) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getMonitorId() == null ? other.getMonitorId() == null : this.getMonitorId().equals(other.getMonitorId()))
            && (this.getAvgSpeed() == null ? other.getAvgSpeed() == null : this.getAvgSpeed().equals(other.getAvgSpeed()))
            && (this.getCarCount() == null ? other.getCarCount() == null : this.getCarCount().equals(other.getCarCount()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
        result = prime * result + ((getMonitorId() == null) ? 0 : getMonitorId().hashCode());
        result = prime * result + ((getAvgSpeed() == null) ? 0 : getAvgSpeed().hashCode());
        result = prime * result + ((getCarCount() == null) ? 0 : getCarCount().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", monitorId=").append(monitorId);
        sb.append(", avgSpeed=").append(avgSpeed);
        sb.append(", carCount=").append(carCount);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}