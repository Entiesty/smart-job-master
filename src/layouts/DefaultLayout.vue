<template>
  <el-config-provider :locale="zhCn">
    <el-container class="layout-container">
      <el-aside class="sidebar" :width="isCollapse?'100px':'200px'">
        <div class="el-aside__logo"></div>
        <el-menu
          active-text-color="#ffd04b"
          background-color="#232323"
          :default-active="$route.path"
          text-color="#fff"
          router
          :collapse="isCollapse"
        >
        <el-menu-item index="/home" class="el-menu-item" style="margin: 4px 0; border-radius: 4px;"
        v-if="userStore.userInfo.role=='worker' ">
        <el-icon><i-ep-DocumentAdd /></el-icon>
        <span>个人任务</span>
      </el-menu-item>
      <el-menu-item index="/jobList"  >
        <el-icon><i-ep-Collection /></el-icon>
        <span>任务列表</span>
      </el-menu-item>
      <el-menu-item index="/postJob"  v-if="userStore.userInfo.role=='enterprise'">
        <el-icon><i-ep-DocumentAdd /></el-icon>
        <span>发布任务</span>
      </el-menu-item>
      <el-menu-item index="/task-management"  v-if="userStore.userInfo.role=='enterprise'">
        <el-icon><i-ep-Document /></el-icon>
        <span>任务管理</span>
      </el-menu-item>
      <el-menu-item index="/workerList"  v-if="userStore.userInfo.role=='enterprise'">
        <el-icon><i-ep-Search /></el-icon>
        <span>工作者查找</span>
      </el-menu-item>
      <el-menu-item index="/reviewList">
        <el-icon><i-ep-ChatLineSquare /></el-icon>
        <span>评价查看</span>
      </el-menu-item>
      <el-menu-item index="/chat">
        <el-icon><i-ep-Comment /></el-icon>
        <span>聊天</span>
      </el-menu-item>
      <el-menu-item index="/analytics">
        <el-icon><i-ep-Histogram /></el-icon>
        <span>统计分析</span>
      </el-menu-item>
      <el-menu-item index="/profile">
        <el-icon><i-ep-user/></el-icon>
        <span>个人资料</span>
      </el-menu-item>
        </el-menu>
      </el-aside>   
      <el-container>
        <el-header>
        <div  class="toggle-button" @click="toggleCollapse">
          <el-icon v-if="isCollapse"><i-ep-Expand /></el-icon>
          <el-icon v-else><i-ep-fold /></el-icon>
        </div>
          <div class="right">
          <el-badge :value="notificationStore.notifications.length" :hidden="notificationStore.notifications.length === 0" class="notification-badge">
            <el-popover
              placement="bottom"
              :width="300"
              trigger="click"
              popper-class="notification-popover"
              @show="notificationStore.markAllAsRead"
            >
              <template #reference>
                <el-icon class="info"><i-ep-Bell /></el-icon>
              </template>
              <div class="notification-container">
                <div class="notification-header">
                  <span>通知</span>
                  <el-button v-if="notificationStore.notifications.length > 0" type="text" @click="notificationStore.clearAllNotifications">清空</el-button>
                </div>
                <div v-if="notificationStore.notifications.length === 0" class="empty-notification">
                  暂无通知
                </div>
                <div v-else class="notification-list">
                  <div v-for="notification in notificationStore.notifications" :key="notification.id" class="notification-item">
                    <div class="notification-title">{{ notification.title }}</div>
                    <div class="notification-content">{{ notification.content }}</div>
                    <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
                  </div>
                </div>
              </div>
            </el-popover>
          </el-badge>
          <el-dropdown placement="bottom-end" @command="handleCommand" trigger="click">
            <span class="el-dropdown__box">
              <strong class="username">{{ userInfo.username }}</strong>
              <el-avatar :src="userStore.userAvatar " />
              <el-icon><i-ep-CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile" >
              <el-icon><i-ep-User /></el-icon>
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item command="logout" >
                  <el-icon><i-ep-CloseBold /></el-icon>
                  退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
        <el-footer>智慧零工平台 ©2025 Created by fhxf</el-footer>
    </el-container>
    </el-container>
 
  </el-config-provider>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';
import { useUserStore } from '@/store/user';
import { useNotificationStore } from '@/store/notification';
import { zhCn } from 'element-plus/es/locales.mjs';
import { ElMessageBox } from 'element-plus';

const userStore = useUserStore();
const notificationStore = useNotificationStore();
const userInfo = ref(userStore.userInfo);
const router = useRouter();
const isCollapse = ref(false);

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

// 处理下拉菜单命令
const handleCommand = async (command) => {
 if(command==='logout')
 {
 await ElMessageBox.confirm(
    '确定退出登录?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    userStore.logout();
    //刷新页面，清除cookie
    router.push('/login').then(()=> window.location.reload())
 }else{
  router.push(`/${command}`)
 }
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

onMounted(() => {
  // 组件挂载时连接通知流
  if (userStore.userInfo && userStore.userInfo.id) {
    notificationStore.connectToStream(userStore.userInfo.id);
  }
});

onUnmounted(() => {
  // 组件卸载时断开通知流
  notificationStore.disconnectStream();
});
</script>

<style lang="scss" scoped>

.el-container {
  height: 100vh;
}

.el-header {
  background-color: #2a2d31;
  color: #813131;
}
.layout-container {
  height: 100vh;
  overflow: hidden;
  background: url(@/assets/background.jpg) no-repeat center /  120px auto;
}

.sidebar {
  background-color: #232323;
  transition: width 0.3s ease;
  max-height: 100%;
  overflow-y: auto;
  overflow-x: hidden; // 添加此行以隐藏水平滚动条
  .el-aside__logo {
    height: 10%;
    background: url(@/assets/logo2.png) no-repeat center / 120px auto;
  }
  
  .el-menu {
    border-right: none;
  }
  @media (max-width: 768px) {
    width: 64px;
    .el-menu--collapse {
      width: 64px;
    }
  }
}

.el-container {
  height: 100vh;
  overflow: hidden; // 添加此行以隐藏任何溢出的内容
}
.el-main {
  background-color: #f0f2f5;
  overflow: auto;
}
.el-header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 8%;
    .toggle-button{
      // 设置文本大小
      font-size:30px;
      // 设置文本行高
      line-height:24px;
      // 设置文本颜色
      // 设置文本居中
      text-align: center;
      // 设置文本间距
      letter-spacing: 0.2em;
      // 设置鼠标悬浮变小手效果
      justify-content:flex-start;
      cursor: pointer;
    }
    .right{
      .info{
      font-size: 20px;
      color: #999;
      cursor: pointer;
      margin-right: 10px;
      }
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  .el-dropdown__box {
    cursor: pointer;
    display: flex;
    align-items: center;
    width: 100px;
    justify-content: space-between;
    .el-avatar {
      width: 40px;
      height: 40px;
      object-fit: cover;
      border-radius: 50%;
    }
    .username {
      padding:  10px 0;
      align-items: center;
      display: center;
      display: flex;
      font-size:18px;
      color: #333;
    }
    .el-icon {
      color: #999;
    }
    &:active,
    &:focus {
      outline: none;
    }
  }
}
.el-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #666;
}
.el-main {
  background-color: #f0f2f5;
}

.notification-badge {
  margin-right: 10px;
}

.notification-container {
  max-height: 400px;
  overflow-y: auto;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}

.empty-notification {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.notification-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  
  &:last-child {
    border-bottom: none;
  }
}

.notification-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.notification-content {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

.el-header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 8%;
    .toggle-button{
      // 设置文本大小
      font-size:30px;
      // 设置文本行高
      line-height:24px;
      // 设置文本颜色
      // 设置文本居中
      text-align: center;
      // 设置文本间距
      letter-spacing: 0.2em;
      // 设置鼠标悬浮变小手效果
      justify-content:flex-start;
      cursor: pointer;
    }
    .right{
      .info{
      font-size: 20px;
      color: #999;
      cursor: pointer;
      margin-right: 10px;
      }
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  .el-dropdown__box {
    cursor: pointer;
    display: flex;
    align-items: center;
    width: 100px;
    justify-content: space-between;
    .el-avatar {
      width: 30px;
      height: 30px;
      object-fit: cover;
      border-radius: 50%;
    }
    .username {
      padding:  10px 0;
      align-items: center;
      display: center;
      display: flex;
      font-size:18px;
      color: #333;
    }
    .el-icon {
      color: #999;
    }
    &:active,
    &:focus {
      outline: none;
    }
  }
}
.el-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #666;
}
.el-main {
  background-color: #f0f2f5;
}

</style>