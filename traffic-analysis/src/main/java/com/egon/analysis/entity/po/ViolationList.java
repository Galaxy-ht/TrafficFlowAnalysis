package com.egon.analysis.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName t_violation_list
 */
@TableName(value ="t_violation_list")
@Data
public class ViolationList implements Serializable {
    /**
     * 
     */
    @TableId
    private String car;

    /**
     * 
     */
    private String violation;

    /**
     * 
     */
    private Long createTime;

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
        ViolationList other = (ViolationList) that;
        return (this.getCar() == null ? other.getCar() == null : this.getCar().equals(other.getCar()))
            && (this.getViolation() == null ? other.getViolation() == null : this.getViolation().equals(other.getViolation()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCar() == null) ? 0 : getCar().hashCode());
        result = prime * result + ((getViolation() == null) ? 0 : getViolation().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", car=").append(car);
        sb.append(", violation=").append(violation);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}