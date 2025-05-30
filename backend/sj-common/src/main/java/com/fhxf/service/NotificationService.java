package com.fhxf.service;

import com.fhxf.domain.dto.notification.NotificationDTO;
import reactor.core.publisher.Flux;

/**
 * 通知服务接口
 * 用于处理系统通知的发送和接收
 */
public interface NotificationService {
    
    /**
     * 发送通知给指定用户
     * 
     * @param userId 接收通知的用户ID
     * @param notification 通知内容
     */
    void sendNotification(Long userId, NotificationDTO notification);
    
    /**
     * 获取指定用户的通知流
     * 
     * @param userId 用户ID
     * @return 通知事件流
     */
    Flux<NotificationDTO> getNotificationsByUser(Long userId);
}