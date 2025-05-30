<template>
    <el-container class="register-container">
        <el-header class="register-header">
        <h1 class="register-subtitle">加入智慧零工平台，开启灵活工作新模式</h1>
      </el-header>
      <div class="register-main">
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="rules"
          label-width="120px"
          class="register-form"
          status-icon
        >
          <el-form-item label="用户名" prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请输入用户名"
              prefix-icon="User"
            ></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input 
              :type="passwordVisible ? 'text' : 'password'" 
              v-model="registerForm.password" 
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              :type="passwordVisible ? 'text' : 'password'" 
              v-model="registerForm.confirmPassword" 
              placeholder="请确认密码"
              prefix-icon="Lock"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item label="手机号码" prop="contactPhone">
            <el-input 
              v-model="registerForm.contactPhone" 
              placeholder="请输入手机号码"
              prefix-icon="contactPhone"
            ></el-input>
          </el-form-item>
          <el-form-item label="电子邮件" prop="contactEmail">
            <el-input 
              v-model="registerForm.contactEmail" 
              placeholder="请输入电子邮件"
              prefix-icon="Message"
            ></el-input>
          </el-form-item>
          <el-form-item label="所在地" prop="location">
            <el-input 
              v-model="registerForm.location" 
              placeholder="请输入所在地"
              prefix-icon="Message"
            ></el-input>
          </el-form-item>
          <el-form :model="skillsForm" ref="skillsFormRef" label-width="120px">
            <el-form-item label="可工作时间" prop="availableTime">
              <el-time-select
                v-model="registerForm.workTimeBegin"
                start="08:00"
                step="00:30"
                end="24:00"
                placeholder="开始时间"
                style="width: 48%; margin-right: 4%"
              ></el-time-select>
              <el-time-select
                v-model="registerForm.workTimeEnd"
                start="08:00"
                step="00:30"
                end="24:00"
                placeholder="结束时间"
                style="width: 48%"
              ></el-time-select>
            </el-form-item>
            <el-form-item label="工作日" prop="workDays">
              <el-checkbox-group v-model="registerForm.workDay">
                <el-checkbox label="1">周一</el-checkbox>
                <el-checkbox label="2">周二</el-checkbox>
                <el-checkbox label="3">周三</el-checkbox>
                <el-checkbox label="4">周四</el-checkbox>
                <el-checkbox label="5">周五</el-checkbox>
                <el-checkbox label="6">周六</el-checkbox>
                <el-checkbox label="0">周日</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-form>
          <el-form-item label="技能标签" prop="skills">
            <el-select
              v-model="registerForm.skills"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder="请选择或添加您的技能标签"
              style="width: 100%"
            >
              <el-option
                v-for="item in skillOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="验证码" prop="captcha">
            <div class="captcha-container">
              <el-input
                v-model="registerForm.captcha"
                placeholder="请输入验证码"
                style="width: 50%"
              ></el-input>
              <div class="captcha-image" @click="refreshCaptcha">
                <img :src="captchaImage" alt="验证码" />
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="agreement" class="agreement-item">
            <el-checkbox v-model="registerForm.agreement">我已阅读并同意</el-checkbox>
            <el-link type="primary" @click="showAgreement">《用户协议》</el-link>
            <span>和</span>
            <el-link type="primary" @click="showPrivacy">《隐私政策》</el-link>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm">注册</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-container>
  </template>
  
<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { ElMessage, ElMessageBox } from "element-plus";
import { computedAsync } from "@vueuse/core";
import { useJobStore } from "../../store/job";

const router = useRouter();
const userStore = useUserStore();
const registerFormRef = ref(null);
const captchaImage = ref("");
const passwordVisible = ref(false);
const loading = ref(false);

const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  contactPhone: "",
  contactEmail: "",
  skills: [],
  captcha: "",
  captchaKey: "",
  agreement: false,
  role: 1, // 1表示零工用户
  location: "",
  workTimeBegin: "",
  workTimeEnd: "",
  workDay: [] // 1-7 表示周一到周日
});

// 技能选项
const jobStore = useJobStore();
const skillOptions = computedAsync(async () => {
  const skills = await jobStore.getSkillLabels();
  return skills.map((skill) => ({
    value: skill.id,
    label: skill.name,
  }));
})

// 表单验证规则
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
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.value.password) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  contactPhone: [
    { required: true, message: "请输入手机号码", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码格式",
      trigger: "blur",
    },
  ],
  contactEmail: [
    { required: true, message: "请输入电子邮件", trigger: "blur" },
    {
      type: "contactEmail",
      message: "请输入正确的电子邮件格式",
      trigger: "blur",
    },
  ],
  skills: [
    { required: true, message: "请至少选择一项技能", trigger: "change" }
  ],
  captcha: [
    { required: true, message: "请输入验证码", trigger: "blur" }
  ],
  agreement: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error("请阅读并同意用户协议和隐私政策"));
        } else {
          callback();
        }
      },
      trigger: "change",
    },
  ],
  location: [
    { required: true, message: "请输入所在地", trigger: "blur" }
  ]
};

// 刷新验证码
const refreshCaptcha = async () => {
  try {
    await userStore.captcha();
    captchaImage.value = userStore.captchaImg;
  } catch (error) {
    ElMessage.error("获取验证码失败，请稍后重试");
  }
};

// 显示用户协议
const showAgreement = () => {
  ElMessageBox.alert(
    "智慧零工平台用户协议内容...",
    "用户协议",
    {
      confirmButtonText: "我已阅读",
      callback: () => {}
    }
  );
};

// 显示隐私政策
const showPrivacy = () => {
  ElMessageBox.alert(
    "智慧零工平台隐私政策内容...",
    "隐私政策",
    {
      confirmButtonText: "我已阅读",
      callback: () => {}
    }
  );
};

// 提交表单
const submitForm = async () => {
  if (!registerFormRef.value) return;
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;
        registerForm.value.captchaKey = userStore.captchaKey;
        registerForm.value.skills = registerForm.value.skills;
        await userStore.workerRegister(registerForm.value);
    setTimeout(() => {
      router.push("/login");
    }, 1500);
      } catch (error) {
        ElMessage.error(error.message || "注册失败，请稍后重试");
        refreshCaptcha();
      } finally {
        loading.value = false;
      }
    } else {
      ElMessage.warning("请正确填写所有必填项");
      return false;
    }
  });
};

// 重置表单
const resetForm = () => {
  registerFormRef.value.resetFields();
  refreshCaptcha();
};

onMounted(async () => {
  await refreshCaptcha();
});
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  background: url("@/assets/backround.png") no-repeat center / cover;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 40px;
}

.register-header {
  text-align: center;
  margin: 30px 0;
  color: #fff;
  
  h1 {
    font-size: 28px;
    margin-bottom: 10px;
  }
  
  .register-subtitle {
    font-size: 16px;
    opacity: 0.9;
  }
}

.register-main {
  width: 100%;
  max-width: 550px;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-form {
  margin-left: 40px;
  margin-top: 20px;
  width: 70%;
}

.captcha-container {
  display: flex;
  align-items: center;
  
  .captcha-image {
    margin-left: 25px;
    cursor: pointer;
    height: 40px;
    border-radius: 4px;
    overflow: hidden;
    img {
      height: 100%;
    }
  }
}

.agreement-item {
  margin-left: -60px;
  display: flex;
  align-items: center;
  :deep(.el-form-item__content) {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    line-height: 1.5;
  }
  
  .el-link {
    margin: 0 5px;
  }
}

@media (max-width: 768px) {
  .register-main {
    max-width: 90%;
    padding: 20px;
  }
  
  .register-form {
    :deep(.el-form-item__label) {
      float: none;
      display: block;
      text-align: left;
      padding: 0 0 10px;
    }
    
    :deep(.el-form-item__content) {
      margin-left: 0 !important;
    }
  }
}

.el-button {
        display: flex;
        align-items: center;
        justify-content: center;
        width: auto;
        margin: 0 auto;
}
  
  .mb-4 {
    margin-top: 10px;
    background-color: #fff;
    width: 400px;
    .el-step {
        width: 300x;
      }
    }
  
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

  </style>