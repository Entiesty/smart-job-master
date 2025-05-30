package com.fhxf.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话实体类
 */
@Data
@TableName("chat_session")
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)

public class ChatSession {

    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Long id;

    /**
     * 会话名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 会话类型：0-单聊，1-群聊
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 用户A的ID（单聊时使用）
     */
    @Column(name = "user_id_a")
    private Long userIdA;

    /**
     * 用户B的ID（单聊时使用）
     */
    @Column(name = "user_id_b")
    private Long userIdB;

    /**
     * 最后一条消息ID
     */
    @Column(name = "last_message_id")
    private Long lastMessageId;

    /**
     * 最后一条消息内容
     */
    @Column(name = "last_message_content")
    private String lastMessageContent;

    /**
     * 最后一条消息时间
     */
    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数量
     */
    @Column(name = "unread_count")
    private Integer unreadCount;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @Column(name = "is_deleted")
    private Integer isDeleted;
}