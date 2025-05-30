import  api from './index';

/**
 * 获取用户的会话列表
 * @param {number} userId 用户ID
 * @returns {Promise} 会话列表
 */
export function getChatSessions(userId) {
  return api.get(`/chat/sessions/${userId}`);
}
/**
 * 获取会话的消息记录
 * @param {number} sessionId 会话ID
 * @returns {Promise} 消息记录列表
 */
export function getMessages(sessionId) {
  return api.get(`/chat/messages/${sessionId}`);
}

/**
 * 发送消息
 * @param {Object} data 消息数据
 * @returns {Promise} 发送结果
 */
export function sendMessage(data) {
  return api.post('/chat/send', data);
}

/**
 * 创建新的会话
 * @param {number} senderId 发送者ID
 * @param {number} receiverId 接收者ID
 * @returns {Promise} 创建的会话
 */
export function createSession(senderId, receiverId) {
  return api.post(`/chat/session/create/${senderId}/${receiverId}`);
}

/**
 * 标记消息为已读
 * @param {number} sessionId 会话ID
 * @param {number} userId 用户ID
 * @returns {Promise} 操作结果
 */
export function markMessagesAsRead(sessionId, userId) {
  return api.put(`/chat/messages/read/${sessionId}/${userId}`);
}