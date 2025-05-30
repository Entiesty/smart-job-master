package com.fhxf.domain.po;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fhxf.global.JsonHandler.ListStringTypeHandler;
import jakarta.persistence.*;
import lombok.Data;

/**
 * 
 * @TableName worker
 */
@TableName(value ="worker" )
@Data
@Entity
public class Worker {
    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 
     */
    @TableField(value = "exp")
    @Column(name = "exp")
    private String exp;
    /**
     *
     */
    @TableField(value = "done_jobs")
    @Column(name = "done_jobs")
    private Integer doneJobs;
    /**
     * 
     */
    @TableField(value = "label")
    @Column(name = "label")
    private String label;
    @TableField(value = "location")
    @Column(name = "location")
    private String location;
    /**
     * 
     */
    @TableField(value = "work_time_begin")
    @Column(name = "work_time_begin")
    private String workTimeBegin;

    /**
     * 
     */
    @TableField(value = "work_day")
    @Column(name = "work_day")
    private String workDay;

    /**
     * 
     */
    @TableField(value = "work_time_end")
    @Column(name = "work_time_end")
    private String workTimeEnd;

    /**
     * 
     */
    @TableField(value = "user_id")
        @Column(name = "user_id")
    private Long userId;

    /**
     * 
     */
    @TableField(value = "created_at" ,fill = FieldFill.INSERT_UPDATE)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 
     */
    @TableField(value = "rating")
    @Column(name = "rating")
    private Double rating;

    /**
     * 
     */
    @TableField(value = "updated_at" ,fill = FieldFill.INSERT)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @JsonSetter
    public void parseLabel(List<String> labels) {
        this.label = labels.toString();
    }
    @JsonGetter
    public List<String> getJsonLabel() {
        return JSONUtil.toList(this.label, String.class);
    }
    @JsonSetter
    public void parseWorkDay(List<String> days) {
        this.workDay = days.toString();
    }
    @JsonGetter
    public List<String> getJsonWorkDay() {
        return JSONUtil.toList(this.workDay, String.class);
    }

}