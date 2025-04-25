import './style.css'
import ElementPlus from 'element-plus'
import "element-plus/dist/index.css"

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from '@/router'


const pinia = createPinia() // 创建 Pinia 实例
const app=createApp(App);
app.use(router);
app.use(ElementPlus);
app.use(pinia);
app.mount('#app');
