import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getChatSessions, getMessages, sendMessage, createSession, markMessagesAsRead } from '@/api/chat';

export const useChatStore = defineStore('chat', () => {
  // 消息列表
  const messages = ref([]);
  // 会话列表
  const sessions = ref([]);
  // 当前选中的会话ID
  const currentSessionId = ref(null);
  // WebSocket连接
  let socket = null;
  /**
   * 初始化WebSocket连接
   * @param {number} userId 用户ID
   */
  const initWebSocket = (userId) => {
    // 关闭之前的连接
    if (socket) {
      socket.close();
    }
    // 创建新的WebSocket连接
    // 修正WebSocket URL，确保正确连接到后端WebSocket端点
    const baseUrl = window.location.hostname === 'localhost' ? 'localhost:3000' : window.location.host;
    const wsUrl = `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${baseUrl}/websocket/${userId}`;
    socket = new WebSocket(wsUrl);
    console.log('尝试连接WebSocket:', wsUrl);
    // 连接打开事件
    socket.onopen = () => {
      console.log('WebSocket连接已建立');
      // 连接成功后，可以发送一个心跳消息确认连接状态
      socket.send(JSON.stringify({ type: 'PING' }));
    };
    // 接收消息事件
    socket.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        console.log('收到WebSocket消息:', message);
        // 如果是心跳响应，不做处理
        if (message.type === 'PONG') return;
        // 如果是当前会话的消息，直接添加到消息列表
        sessions.value.forEach(chatVo => {
          if (parseInt(chatVo.id) === message.sessionId) {
            chatVo.chatMessages.push(message);
            console.log('添加新消息到消息列表:',chatVo.id, message);
          }
        });
        // 更新会话列表中的最后一条消息
        updateSessionLastMessage(message);
      } catch (error) {
        console.error('处理WebSocket消息时发生错误:', error, event.data);
      }
    };

    // 连接关闭事件
    socket.onclose = (event) => {
      console.log('WebSocket连接已关闭，代码:', event.code, '原因:', event.reason);
      // 非正常关闭时，尝试重新连接
      if (event.code !== 1000) {
        console.log('尝试在3秒后重新连接...');
        setTimeout(() => {
          if (userId) initWebSocket(userId);
        }, 3000);
      }
    };

    // 连接错误事件
    socket.onerror = (error) => {
      console.error('WebSocket连接发生错误:', error);
    };
  };

  /**
   * 更新会话的最后一条消息
   * @param {Object} message 消息对象
   */
  const updateSessionLastMessage = (message) => {
 
    const sessionIndex = sessions.value.findIndex(s => parseInt(s.id) === message.sessionId);
    console.log('找到的会话索引:', sessionIndex);
    if (sessionIndex !== -1) {
      const session = sessions.value[sessionIndex];
      session.lastMessageContent = message.content;
      session.lastMessageTime = message.sendTime;
      // 如果不是当前会话，增加未读消息数
      console.log(message.sessionId, currentSessionId.value);
      if ( !currentSessionId.value || message.sessionId !== currentSessionId.value && message.receiverId === localStorage.getItem('userId')) {
        session.unreadCount++;
      }
      // 将该会话移到列表最前面
      sessions.value.splice(sessionIndex, 1);
      sessions.value.unshift(session);
    }
  };

  /**
   * 获取用户的会话列表
   * @param {number} userId 用户ID
   */
  const getSessions = async (userId) => {
    try {
      const response = await getChatSessions(userId);
      sessions.value = response.data;
      return sessions.value;
    } catch (error) {
      console.error('获取会话列表失败:', error);
      return [];
    }
  };

  /**
   * 获取会话的消息记录
   * @param {number} sessionId 会话ID
   */
  const getChatMessages = async (sessionId) => {
    try {
      currentSessionId.value = sessionId;
      // 标记消息为已读
      await markMessagesAsRead(sessionId, localStorage.getItem('userId'));
      // 更新会话的未读消息数
      const session = sessions.value.find(s => s.id === sessionId);
      if (session) {
        session.unreadCount = 0;
      }
    } catch (error) {
      console.error('获取消息记录失败:', error);
    }
  };

  /**
   * 发送消息
   * @param {Object} messageData 消息数据
   */
  const sendChatMessage = async (messageData) => {
    try {
      // 发送消息到服务器
      const response = await sendMessage(messageData);
      const sentMessage = response.data;
      
      // 确保消息已添加到消息列表中（避免重复添加）
     
      const messageSession =  sessions.value.find(chatVo =>
       chatVo.id === sentMessage.sessionId);
      console.log('找到的会话:', messageSession.chatMessages);
      const messageExists = messageSession.chatMessages.some(msg => 
        msg.id === sentMessage.id || 
        (msg.sessionId === sentMessage.sessionId && 
         msg.senderId === sentMessage.senderId && 
         msg.content === sentMessage.content && 
         Math.abs(new Date(msg.sendTime || Date.now()) - new Date(sentMessage.sendTime || Date.now())) < 1000)
      );
      if (!messageExists) {
        console.log('添加新发送的消息到列表:', sentMessage);
        messageSession.chatMessages.push(sentMessage);
      }
      
      // 更新会话的最后一条消息
      updateSessionLastMessage(sentMessage);
      return sentMessage;
    } catch (error) {
      console.error('发送消息失败:', error);
      return null;
    }
  };

  /**
   * 创建新的会话
   * @param {number} senderId 发送者ID
   * @param {number} receiverId 接收者ID
   */
  const createChatSession = async (senderId, receiverId) => {
    try {
      const response = await createSession(senderId, receiverId);
      const newSession = response.data;
      sessions.value.unshift(newSession);
      currentSessionId.value = newSession.id;
      return newSession;
    } catch (error) {
      console.error('创建会话失败:', error);
      return null;
    }
  };

  /**
   * 关闭WebSocket连接
   */
  const closeWebSocket = () => {
    if (socket) {
      socket.close();
      socket = null;
    }
  };

  // 添加心跳检测，保持WebSocket连接活跃
  let heartbeatInterval = null;
  
  /**
   * 开始心跳检测
   */
  const startHeartbeat = () => {
    // 清除之前的心跳检测
    stopHeartbeat();
    
    // 每30秒发送一次心跳
    heartbeatInterval = setInterval(() => {
      if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify({ type: 'PING' }));
        console.log('发送心跳包...');
      } else if (socket) {
        console.warn('WebSocket连接已断开，尝试重新连接...');
        const userId = localStorage.getItem('userId');
        if (userId) initWebSocket(userId);
      }
    }, 30000);
  };
  
  /**
   * 停止心跳检测
   */
  const stopHeartbeat = () => {
    if (heartbeatInterval) {
      clearInterval(heartbeatInterval);
      heartbeatInterval = null;
    }
  };
  
  // 修改initWebSocket方法，添加心跳检测
  const originalInitWebSocket = initWebSocket;
  const initWebSocketWithHeartbeat = (userId) => {
    originalInitWebSocket(userId);
    startHeartbeat();
  };
  
  // 修改closeWebSocket方法，停止心跳检测
  const originalCloseWebSocket = closeWebSocket;
  const closeWebSocketWithHeartbeat = () => {
    stopHeartbeat();
    originalCloseWebSocket();
  };
  
  return {
    messages,
    sessions,
    currentSessionId,
    initWebSocket: initWebSocketWithHeartbeat,
    getSessions,
    getChatMessages,
    sendChatMessage,
    createChatSession,
    closeWebSocket: closeWebSocketWithHeartbeat
  };
});