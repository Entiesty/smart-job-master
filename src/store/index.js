import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useStore = defineStore('global', ()=>{
  const pageForm = ref({
    current: 1,
    size: 5,
    total: 20,
  })
  const setPageForm = (data) => {
    pageForm.value = data;
  }
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
};
  return {
    pageForm,
    setPageForm,
    rules
  }
});