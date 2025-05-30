<template>
   <div id="app">
    <router-view/>
  </div>
</template>
<style >
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}
#app {
  min-height: 100vh;
  height: 100%;
  display: flex;
  flex-direction: column;
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;

}

</style>
<script setup>
import { onBeforeMount, onMounted, onUnmounted } from 'vue';
import VueCookies from "vue-cookies";
import { autoLoginApi } from './api/api';
import { useRouter } from 'vue-router';
import { useUserStore } from './store/user';
import { useFileStore } from './store/file';
const router = useRouter();
const userStore = useUserStore();
//自动登录
const autoLogin = async () => {
    const token = VueCookies.get("token");
    if (!token) {
      return;
    }
    //自动登录
    try {
      let result = await autoLoginApi();
      if (!result) {
        return;
      }
      userStore.getUserInfo();
      const base64 = await fileStore.getImg(userStore.userInfo?.avatar )
      userStore.setAvatar(base64|| "");   
      router.push("/jobList"); 
    } catch (error) {
      router.push("/login");
    }
  };
  const fileStore = useFileStore();


let refreshTimer;

onBeforeMount(async () => {
    autoLogin();
});

onMounted(() => {
    refreshTimer = setInterval(() => {
        if(VueCookies.get("token")) {
            userStore.getUserInfo();
        }
    }, 20 * 1000);
});

onUnmounted(() => {
    if(refreshTimer) {
        clearInterval(refreshTimer);
    }
});

</script>