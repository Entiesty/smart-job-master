package com.fhxf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fhxf.domain.po.ChatMessage;
import com.fhxf.domain.po.ChatSession;
import com.fhxf.mapper.ChatMessageMapper;
import com.fhxf.mapper.ChatSessionMapper;
import com.fhxf.service.ChatService;
import com.fhxf.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天服务实现类
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageMapper chatMessageMapper;

    private final ChatSessionMapper chatSessionMapper;

    private final WebSocketServer webSocketServer;

    @Override
    public List<ChatSession> getChatSessionsByUserId(Long userId) {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getUserIdA, userId)
                .or()
                .eq(ChatSession::getUserIdB, userId)
                .eq(ChatSession::getIsDeleted, 0)
                .orderByDesc(ChatSession::getLastMessageTime);
        return chatSessionMapper.selectList(wrapper);
    }

    @Override
    public List<ChatMessage> getMessagesBySessionId(Long sessionId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSessionId, sessionId)
                .eq(ChatMessage::getIsDeleted, 0)
                .orderByAsc(ChatMessage::getSendTime);
        return chatMessageMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessage saveMessage(ChatMessage message) {
        // 设置消息属性
        message.setIsRead(0); // 未读
        message.setSendTime(LocalDateTime.now());
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setIsDeleted(0); // 未删除
        
        // 保存消息
        chatMessageMapper.insert(message);
        
        // 更新会话的最后一条消息信息
        ChatSession session = chatSessionMapper.selectById(message.getSessionId());
        if (session != null) {
            session.setLastMessageId(message.getId());
            session.setLastMessageContent(message.getContent());
            session.setLastMessageTime(message.getSendTime());
            
            // 增加未读消息数量
            Long receiverId = message.getReceiverId();
            if (session.getUserIdA().equals(receiverId)) {
                session.setUnreadCount(session.getUnreadCount() + 1);
            } else if (session.getUserIdB().equals(receiverId)) {
                session.setUnreadCount(session.getUnreadCount() + 1);
            }
            
            session.setUpdateTime(LocalDateTime.now());
            chatSessionMapper.updateById(session);
        }
        
        return message;
    }

    @Override
    public void notifyMessageReceived(ChatMessage message) {
        // 通过WebSocket推送消息给接收方
        webSocketServer.sendMessage(message.getReceiverId().toString(), message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatSession createOrGetSession(Long senderId, Long receiverId) {
        // 查询是否已存在会话
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ChatSession::getUserIdA, senderId).eq(ChatSession::getUserIdB, receiverId)
                .or()
                .eq(ChatSession::getUserIdA, receiverId).eq(ChatSession::getUserIdB, senderId))
                .eq(ChatSession::getIsDeleted, 0);
        
        ChatSession session = chatSessionMapper.selectOne(wrapper);
        
        // 如果不存在，则创建新会话
        if (session == null) {
            session = new ChatSession();
            session.setName(""); // 单聊不需要名称
            session.setType(0); // 单聊
            session.setUserIdA(senderId);
            session.setUserIdB(receiverId);
            session.setLastMessageId(0L);
            session.setLastMessageContent("");
            session.setLastMessageTime(LocalDateTime.now());
            session.setUnreadCount(0);
            session.setCreateTime(LocalDateTime.now());
            session.setUpdateTime(LocalDateTime.now());
            session.setIsDeleted(0);
            
            chatSessionMapper.insert(session);
        }
        
        return session;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessagesAsRead(Long sessionId, Long userId) {
        // 更新消息为已读
        LambdaUpdateWrapper<ChatMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ChatMessage::getSessionId, sessionId)
                .eq(ChatMessage::getReceiverId, userId)
                .eq(ChatMessage::getIsRead, 0)
                .set(ChatMessage::getIsRead, 1)
                .set(ChatMessage::getUpdateTime, LocalDateTime.now());
        
        chatMessageMapper.update(null, updateWrapper);
        
        // 更新会话的未读消息数量
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session != null) {
            session.setUnreadCount(0);
            session.setUpdateTime(LocalDateTime.now());
            chatSessionMapper.updateById(session);
        }
    }
}