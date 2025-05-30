<template>
  <!-- 消息页面 -->
  <div class="page-section">
    <h1 class="page-title">消息中心</h1>
    <el-row>
      <el-col :span="8">
        <div class="card-left">
          <div class="card-title">
            <span>对话列表</span>
          </div>
          <div
            class="message-card"
            v-for="session in chatStore.sessions"
            :key="session.id"
            @click="selectConversation(session)"
            :class="{ 'active': session.id === chatStore.currentSessionId }"
          >
            <div class="message-header">
              <img :src="avatarMap.get(session.userIdA === user.id ? session.userIdB : session.userIdA)" class="message-avatar" />
              <div class="message-info">
                <div class="message-sender">{{ getUserName(session.userIdA === user.id ? session.userIdB : session.userIdA) }}</div>
                <div class="message-time">{{ formatTime(session.lastMessageTime) }}</div>
              </div>
              <el-badge v-if="session.unreadCount > 0" :value="session.unreadCount" class="unread-badge" />
            </div>
            <div class="message-preview">{{ session.lastMessageContent }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="16">
        <div class="card-right">
          <div class="card-title" v-if="selectedSession">
            <span>与 {{ getUserName(selectedSession.userIdA === user.id ? selectedSession.userIdB : selectedSession.userIdA) }} 的对话</span>
          </div>
          <div class="card-right-top" v-if="selectedSession">
            <div class="card-message">
              <div v-for="msg in selectedSession.chatMessages" :key="msg.id">
                <div
                  style="display: flex; align-items: flex-start; margin-bottom: 5px"
                  :style="{ flexDirection: msg.senderId === user.id ? 'row-reverse' : 'row' }"
                >
                  <img
                    :src="avatarMap.get(msg.senderId)"
                    style="
                      width: 40px;
                      height: 40px;
                      border-radius: 50%;
                      margin: 0 10px;
                    "
                  />
                  <div
                    :style="{
                      backgroundColor: msg.senderId === user.id ? '#ecf5ff' : '#f5f5f5',
                      padding: '10px 15px',
                      borderRadius: '4px',
                      maxWidth: '70%',
                    }"
                  >
                    <div style="color: #666; font-size: 14px">
                      {{ msg.content }}
                    </div>
                    <div
                      style="
                        color: #999;
                        font-size: 12px;
                        text-align: right;
                        margin-top: 5px;
                      "
                    >
                      {{ formatTime(msg.sendTime) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div
            class="card"
            v-else
            style="display: flex; align-items: center; justify-content: center"
          >
            <div style="text-align: center; color: #999">
              <i
                class="el-icon-chat-dot-round"
                style="font-size: 50px; margin-bottom: 15px"
              ></i>
              <div>请从左侧选择对话</div>
            </div>
          </div>
          <div class="card-input" v-if="selectedSession">
            <el-input
              type="textarea"
              :rows="3"
              placeholder="输入消息内容"
              v-model="newMessage"
              style="margin-top: 40px"
              @keyup.enter.ctrl="sendMessage"
            >
            </el-input>
            <div style="margin-top: 10px; text-align: right; padding: 10px">
              <el-button type="primary" @click="sendMessage">发送</el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onUnmounted, computed,onBeforeMount } from "vue";
import { useChatStore } from "../../store/chat";
import { useUserStore } from "../../store/user";
import { ElMessage } from "element-plus";
import dayjs from "dayjs";

// 获取用户信息
const userStore = useUserStore();
const user = userStore.userInfo;

// 获取聊天状态管理
const chatStore = useChatStore();

// 当前选中的会话
const selectedSession = computed(() => {
  return chatStore.sessions.find(s => s.id === chatStore.currentSessionId);
});

// 新消息内容
const newMessage = ref("");

// 在组件挂载时初始化

onBeforeMount(async () => {
  // 初始化WebSocket连接
  chatStore.initWebSocket(user.id);
  
  // 获取会话列表
  await chatStore.getSessions(user.id);

  // 初始化用户头像
  if (chatStore.sessions.length > 0) {
    for (const session of chatStore.sessions) {
      await getAvatarUrl(session.userIdA === user.id? session.userIdB : session.userIdA);
      for (const msg of session.chatMessages) {
        await getAvatarUrl(msg.senderId);
      }
    }
  }
});

// 在组件卸载时关闭WebSocket连接
onUnmounted(() => {
  chatStore.closeWebSocket();
});

// 选择会话
const selectConversation = async (session) => {
  // 获取会话的消息记录
  await chatStore.getChatMessages(session.id);
};

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim()) {
    ElMessage.warning("消息内容不能为空");
    return;
  }
  
  if (!selectedSession.value) {
    ElMessage.warning("请先选择一个会话");
    return;
  }
  
  const messageData = {
    sessionId: selectedSession.value.id,
    senderId: user.id,
    receiverId: selectedSession.value.userIdA === user.id ? selectedSession.value.userIdB : selectedSession.value.userIdA,
    content: newMessage.value,
    type: 0 // 文本消息
  };
  
  try {
    const sentMessage = await chatStore.sendChatMessage(messageData);
    if (sentMessage) {
      console.log('消息发送成功:', sentMessage);
    
    } else {
      ElMessage.error("消息发送失败，请重试");
    }
    newMessage.value = "";
  } catch (error) {
    console.error('发送消息时出错:', error);
    ElMessage.error("消息发送失败，请重试");
  }
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return "";
  
  const messageTime = dayjs(time);
  const now = dayjs();
  
  if (messageTime.isSame(now, 'day')) {
    return messageTime.format('HH:mm');
  } else if (messageTime.isSame(now.subtract(1, 'day'), 'day')) {
    return '昨天 ' + messageTime.format('HH:mm');
  } else if (messageTime.isSame(now, 'year')) {
    return messageTime.format('MM-DD HH:mm');
  } else {
    return messageTime.format('YYYY-MM-DD HH:mm');
  }
};
import { useFileStore } from '../../store/file';
// 获取用户头像
const fileStore = useFileStore();
const avatarMap = ref(new Map());
const getAvatarUrl = async (userId) => {
  if(avatarMap.value.has(userId)){
    return avatarMap.value.get(userId);
  }
  const res = await userStore.getUserAvatar(userId);
  const img = await fileStore.getImg(res);
  avatarMap.value.set(userId, img);
};

// 获取用户名称
const getUserName = (userId) => {
  // 这里应该根据用户ID获取用户名称，暂时使用固定名称
  return userId === user.id ? '我' : `用户${userId}`;
};
watch(chatStore.messages, (newValue) => {
  console.log('消息列表更新:', newValue);
  if (newValue.length > 0) {
    const messageList = document.querySelector('.card-message');
    if (messageList) {
      messageList.scrollTop = messageList.scrollHeight;
    }
  }
});
</script>

<style scoped>
/* 页面标题 */
.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
  font-weight: 600;
}
.card-left {
  overflow-y: auto;
  background-color: white;
  border-radius: 8px 0px 0px 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  max-height: 900px;
  min-height: 900px;
}
/* 卡片样式 */
.card-right {
  
  display: block;
  background-color: white;
  min-height: 900px;
}
.card-right-top {
  overflow-y: auto;
  border-radius: 0px 8px 0px 0px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: white;
  padding: 20px;
  max-height: 625px;
  min-height: 625px;
}
.card-input {
  background-color: white;
  border-radius: 0px 0px 8px 0px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.card-title {
  font-size: 18px;
  margin-bottom: 15px;
  color: #409eff;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
}

/* 消息卡片 */
.message-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
  cursor: pointer;
}

.message-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message-card.active {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.message-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
}

.message-info {
  flex: 1;
}

.message-sender {
  font-weight: 600;
  margin-bottom: 2px;
}

.message-time {
  color: #999;
  font-size: 12px;
}

.message-preview {
  color: #666;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.unread-badge {
  margin-left: 10px;
}

/* 页面切换样式 */
.page-section {
  overflow: hidden;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
