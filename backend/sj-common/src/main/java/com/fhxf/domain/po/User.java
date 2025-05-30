package com.fhxf.domain.po;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fhxf.domain.enums.JobAcceptanceStatusEnum;
import com.fhxf.domain.enums.UserRoleEnum;
import com.fhxf.domain.enums.UserStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 
 * @TableName user
 */
@Table(name="user")
@Data
@Entity
@Accessors(chain = true)
public class User {
    /**
     * 
     */
    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 
     */
        @Column(name = "username")
        private String username;

    /**
     * 
     */
        @Column(name = "password")
        private String password;

    /**
     * 
     */
        @Column(name = "contact_email")
        private String contactEmail;

    /**
     * 
     */
        @Column(name = "contact_phone")
        private String contactPhone;

    /**
     * 
     */
        @Column(name = "role")
        @Enumerated(EnumType.ORDINAL)
        private UserRoleEnum role;

    /**
     * 
     */
        @Column(name = "status")
        @Enumerated(EnumType.ORDINAL)
        private UserStatusEnum status;
    /**
     *
     */
        @Column(name = "introduction")
        private String introduction;

    /**
     *
     */
        @Column(name = "avatar")
        private String avatar;
    /**
     *
     */
    @Column(name = "location")
    private String location;
    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 
     */
    @Column(name = "updated_at")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;



}