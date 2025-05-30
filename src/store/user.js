import { defineStore } from 'pinia';
import { ref } from 'vue';
import { loginApi, captchaApi,updateUserCertificatesApi, enterpriseRegisterApi, logoutApi, workerRegisterApi ,updateUserInfoApi } from '@/api/api';
import { ElMessage } from 'element-plus';
import { deleteUserCertificatesApi, getUserAvatarApi, getUserInfoApi, updateEnterpriseApi, updateWorkerApi } from '../api/api';
export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null);
  const token = ref('');
  const captchaImg = ref('');
  const captchaKey = ref('');
  const userAvatar = ref('');
  const setToken = (newToken) => {
    token.value = newToken;
    localStorage.setItem('token', newToken);
  };
  const setWorkerInfo = (data) => {
    console.log(data)
    userInfo.value.worker =data
  }
  const setUserInfo = (newUserInfo) => {
    userInfo.value = newUserInfo;
    localStorage.setItem('userId', userInfo.value.id);
  }
  const getUserInfo = async () => {
    const response = await getUserInfoApi();
    userInfo.value = response.data.data;
    localStorage.setItem('userId', userInfo.value.id);

    return response.data.data;
  };
  const setAvatar = (newAvatar) => {
    userAvatar.value = newAvatar;
  };
  const updateUserInfo = async (newUserInfo) => {
    const res = await updateUserInfoApi(newUserInfo);
    userInfo.value = res.data.data;
  };
  const captcha= async () => {
    const {data:{data}} = await captchaApi();
    captchaImg.value = data.captchaImg;
    captchaKey.value = data.captchaKey;
  };
  const login = async (credentials) => {
    const resp = await loginApi(credentials)
    token.value =  resp.data.data.token;
    await getUserInfo();
    localStorage.setItem('token', token.value);
    return resp;
  };
  const getUserAvatar = async (userId) => {
    const response = await getUserAvatarApi(userId);
    return response.data.data;
  };
  const logout = async () => {
    userInfo.value = null;
    token.value = '';
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    await logoutApi();
  };
  const enterpriseRegister = async (userData) => {
    const response = await enterpriseRegisterApi(userData);
    if (response.data.code === 200) {
      ElMessage.success('注册成功');
    }
  };
  const workerRegister = async (userData) => {
    const response = await workerRegisterApi(userData);
    if (response.data.code === 200) {
      ElMessage.success('注册成功');
    }
   
  };
  const updateProfile = async (profileData) => {
   await updateProfileApi(profileData);
  };
  const updateWorker = async (profileData) => {
    await updateWorkerApi(profileData);
  };
  const addUserCertificate = async (profileData) => {
     await updateUserCertificatesApi(profileData);
  };
  const deleteUserCertificate = async (profileData) => {
     await deleteUserCertificatesApi(profileData);
  };
  const updateEnterprise = async (profileData) => {
    await updateEnterpriseApi(profileData);
  };
  return {
    userInfo,
    token,
    captchaImg,
    captchaKey,
    setWorkerInfo,
    addUserCertificate,
    deleteUserCertificate,
    getUserInfo,
    updateWorker,
    updateEnterprise,
    getUserAvatar,
    captcha,
    setToken,
    setUserInfo,
    updateUserInfo,
    setAvatar,
    login,
    logout,
    enterpriseRegister,
    workerRegister,
    updateProfile,
    userAvatar,
  };
});