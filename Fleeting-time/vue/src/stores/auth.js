import { defineStore } from 'pinia'
import { ref, onMounted } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(false)
  const token = ref('')
  const imageIdMap = ref({})

  // 登录
  function login(authToken) {
    token.value = authToken
    isLoggedIn.value = true
    // 将 token 存储到 localStorage
    localStorage.setItem('token', authToken)
  }

  // 登出
  function logout() {
    token.value = ''
    isLoggedIn.value = false
    // 删除 localStorage 中的 token
    localStorage.removeItem('token')
  }

  // 检查是否已登录
  function checkAuth() {
    const storedToken = localStorage.getItem('token')
    if (storedToken) {
      token.value = storedToken
      isLoggedIn.value = true
    } else {
      isLoggedIn.value = false
    }
  }

  
  // imageId->url映射表
  function setImageId(url, id) {
    imageIdMap.value[url] = id
  }

  function getImageId(url) {
    return imageIdMap.value[url]
  }

  return { 
    isLoggedIn, 
    token, 
    login, 
    logout, 
    checkAuth,
    imageIdMap,
    setImageId,
    getImageId 
  }
},{
  persist: {
    paths: ['isLoggedIn', 'token', 'imageIdMap'] 
  }
}
)
