import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus, { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import 'element-plus/dist/index.css'
import axios from "axios";

const app = createApp(App)
app.config.globalProperties.$axios = axios    //全局注册，使用方法为:this.$axios
axios.defaults.baseURL = '/api';

// request interceptor
axios.interceptors.request.use(
  config => {
    let token = sessionStorage.getItem("token");
    if (token) {
      config.headers['token'] = token;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
)

app.use(router)
app.use(ElementPlus)

app.config.globalProperties.$message = {
  success: (msg) => ElMessage({ message: msg, type: 'success' }),
  error: (msg) => ElMessage({ message: msg, type: 'error' }),
  info: (msg) => ElMessage({ message: msg, type: 'info' }),
  warning: (msg) => ElMessage({ message: msg, type: 'warning' }),
  open: (opts) => ElMessage(opts)
}
app.config.globalProperties.$confirm = (msg, title, opts) => ElMessageBox.confirm(msg, title, opts)
app.config.globalProperties.$alert = (msg, title, opts) => ElMessageBox.alert(msg, title, opts)
app.config.globalProperties.$notify = (opts) => ElNotification(opts)

import * as ElementPlusIconsVue from '@element-plus/icons-vue'
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')


