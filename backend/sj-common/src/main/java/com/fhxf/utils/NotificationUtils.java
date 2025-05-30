package com.fhxf.utils;

import com.fhxf.domain.dto.notification.NotificationDTO;
import com.fhxf.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 通知工具类
 * 提供便捷的方法发送各类通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationUtils {

    private final NotificationService notificationService;

    /**
     * 发送系统通知
     *
     * @param userId  接收用户ID
     * @param title   通知标题
     * @param content 通知内容
     */
    public void sendSystemNotification(Long userId, String title, String content) {
        NotificationDTO notification = createNotification(userId, title, content, "SYSTEM", null, null);
        notificationService.sendNotification(userId, notification);
    }

    /**
     * 发送任务相关通知
     *
     * @param userId    接收用户ID
     * @param title     通知标题
     * @param content   通知内容
     * @param jobId     任务ID
     * @param fromUserId 发送者ID（可选）
     */
    public void sendJobNotification(Long userId, String title, String content, Long jobId, Long fromUserId) {
        NotificationDTO notification = createNotification(userId, title, content, "JOB", fromUserId, jobId);
        notificationService.sendNotification(userId, notification);
    }

    /**
     * 发送聊天相关通知
     *
     * @param userId     接收用户ID
     * @param title      通知标题
     * @param content    通知内容
     * @param fromUserId 发送者ID
     * @param sessionId  聊天会话ID
     */
    public void sendChatNotification(Long userId, String title, String content, Long fromUserId, Long sessionId) {
        NotificationDTO notification = createNotification(userId, title, content, "CHAT", fromUserId, sessionId);
        notificationService.sendNotification(userId, notification);
    }

    /**
     * 发送评价相关通知
     *
     * @param userId     接收用户ID
     * @param title      通知标题
     * @param content    通知内容
     * @param fromUserId 发送者ID
     * @param reviewId   评价ID
     */
    public void sendReviewNotification(Long userId, String title, String content, Long fromUserId, Long reviewId) {
        NotificationDTO notification = createNotification(userId, title, content, "REVIEW", fromUserId, reviewId);
        notificationService.sendNotification(userId, notification);
    }

    /**
     * 创建通知对象
     *
     * @param userId     接收用户ID
     * @param title      通知标题
     * @param content    通知内容
     * @param type       通知类型
     * @param fromUserId 发送者ID（可选）
     * @param businessId 业务ID（可选）
     * @return 通知对象
     */
    private NotificationDTO createNotification(Long userId, String title, String content, String type, Long fromUserId, Long businessId) {
        return NotificationDTO.builder()
                .id(System.currentTimeMillis())
                .title(title)
                .content(content)
                .type(type)
                .createTime(LocalDateTime.now())
                .read(false)
                .toUserId(userId)
                .fromUserId(fromUserId)
                .businessId(businessId)
                .build();
    }
}