package com.fhxf.domain.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知数据传输对象
 * 用于在系统中传递通知信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 通知类型（如：系统通知、任务通知、消息通知等）
     */
    private String type;
    
    /**
     * 通知发送时间
     */
    private LocalDateTime createTime;
    
    /**
     * 通知是否已读
     */
    private Boolean read;
    
    /**
     * 通知发送者ID
     */
    private Long fromUserId;
    
    /**
     * 通知接收者ID
     */
    private Long toUserId;
    
    /**
     * 相关业务ID（如任务ID、聊天ID等）
     */
    private Long businessId;
}