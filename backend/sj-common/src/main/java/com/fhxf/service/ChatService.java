package com.fhxf.service;

import com.fhxf.domain.po.ChatMessage;
import com.fhxf.domain.po.ChatSession;

import java.util.List;

/**
 * 聊天服务接口
 */
public interface ChatService {

    /**
     * 获取用户的会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ChatSession> getChatSessionsByUserId(Long userId);

    /**
     * 获取会话的消息记录
     * @param sessionId 会话ID
     * @return 消息记录列表
     */
    List<ChatMessage> getMessagesBySessionId(Long sessionId);

    /**
     * 保存消息
     * @param message 消息内容
     * @return 保存后的消息
     */
    ChatMessage saveMessage(ChatMessage message);

    /**
     * 通知消息接收
     * @param message 消息内容
     */
    void notifyMessageReceived(ChatMessage message);

    /**
     * 创建或获取会话
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @return 会话
     */
    ChatSession createOrGetSession(Long senderId, Long receiverId);

    /**
     * 标记消息为已读
     * @param sessionId 会话ID
     * @param userId 用户ID
     */
    void markMessagesAsRead(Long sessionId, Long userId);
}