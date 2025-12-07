<template>
  <div v-if="visible" class="dialog-overlay" @click="handleOverlayClick">
    <div class="dialog-container" @click.stop>
      <div class="dialog-header">
        <h2>{{ isLogin ? '登录' : '注册' }}</h2>
        <button class="close-btn" @click="close">×</button>
      </div>
      
      <div class="dialog-body">
        <form @submit.prevent="handleSubmit">
          <div v-if="!isLogin" class="form-item">
            <label>昵称</label>
            <input 
              v-model="form.nickname" 
              type="text" 
              placeholder="请输入昵称（可选）"
              class="form-input"
            />
          </div>
          
          <div v-if="!isLogin" class="form-item">
            <label>邮箱</label>
            <input 
              v-model="form.email" 
              type="email" 
              placeholder="请输入邮箱（可选）"
              class="form-input"
            />
          </div>
          
          <div class="form-item">
            <label>用户名</label>
            <input 
              v-model="form.username" 
              type="text" 
              placeholder="请输入用户名"
              required
              class="form-input"
            />
          </div>
          
          <div class="form-item">
            <label>密码</label>
            <input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码"
              required
              class="form-input"
            />
          </div>
          
          <div v-if="error" class="error-message">
            {{ error }}
          </div>
          
          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '处理中...' : (isLogin ? '登录' : '注册') }}
          </button>
        </form>
        
        <div class="dialog-footer">
          <span @click="toggleMode" class="toggle-link">
            {{ isLogin ? '还没有账号？立即注册' : '已有账号？立即登录' }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { login, register } from '../api/auth'
import { useUserStore } from '../store/user'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  defaultMode: {
    type: String,
    default: 'login' // 'login' 或 'register'
  }
})

const emit = defineEmits(['update:visible', 'success'])

const isLogin = ref(props.defaultMode === 'login')
const loading = ref(false)
const error = ref('')

const form = reactive({
  username: '',
  password: '',
  email: '',
  nickname: ''
})

const { setUser } = useUserStore()

// 监听 visible 变化，重置表单
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetForm()
    isLogin.value = props.defaultMode === 'login'
  }
})

const resetForm = () => {
  form.username = ''
  form.password = ''
  form.email = ''
  form.nickname = ''
  error.value = ''
  loading.value = false
}

const toggleMode = () => {
  isLogin.value = !isLogin.value
  resetForm()
}

const close = () => {
  emit('update:visible', false)
}

const handleOverlayClick = () => {
  close()
}

const handleSubmit = async () => {
  error.value = ''
  loading.value = true

  try {
    if (isLogin.value) {
      // 登录
      const response = await login({
        username: form.username,
        password: form.password
      })
      
      if (response.code === 200) {
        setUser(response.data)
        emit('success', response.data)
        close()
      } else {
        error.value = response.message || '登录失败'
      }
    } else {
      // 注册
      const response = await register({
        username: form.username,
        password: form.password,
        email: form.email || undefined,
        nickname: form.nickname || undefined
      })
      
      if (response.code === 200) {
        // 注册成功后自动登录
        const loginResponse = await login({
          username: form.username,
          password: form.password
        })
        
        if (loginResponse.code === 200) {
          setUser(loginResponse.data)
          emit('success', loginResponse.data)
          close()
        } else {
          error.value = '注册成功，请手动登录'
        }
      } else {
        error.value = response.message || '注册失败'
      }
    }
  } catch (err) {
    error.value = err.message || '操作失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-container {
  background: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.dialog-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #252933;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: #8a919f;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s;
}

.close-btn:hover {
  background-color: #f5f5f5;
  color: #252933;
}

.dialog-body {
  padding: 24px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #515767;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-input:focus {
  border-color: #1e80ff;
}

.error-message {
  color: #f56565;
  font-size: 13px;
  margin-bottom: 16px;
  padding: 8px 12px;
  background-color: #fff5f5;
  border-radius: 4px;
  border-left: 3px solid #f56565;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background-color: #1e80ff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 8px;
}

.submit-btn:hover:not(:disabled) {
  background-color: #1171ee;
}

.submit-btn:disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
}

.dialog-footer {
  margin-top: 20px;
  text-align: center;
}

.toggle-link {
  color: #1e80ff;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s;
}

.toggle-link:hover {
  color: #1171ee;
  text-decoration: underline;
}
</style>













