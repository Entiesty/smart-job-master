package com.fhxf.domain.po;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.*;
import lombok.Data;

/**
 * 
 * @TableName statistics
 */
@Table(name="statistics")
@Data
@Entity
public class Statistics {
    /**
     * 
     */
    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 
     */
    @Column(name = "total_jobs")
    private Integer totalJobs;

    /**
     * 
     */
    @Column(name = "completed_jobs")
    private Integer completedJobs;

    /**
     * 
     */
    @Column(name = "total_users")
    private Integer totalUsers;

    /**
     * 
     */
    @Column(name = "active_users")
    private Integer activeUsers;

    /**
     * 
     */
    @Column(name = "created_at")
    private Date createdAt;

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
        Statistics other = (Statistics) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTotalJobs() == null ? other.getTotalJobs() == null : this.getTotalJobs().equals(other.getTotalJobs()))
            && (this.getCompletedJobs() == null ? other.getCompletedJobs() == null : this.getCompletedJobs().equals(other.getCompletedJobs()))
            && (this.getTotalUsers() == null ? other.getTotalUsers() == null : this.getTotalUsers().equals(other.getTotalUsers()))
            && (this.getActiveUsers() == null ? other.getActiveUsers() == null : this.getActiveUsers().equals(other.getActiveUsers()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTotalJobs() == null) ? 0 : getTotalJobs().hashCode());
        result = prime * result + ((getCompletedJobs() == null) ? 0 : getCompletedJobs().hashCode());
        result = prime * result + ((getTotalUsers() == null) ? 0 : getTotalUsers().hashCode());
        result = prime * result + ((getActiveUsers() == null) ? 0 : getActiveUsers().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", totalJobs=").append(totalJobs);
        sb.append(", completedJobs=").append(completedJobs);
        sb.append(", totalUsers=").append(totalUsers);
        sb.append(", activeUsers=").append(activeUsers);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}