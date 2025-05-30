package com.fhxf.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 */
@Data
@TableName("chat_message")
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage {

    /**
     * 消息ID
     */
    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 会话ID
     */
    @Column(name = "session_id")
    private Long sessionId;

    /**
     * 发送者ID
     */
    @Column(name = "sender_id")

    private Long senderId;

    /**
     * 接收者ID
     */
    @Column(name = "receiver_id")

    private Long receiverId;

    /**
     * 消息内容
     */
    @Column(name = "content")

    private String content;

    /**
     * 消息类型：0-文本，1-图片，2-文件
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 是否已读：0-未读，1-已读
     */
    @Column(name = "is_read")
    private Integer isRead;

    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private LocalDateTime sendTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @Column(name = "isDeleted")
    private Integer isDeleted;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getSendTime() {
        return sendTime;
    }
}
