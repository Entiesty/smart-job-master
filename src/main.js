import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import 'element-plus/dist/index.css';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import ElementPlus from 'element-plus';
import AsyncComputed from 'vue-async-computed'

const app = createApp(App);
app.use(AsyncComputed)

app.use(ElementPlus, {
    locale: zhCn,
});
app.use(createPinia());
app.use(router);
app.mount('#app');