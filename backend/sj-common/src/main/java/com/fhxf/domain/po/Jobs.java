package com.fhxf.domain.po;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fhxf.domain.enums.JobStatusEnum;
import com.fhxf.global.JsonHandler.ListStringTypeHandler;
import jakarta.persistence.*;
import lombok.Data;

/**
 * 
 * @TableName jobs
 */
@TableName(value ="jobs",autoResultMap = true)
@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Jobs {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "title")
    @Column(name = "title")
    private String title;

    /**
     * 
     */
    @Column(name = "category")
    @TableField(value = "category")
    private String category;

    /**
     * 
     */
    @Column(name = "description")
    @TableField(value = "description")
    private String description;

    /**
     * 
     */
    @Column(name = "location")
    @TableField(value = "location")
    private String location;

    /**
     * 
     */
    @Column(name = "price")
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 
     */
    @Column(name = "salary_type")
    @TableField(value = "salary_type")
    private String salaryType;

    /**
     * 
     */
    @Column(name = "start_date")
    @TableField(value = "start_date")
    private LocalDateTime startDate;

    /**
     * 
     */
    @Column(name = "end_date")
    @TableField(value = "end_date")
    private LocalDateTime endDate;

    /**
     * 
     */
    @Column(name = "headcount")
    @TableField(value = "headcount")
    private Integer headcount;
    /**
     * 
     */
    @Column(name = "skills")
    @TableField(value = "skills",typeHandler = ListStringTypeHandler.class)
    private String skills;
    /**
     * 
     */
    @Column(name = "contact_info")
    @TableField(value = "contact_info")
    private String contractInfo;

    /**
     * 
     */
    @Column(name = "created_at")
    @TableField(value = "created_at" , fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 
     */
    @Column(name = "updated_at")
    @TableField(value = "updated_at",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 
     */
    @Column(name = "user_id")
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 
     */
    @Column(name = "company_name")
    @TableField(value = "company_name")
    private String companyName;

    /**
     * 
     */
    @Column(name = "contract_file_path")
    @TableField(value = "contract_file_path")
    private String contractFilePath;

    /**
     * 
     */
    @Column(name = "other_file_path")
    @TableField(value = "other_file_path")
    private String otherFilePath;

    @Column(name = "status")
    @TableField(value = "status")
    @Enumerated(EnumType.ORDINAL)
    private JobStatusEnum status;

    @JsonSetter
    public void setSkills(List<String> skills) {
        this.skills = skills.toString();
    }
    @JsonGetter
    public List<String> getSkills() {
        return JSONUtil.toList(this.skills, String.class);
    }
}