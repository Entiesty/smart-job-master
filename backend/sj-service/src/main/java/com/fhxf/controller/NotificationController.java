package com.fhxf.controller;

import com.fhxf.domain.dto.notification.NotificationDTO;
import com.fhxf.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 通知控制器
 * 提供SSE接口用于实时推送通知
 */
@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    /**
     * 获取用户通知流
     * 使用SSE (Server-Sent Events) 实现实时推送
     *
     * @param userId 用户ID
     * @return 通知事件流
     */
    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationDTO>> streamNotifications(@PathVariable Long userId) {
        log.info("用户 {} 开始订阅通知流", userId);
        
        return notificationService.getNotificationsByUser(userId)
                .map(notification -> ServerSentEvent.<NotificationDTO>builder()
                        .id(String.valueOf(notification.getId()))
                        .event("notification")
                        .data(notification)
                        .build())
                .mergeWith(Flux.interval(Duration.ofSeconds(30))
                        .map(tick -> ServerSentEvent.<NotificationDTO>builder()
                                .event("heartbeat")
                                .build()));
    }

    /**
     * 发送通知给指定用户
     *
     * @param userId 用户ID
     * @param notification 通知内容
     * @return 发送结果
     */
    @PostMapping(value ="/{userId}")
    public NotificationDTO sendNotification(
            @PathVariable Long userId,
            @RequestBody NotificationDTO notification) {
        log.info("发送通知给用户 {}: {}", userId, notification.getTitle());
        
        // 设置创建时间
        notification.setCreateTime(LocalDateTime.now());
        notification.setRead(false);
        
        // 发送通知
        notificationService.sendNotification(userId, notification);
        
        return notification;
    }

    /**
     * 测试发送系统通知
     *
     * @param userId 用户ID
     * @return 发送结果
     */
    @GetMapping(value ="/test/{userId}")
    public NotificationDTO sendTestNotification(@PathVariable Long userId) {
        NotificationDTO notification = NotificationDTO.builder()
                .id(System.currentTimeMillis())
                .title("系统通知")
                .content("这是一条测试通知消息")
                .type("SYSTEM")
                .createTime(LocalDateTime.now())
                .read(false)
                .toUserId(userId)
                .build();
        
        notificationService.sendNotification(userId, notification);
        
        return notification;
    }
}