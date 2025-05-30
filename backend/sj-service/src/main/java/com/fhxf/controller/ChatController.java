package com.fhxf.controller;

import cn.hutool.core.bean.BeanUtil;
import com.fhxf.domain.po.ChatMessage;
import com.fhxf.domain.po.ChatSession;
import com.fhxf.domain.vo.ChatVo;
import com.fhxf.service.ChatService;
import com.fhxf.utils.NotificationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天控制器
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final NotificationUtils notificationUtils;

    /**
     * 获取用户的所有聊天会话
     *
     * @param userId 用户ID
     * @return 聊天会话列表
     */
    @GetMapping("/sessions/{userId}")
    public ResponseEntity<List<ChatVo>> getChatSessions(@PathVariable Long userId) {
        List<ChatVo> list = new ArrayList<>();
        List<ChatSession> sessions = chatService.getChatSessionsByUserId(userId);
        for (ChatSession session : sessions) {
            ChatVo chatVo = BeanUtil.copyProperties(session, ChatVo.class);
            chatVo.setChatMessages(chatService.getMessagesBySessionId(session.getId()));
            list.add(chatVo);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 获取会话的所有消息
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @GetMapping("/messages/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        List<ChatMessage> messages = chatService.getMessagesBySessionId(sessionId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 发送消息
     *
     * @param message 消息内容
     * @return 保存的消息
     */
    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessage message) {
        ChatMessage savedMessage = chatService.saveMessage(message);
        
        // 通知消息接收者
        chatService.notifyMessageReceived(savedMessage);
        
        // 发送实时通知
        if (savedMessage.getReceiverId() != null && !savedMessage.getReceiverId().equals(savedMessage.getSenderId())) {
            String senderName = "用户"; // 实际应用中应该获取发送者的用户名
            notificationUtils.sendChatNotification(
                savedMessage.getReceiverId(),
                "新消息提醒",
                senderName + ": " + (savedMessage.getContent().length() > 20 ? 
                    savedMessage.getContent().substring(0, 20) + "..." : savedMessage.getContent()),
                savedMessage.getSenderId(),
                savedMessage.getSessionId()
            );
        }
        
        return ResponseEntity.ok(savedMessage);
    }

    /**
     * 创建或获取聊天会话
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @return 聊天会话
     */
    @PostMapping("/session/create/{senderId}/{receiverId}")
    public ResponseEntity<ChatSession> createSession(@PathVariable Long senderId, @PathVariable Long receiverId) {
        ChatSession session = chatService.createOrGetSession(senderId, receiverId);
        return ResponseEntity.ok(session);
    }

    /**
     * 标记消息为已读
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @return 无内容响应
     */
    @PutMapping("/messages/read/{sessionId}/{userId}")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long sessionId,
            @PathVariable Long userId) {
        chatService.markMessagesAsRead(sessionId, userId);
        return ResponseEntity.ok().build();
    }
}