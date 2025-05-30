import { defineStore } from 'pinia';
import { ref } from 'vue';
import { connectNotificationStream, closeNotificationStream } from '@/api/notification';
import { ElNotification } from 'element-plus';

/**
 * 通知状态管理
 */
export const useNotificationStore = defineStore('notification', () => {
  // 通知列表
  const notifications = ref([]);
  // 是否已连接通知流
  const isConnected = ref(false);
  // 事件源对象
  let eventSource = null;

  /**
   * 连接通知流
   * @param {number} userId 用户ID
   */
  const connectToStream = (userId) => {
    if (!userId || isConnected.value) return;
 
    eventSource = connectNotificationStream(userId);
    isConnected.value = true;

    // 监听通知事件
    eventSource.addEventListener('notification', (event) => {
      try {
        const notification = JSON.parse(event.data);
        addNotification(notification);
        showNotificationPopup(notification);
      } catch (error) {
        console.error('解析通知数据失败:', error);
      }
    });

    // 监听错误
    eventSource.onerror = (error) => {
      console.error('通知流连接错误:', error);
      isConnected.value = false;
      
      // 尝试重新连接
      setTimeout(() => {
        connectToStream(userId);
      }, 5000);
    };
  };

  /**
   * 断开通知流连接
   */
  const disconnectStream = () => {
    closeNotificationStream();
    isConnected.value = false;
    eventSource = null;
  };

  /**
   * 添加通知到列表
   * @param {Object} notification 通知对象
   */
  const addNotification = (notification) => {
    notifications.value.unshift(notification);
    
    // 最多保留20条通知
    if (notifications.value.length > 20) {
      notifications.value = notifications.value.slice(0, 20);
    }
  };

  /**
   * 显示通知弹窗
   * @param {Object} notification 通知对象
   */
  const showNotificationPopup = (notification) => {
    ElNotification({
      title: notification.title,
      message: notification.content,
      type: getNotificationType(notification.type),
      duration: 3000
    });
  };

  /**
   * 获取通知类型
   * @param {string} type 通知类型
   * @returns {string} Element Plus通知类型
   */
  const getNotificationType = (type) => {
    switch (type) {
      case 'SYSTEM':
        return 'info';
      case 'SUCCESS':
        return 'success';
      case 'WARNING':
        return 'warning';
      case 'ERROR':
        return 'error';
      default:
        return 'info';
    }
  };

  /**
   * 清空所有通知
   */
  const clearAllNotifications = () => {
    notifications.value = [];
  };

  /**
   * 标记所有通知为已读
   */
  const markAllAsRead = () => {
    notifications.value = notifications.value.map(notification => ({
      ...notification,
      read: true
    }));
  };

  /**
   * 标记指定通知为已读
   * @param {number} id 通知ID
   */
  const markAsRead = (id) => {
    const index = notifications.value.findIndex(item => item.id === id);
    if (index !== -1) {
      notifications.value[index].read = true;
    }
  };

  /**
   * 删除指定通知
   * @param {number} id 通知ID
   */
  const removeNotification = (id) => {
    notifications.value = notifications.value.filter(item => item.id !== id);
  };

  return {
    notifications,
    isConnected,
    connectToStream,
    disconnectStream,
    addNotification,
    clearAllNotifications,
    markAllAsRead,
    markAsRead,
    removeNotification
  };
});