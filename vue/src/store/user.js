import { ref, reactive } from 'vue'

// 用户信息
const userInfo = ref(null)

// 是否已登录
const isLoggedIn = ref(false)

// 从 localStorage 恢复用户信息
const initUser = () => {
  const savedUser = localStorage.getItem('userInfo')
  if (savedUser) {
    try {
      userInfo.value = JSON.parse(savedUser)
      isLoggedIn.value = true
    } catch (e) {
      console.error('恢复用户信息失败:', e)
      localStorage.removeItem('userInfo')
    }
  }
}

// 设置用户信息
const setUser = (user) => {
  userInfo.value = user
  isLoggedIn.value = true
  localStorage.setItem('userInfo', JSON.stringify(user))
}

// 清除用户信息
const clearUser = () => {
  userInfo.value = null
  isLoggedIn.value = false
  localStorage.removeItem('userInfo')
}

// 初始化
initUser()

export function useUserStore() {
  return {
    userInfo,
    isLoggedIn,
    setUser,
    clearUser
  }
}












