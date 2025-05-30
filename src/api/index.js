import axios from 'axios'
import { useUserStore } from '../store/user';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
const baseURL = '/api'
const api = axios.create({
    baseURL,
    timeout: 10000
  });

api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = userStore.token
    }
    // Initialize config.data if it doesn't exist
    if (!config.data) {
      config.data = {};
    }
    config.data.token = userStore.token;
    // Check if userInfo exists before accessing userId
    if (userStore.userInfo) {
      config.data.userId = userStore.userInfo.id;
    }
    return config
  },
  (err) => Promise.reject(err)
)
const router = useRouter()
api.interceptors.response.use(
 async (res) => {
    if (res.data.code === 200) {
      ElMessage.success(res.data.message)
    }else{
      if(res.data.message){
        ElMessage.error(res.data.message)
      }
      if(res.data.code === 401){
        const userStore = useUserStore()
        await userStore.logout()
        router.push('/login')
      }
    }
    return res
  },
  (err) => {
    return Promise.reject(err)
  }
)
export default api

export {baseURL}
