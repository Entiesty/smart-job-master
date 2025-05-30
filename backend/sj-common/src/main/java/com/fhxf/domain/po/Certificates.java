package com.fhxf.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.fhxf.domain.enums.CertificateStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

/**
 * 
 * @TableName certificates
 */
@TableName(value ="certificates")
@Data
@Entity
public class Certificates {
    /**
     * 
     */
    @Id
    @TableId(value = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 
     */
    @TableField(value = "name")
    @Column(name = "name")
    private String name;

    /**
     * 
     */
    @TableField(value = "issue_date" )
    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    /**
     * 
     */
    @TableField(value = "statu")
    @Column(name = "statu")
    @Enumerated(EnumType.ORDINAL)

    private CertificateStatusEnum statu;

    /**
     * 
     */
    @TableField(value = "user_id")
    @Column(name = "user_id")

    private Long userId;

    /**
     * 
     */
    @TableField(value = "file_path")
    @Column(name = "file_path")

    private String filePath;

    /**
     * 
     */
    @TableField(value = "created_at" ,fill=FieldFill.INSERT_UPDATE)
    @Column(name = "created_at")

    private LocalDateTime createdAt;

    /**
     * 
     */
    @TableField(value = "updated_at", fill = FieldFill.UPDATE)
    @Column(name = "updated_at")

    private LocalDateTime updatedAt;

    /**
     *
     */
    @TableField(value = "issuer")
    @Column(name = "issuer")

    private String issuer;

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
        Certificates other = (Certificates) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getIssueDate() == null ? other.getIssueDate() == null : this.getIssueDate().equals(other.getIssueDate()))
            && (this.getStatu() == null ? other.getStatu() == null : this.getStatu().equals(other.getStatu()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getFilePath() == null ? other.getFilePath() == null : this.getFilePath().equals(other.getFilePath()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()))
            && (this.getIssuer() == null ? other.getIssuer() == null : this.getIssuer().equals(other.getIssuer()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getIssueDate() == null) ? 0 : getIssueDate().hashCode());
        result = prime * result + ((getStatu() == null) ? 0 : getStatu().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getFilePath() == null) ? 0 : getFilePath().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        result = prime * result + ((getIssuer() == null) ? 0 : getIssuer().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", issueDate=").append(issueDate);
        sb.append(", statu=").append(statu);
        sb.append(", userId=").append(userId);
        sb.append(", filePath=").append(filePath);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", issuer=").append(issuer);
        sb.append("]");
        return sb.toString();
    }
}