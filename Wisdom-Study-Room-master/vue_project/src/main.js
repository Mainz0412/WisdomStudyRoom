import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus, { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import 'element-plus/dist/index.css'
import axios from "axios";

const app = createApp(App)
app.config.globalProperties.$axios = axios    //全局注册，使用方法为:this.$axios
axios.defaults.baseURL = '/api';

// 捕获并忽略浏览器中常见的 ResizeObserver 循环错误，避免 dev overlay 弹窗影响用户
window.addEventListener('error', (event) => {
  try {
    const msg = event && event.message ? event.message : '';
    if (msg && msg.indexOf('ResizeObserver loop completed with undelivered notifications') !== -1) {
      event.stopImmediatePropagation && event.stopImmediatePropagation();
      event.preventDefault && event.preventDefault();
      return false;
    }
  } catch (e) {
    // 忽略处理中的任何异常
  }
});

window.addEventListener('unhandledrejection', (ev) => {
  // 记录未处理的 promise rejection，但不要让开发覆盖弹窗阻塞页面
  console.warn('Unhandled promise rejection:', ev && ev.reason);
});

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


