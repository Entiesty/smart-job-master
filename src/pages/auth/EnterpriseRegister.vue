<template>
  <el-container class="register-container">
    <el-header class="register-header">
      <h1>企业注册</h1>
    </el-header>
    <div class="register-main">
      <el-steps
        class="mb-4"
        :active="activeStep"
        style="max-width: 600px"
        finish-status="success"
        align-center
      >
        <el-step title="创建申请" />
        <el-step title="填写商户资料" />
        <el-step title="确认提交" />
      </el-steps>
      <div v-if="activeStep === 1" class="form-container">
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="rules"
          label-width="120px"
          class="register-form"
        >
          <el-form-item  prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
            ></el-input>
          </el-form-item>
          <el-form-item  prop="password">
            <el-input
              type="password"
              v-model="registerForm.password"
              placeholder="请输入密码"
            ></el-input>
          </el-form-item>
          <el-form-item  prop="confirmPassword">
            <el-input
              type="password"
              v-model="registerForm.confirmPassword"
              placeholder="请确认密码"
            ></el-input>
          </el-form-item>
          <el-form-item prop="captcha">
            <el-input
              v-model="registerForm.captcha"
              placeholder="请输入验证码"
              style="width: 60%"
            ></el-input>
            <img :src="captchaImage" @click="refreshCaptcha" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="nextStep">申请注册</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="activeStep === 2" class="form-container">
        <el-form
          ref="enterpriseFormRef"
          :model="enterpriseForm"
          :rules="enterpriseRules"
          label-width="120px"
          class="register-form"
        >
          <el-form-item  prop="companyName">
            <el-input
              v-model="enterpriseForm.companyName"
              placeholder="请输入企业名称"
            ></el-input>
          </el-form-item>
          <el-form-item  prop="creditCode">
            <el-input
              v-model="enterpriseForm.creditCode"
              placeholder="请输入统一社会信用代码"
            ></el-input>
          </el-form-item>
          <el-form-item  prop="businessLicense">
            <el-input
              v-model="enterpriseForm.businessLicense"
              placeholder="请输入营业执照号"
            ></el-input>
          </el-form-item>
          <el-form-item  prop="companyType">
            <el-select
              v-model="enterpriseForm.companyType"
              placeholder="请选择企业类型"
            >
              <el-option
                v-for="item in companyTypes"
                :key="item.value"
            
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="contactName">
            <el-input
              v-model="enterpriseForm.contactName"
              placeholder="请输入联系人姓名"
            ></el-input>
          </el-form-item>
          <el-form-item  prop="contactPhone">
            <el-input
              v-model="enterpriseForm.contactPhone"
              placeholder="请输入联系人电话"
            ></el-input>
          </el-form-item>
          <el-form-item prop="contactEmail">
            <el-input
              v-model="enterpriseForm.contactEmail"
              placeholder="请输入联系人邮箱"
            ></el-input>
          </el-form-item>
          <el-form-item  prop="address">
            <el-input
              type="textarea"
              v-model="enterpriseForm.address"
              placeholder="请输入企业地址"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm">确认提交</el-button>
            <el-button @click="prevStep">上一步</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </el-container>
</template>

<script setup lang="ts">
import { onMounted, ref ,} from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { verifyCaptchaApi,enterpriseRegisterApi } from "../../api/api";
import { useUserStore } from "../../store/user";
import { useStore } from "../../store";
const router = useRouter();
const activeStep = ref(1);
const registerFormRef = ref();
const enterpriseFormRef = ref();
const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  captcha: "",
  captchaKey: "",
});
const enterpriseForm = ref({
  companyName: "",
  creditCode: "",
  businessLicense: "",
  companyType: "",
  contactName: "",
  contactPhone: "",
  contactEmail: "",
  address: "",
});
const captchaImage = ref("");
const userStore = useUserStore();
const refreshCaptcha = async () => {
  await userStore.captcha();
  captchaImage.value = userStore.captchaImg;
};
const companyTypes = ref([
  { label: "有限责任公司", value: "有限责任公司" },
  { label: "股份有限公司", value: "股份有限公司" },
  { label: "个体工商户", value: "个体工商户" },
  { label: "合伙企业", value: "合伙企业" },
  { label: "个人独资企业", value: "个人独资企业" },
]);

const globalStore = useStore();
const rules = globalStore.rules;
rules.confirmPassword = [
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
]
const enterpriseRules = {
  companyName: [{ required: true, message: "请输入企业名称", trigger: "blur" }],
  creditCode: [
    { required: true, message: "请输入统一社会信用代码", trigger: "blur" },
    {
      pattern: /^[0-9A-Z]{18}$/,
      message: "请输入有效的统一社会信用代码",
      trigger: "blur",
    },
  ],
  businessLicense: [
    { required: true, message: "请输入营业执照号", trigger: "blur" },
  ],
  companyType: [
    { required: true, message: "请选择企业类型", trigger: "change" },
  ],
  contactName: [
    { required: true, message: "请输入联系人姓名", trigger: "blur" },
  ],
  contactPhone: [
    { required: true, message: "请输入联系人电话", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入有效的手机号码",
      trigger: "blur",
    },
  ],
  contactEmail: [
    { required: true, message: "请输入联系人邮箱", trigger: "blur" },
    {
      type: "email",
      message: "请输入有效的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  address: [
    { required: true, message: "请输入企业地址", trigger: "blur" },
  ],
};

const nextStep = () => {
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      if(activeStep.value==1){
        registerForm.value.captchaKey = userStore.captchaKey;
        console.log(registerForm.value.captchaKey);
        console.log(registerForm.value.captcha);
        await verifyCaptchaApi(registerForm.value.captchaKey,registerForm.value.captcha)
        .then((res)=>{
          activeStep.value++;
          console.log(res.data.code);
          if(res.data.code==200){
            ElMessage.success("验证码验证成功！");
          }else{
            ElMessage.error("验证码验证失败！");
          }
        }).catch((error)=>{
          ElMessage.error("验证码验证失败！");
        });
      }
      if (activeStep.value > 2) activeStep.value = 0;
    } else {
      ElMessage.error("表单填写有误，请检查！");
    }
  });
};

const prevStep = () => {
  activeStep.value--;
};

const submitForm = () => {
  enterpriseFormRef.value.validate(async (valid) => {
    if (valid) {
      activeStep.value++;
      const formData = {
        ...registerForm.value,
        ...enterpriseForm.value,
        role: 2,
      };
      router.push('/login');
      await userStore.enterpriseRegister(formData);
      ElMessage.success("注册成功！");
    } else {
      ElMessage.error("表单填写有误，请检查！");
    }
  });
};
onMounted(() => {
  refreshCaptcha();
});
</script>

<style scoped>
.register-container {
  height: 100vh;
  background: url("@/assets/backround.png") no-repeat center / cover;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-header {
  color: #fff;
}

.register-main {
  width: 600px;
  height: auto;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.form-container {
  width: 100%;
  max-width: 500px;
}
.register-form {
  width: 80%;
  .el-button {
    display: flex;
    align-items: center;
    justify-content: center;
    width: auto;
    margin: 0 auto;
  }
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
