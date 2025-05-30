import axios from 'axios';

/**
 * 通知相关API
 */

/**
 * 创建SSE连接获取实时通知
 * @param {number} userId 用户ID
 * @returns {EventSource} 事件源对象
 */
export const connectNotificationStream = (userId) => {
  // 关闭之前可能存在的连接
  if (window.notificationEventSource) {
    window.notificationEventSource.close();
  }
  
  // 创建新的SSE连接
  const eventSource = new EventSource(`/api/notifications/stream/${userId}`);
  
  // 保存到全局变量以便后续可以关闭
  window.notificationEventSource = eventSource;
  
  return eventSource;
};

/**
 * 关闭SSE连接
 */
export const closeNotificationStream = () => {
  if (window.notificationEventSource) {
    window.notificationEventSource.close();
    window.notificationEventSource = null;
  }
};

/**
 * 发送测试通知
 * @param {number} userId 用户ID
 * @returns {Promise} 请求Promise
 */
export const sendTestNotification = (userId) => {
  return axios.get(`//notifications/test/${userId}`);
};