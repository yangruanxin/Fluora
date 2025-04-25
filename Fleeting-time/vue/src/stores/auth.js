// Pinia身份验证状态管理store
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(false)
  const token = ref('')

  function login(authToken) {
    token.value = authToken
    isLoggedIn.value = true
    localStorage.setItem('token', authToken)
  }

  function logout() {
    token.value = ''
    isLoggedIn.value = false
    localStorage.removeItem('token')
  }

  function checkAuth() {
    const storedToken = localStorage.getItem('token')
    if (storedToken) {
      token.value = storedToken
      isLoggedIn.value = true
    }
  }

  return { isLoggedIn, token, login, logout, checkAuth }
})