package com.fhxf.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import com.fhxf.domain.enums.JobAcceptanceStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

/**
 * 零工接受的任务
 * @TableName job_acceptances
 */
@TableName(value ="job_acceptances")
@Data
@Entity
public class JobAcceptances {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *
     */
    @TableField(value = "worker_id")
    @Column(name = "worker_id")
    private Long workerId;

    /**
     *
     */
    @TableField(value = "enterprise_id")
    @Column(name = "enterprise_id")
    private Long enterpriseId;

    /**
     *
     */
    @TableField(value = "apply_time", fill = FieldFill.INSERT)
    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    /**
     *
     */
    @TableField(value = "status")
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private JobAcceptanceStatusEnum status;

    /**
     *
     */
    @TableField(value = "comments")
    @Column(name = "comments")
    private String comments;

    /**
     *
     */
    @TableField(value = "created_at",fill = FieldFill.INSERT)
    @Column(name = "created_at")

    private LocalDateTime createdAt;

    /**
     *
     */
    @TableField(value = "updated_at",fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_at")

    private LocalDateTime updatedAt;

    /**
     *
     */
    @TableField(value = "rating")
    @Column(name = "rating")

    private Double rating;

    /**
     *
     */
    @TableField(value = "income")
    @Column(name = "income")

    private Double income;

    /**
     *
     */
    @TableField(value = "days")
    @Column(name = "days")

    private String days;
    /**
     *
     */
    @TableField(value = "job_id")
    @Column(name = "job_id")

    private Integer jobId;
}