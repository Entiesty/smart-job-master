package com.fhxf.websocket;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务器
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    /**
     * 用于存放所有在线客户端
     */
    private static final Map<String, Session> CLIENTS = new ConcurrentHashMap<>();

    /**
     * 对象映射器
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(
    );

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        // 将新的客户端加入在线客户端集合
        CLIENTS.put(userId, session);
        log.info("有新连接加入：{}，当前在线人数为：{}", userId, CLIENTS.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        // 从在线客户端集合中移除关闭的客户端
        CLIENTS.remove(userId);
        log.info("有一连接关闭：{}，当前在线人数为：{}", userId, CLIENTS.size());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId) {
        log.info("服务端收到客户端[{}]的消息：{}", userId, message);
        // 这里可以处理客户端发送的消息，例如转发给其他客户端
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误", error);
    }

    /**
     * 发送消息给指定客户端
     *
     * @param userId  客户端ID
     * @param message 消息内容
     */
    public void sendMessage(String userId, Object message) {
        Session session = CLIENTS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String messageStr = JSONUtil.toJsonStr(message);
                session.getBasicRemote().sendText(messageStr);
                log.info("服务端发送消息给客户端[{}]：{}", userId, messageStr);
            } catch (IOException e) {
                log.error("发送消息给客户端[{}]失败", userId, e);
            }
        } else {
            log.warn("客户端[{}]不在线", userId);
        }
    }

    /**
     * 发送消息给所有客户端
     *
     * @param message 消息内容
     */
    public void sendMessageToAll(Object message) {
        try {
            String messageStr = JSONUtil.toJsonStr(message);
            for (Map.Entry<String, Session> entry : CLIENTS.entrySet()) {
                Session session = entry.getValue();
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(messageStr);
                    log.info("服务端发送消息给客户端[{}]：{}", entry.getKey(), messageStr);
                }
            }
        } catch (IOException e) {
            log.error("发送消息给所有客户端失败", e);
        }
    }
}