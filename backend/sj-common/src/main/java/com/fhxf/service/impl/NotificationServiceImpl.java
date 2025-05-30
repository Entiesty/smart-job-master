package com.fhxf.service.impl;

import com.fhxf.domain.dto.notification.NotificationDTO;
import com.fhxf.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知服务实现类
 * 使用Spring WebFlux的Sinks实现发布订阅模式
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    /**
     * 用户ID到对应Sink的映射，用于管理每个用户的通知流
     */
    private final Map<Long, Sinks.Many<NotificationDTO>> sinks = new ConcurrentHashMap<>();

    @Override
    public void sendNotification(Long userId, NotificationDTO notification) {
        log.info("发送通知给用户 {}: {}", userId, notification.getTitle());
        
        // 设置接收者ID和创建时间
        notification.setToUserId(userId);
        
        // 获取或创建用户的Sink
        Sinks.Many<NotificationDTO> sink = sinks.computeIfAbsent(userId, id -> {
            log.info("为用户 {} 创建新的通知Sink", id);
            return Sinks.many().multicast().onBackpressureBuffer();
        });
        
        // 发送通知到Sink
        sink.tryEmitNext(notification)
                .orThrow();
    }

    @Override
    public Flux<NotificationDTO> getNotificationsByUser(Long userId) {
        log.info("用户 {} 订阅通知流", userId);
        
        // 获取或创建用户的Sink
        Sinks.Many<NotificationDTO> sink = sinks.computeIfAbsent(userId, id -> {
            log.info("为用户 {} 创建新的通知Sink", id);
            return Sinks.many().multicast().onBackpressureBuffer();
        });
        
        // 返回通知流
        return sink.asFlux();
    }
}