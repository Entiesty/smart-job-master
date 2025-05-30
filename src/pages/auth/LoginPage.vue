<template>
  <div class="login-page">
    <div class="form-panel">
      <div class="form-panel bg">
        <img :src="'/src/assets/logo (1).png'" />
      </div>
      <el-form
        :model="from"
        ref="ruleFormRef"
        label-width="60px"
        class="form-panel login-register"
        :rules="rules"
      >
      <div class="login-title">账号登录</div>
        <div  class="form">
          <el-form-item prop="username">
            <el-input
              placeholder="请输入用户名"
              v-model="from.username"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password" >
            <el-input
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              v-model="from.password"
            >
              <template #suffix>
                <span @click="showPassword = !showPassword">
                  <el-icon v-if="showPassword"><View /></el-icon>
                  <el-icon v-else><Hide /></el-icon>
                </span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="captcha">
            <el-input
              v-model="from.captcha"
              placeholder="请输入验证码"
              style="width: 60%"
            ></el-input>
            <img :src="captchaImage" @click="refreshCaptcha" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitLogin">登录</el-button>
          </el-form-item>
        </div>
        <div class="tab-panel">
          <div :class="[opType == 0 ? '' : 'active']" @click="showPanel(1)">
            雇主注册
          </div>
          <el-divider direction="vertical" />
          <div :class="[opType == 1 ? '' : 'active']" @click="showPanel(0)">
            零工注册
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, onUpdated } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { View, Hide } from "@element-plus/icons-vue";
import { useFileStore } from "@/store/file";
import { ElMessage } from "element-plus";
const ruleFormRef = ref(null);
const showPassword = ref(false);
const opType = ref(null); //0 企业 1 个人
const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 10, message: "用户名必须为3-10位的字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      pattern: /^\S{6,15}$/,
      message: "密码必须为6-15位非空字符",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      pattern: /^\S{6,15}$/,
      message: "密码必须为6-15位非空字符",
      trigger: "blur",
    },
  ],
  registerPassword: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      pattern: /^\S{6,15}$/,
      message: "密码必须为6-15位非空字符",
      trigger: "blur",
    },
  ],
  registerPassword: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      pattern: /^\S{6,15}$/,
      message: "密码必须为6-15位非空字符",
      trigger: "blur",
    },
  ],
  reRegisterPassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    {
      pattern: /^\S{6,15}$/,
      message: "密码必须为6-15位非空字符",
      trigger: "blur",
    },
  ],

  captcha: [{ required: true, message: "请输入验证码", trigger: "blur" }],
};
const from = ref({
  username: "",
  password: "",
  captcha: "",
  registerPassword: "",
  reRegisterPassword: "",
  nickName: "",
  captchaKey: "",
});

const fileStore = useFileStore();
onMounted(async() => {
 
  refreshCaptcha();
});

const showPanel = (type) => {
  opType.value = type;
  if (type == 1) {
    console.log("showPanel");
    router.push("/enterpriseRegister");
  } else if(type == 0){
    console.log("showPanel");
    router.push("/workerRegister");
  }
};

const captchaImage = ref("/src/assets/logo.png");

const refreshCaptcha = async () => {
  await userStore.captcha();
  captchaImage.value = userStore.captchaImg;
};
const userStore = useUserStore();
const router = useRouter();
const submitLogin = async () => {
  await ruleFormRef.value.validate(async (valid) => {
    if (valid) {
    from.value.captchaKey = userStore.captchaKey;
     await userStore.login(from.value).then(async () => {
      const base64 = await fileStore.getImg(userStore.userInfo?.avatar )
      userStore.setAvatar(base64);   
    })

    router.push("/jobList");
    } else {
      ElMessage.warning('请填写完整的信息');
      return;
    }
  });
};

</script>

<style lang="scss" scoped>
.login-page {
  height: 100vh;
  background: url("@/assets/backround.png") no-repeat center / cover;
  animation: gradientBG 15s ease infinite;
  display: flex;
  align-items: center;
  justify-content: center;
  @keyframes gradientBG {
    0% {
      background-position: 0% 0%;
    }
    50% {
      background-position: 100% 100%;
    }
    100% {
      background-position: 0% 0%;
    }
  }
  .form-panel {
    width: 800px;
    height: 400px;
    display: flex;
    background: rgba(255, 255, 255, 255);
    border-radius: 12px 12px 12px 12px;
    box-shadow: 0 0 30px rgba(0, 0, 0, 0.1);
    &.bg {
      width: 50%;
      height: auto;
      background: #1a73e8;
      flex-direction: column;
      border-radius: 12px 0px 0px 12px;
      box-shadow: 0px 16px 73px 8px rgba(203, 203, 203, 0.2);

      img {
        height: auto;
        width: auto;
        transition: all 1.3s ease; /* 平滑过渡效果 */
      }
    }
    &.login-register {
      width: 60%;
      height: 100%;
      border-radius: 0px 12px 12px 0px;
      align-items: center;
      padding: 0px 20px;
      flex-direction: column;
      justify-content: center;
        .login-title {
        font-size: 30px;
        font-weight: 700;
        text-align: center;
        color: #364e81;
        margin-top: 10px;
      }
      .tab-panel {
        display: flex;
        align-items: center;
        justify-content: space-around;
        cursor: pointer;
        .active {
          color: var(--el-color-primary);
        }
        > div {
          
          margin: 10px 20px;
          justify-content: space-between;
        }
      }
      .form {
          margin-top: 40px;
          justify-content: space-between;
        }
    }
  }
}
.el-form-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 80%;
  img {
    width: 40%;
  }
}
.el-input {
  width: 100%;
}
.el-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80%;
  margin: 0 auto;
}
</style>
