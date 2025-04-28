// utils/request.js
import axios from 'axios'

// 创建不带 token 的 Axios 实例：用于登录、注册等公共接口
export const publicAxios = axios.create({
  baseURL: 'http://121.43.136.251:8080/api',
  headers: {
    'Content-Type': 'application/json',
    withCredentials: false
  }
})

publicAxios.interceptors.request.use(config => {
    console.log('Axios 发出请求 config：', config)
    return config
})

// 创建带 token 的 Axios 实例：用于需要认证的业务接口
export const authAxios = axios.create({
  baseURL: 'http://121.43.136.251:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 添加请求拦截器 —— 自动加 token
authAxios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token') // 或者从 Vuex 中获取
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器—— 统一处理 token 失效等错误
authAxios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response && error.response.status === 401) {
      console.warn('登录已过期，请重新登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
