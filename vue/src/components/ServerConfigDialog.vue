<template>
  <div v-if="visible" class="dialog-overlay" @click="handleOverlayClick">
    <div class="dialog-container" @click.stop>
      <div class="dialog-header">
        <h2>服务器配置</h2>
        <button class="close-btn" @click="close">×</button>
      </div>
      <div class="dialog-body">
        <div class="form-group">
          <label for="serverUrl">服务器地址：</label>
          <input
            id="serverUrl"
            v-model="serverUrl"
            type="text"
            placeholder="例如：http://192.168.1.100:8080/api"
            class="input-field"
          />
          <p class="help-text">请输入完整的服务器地址，包括协议、IP和端口</p>
        </div>
        <div class="current-config" v-if="currentConfig">
          <p><strong>当前配置：</strong>{{ currentConfig }}</p>
        </div>
      </div>
      <div class="dialog-footer">
        <button class="btn btn-secondary" @click="resetToDefault">恢复默认</button>
        <button class="btn btn-primary" @click="save">保存</button>
        <button class="btn btn-cancel" @click="close">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getServerBaseUrl, setServerBaseUrl, clearServerConfig } from '../utils/index.js'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'saved'])

const serverUrl = ref('')
const currentConfig = ref('')

// 加载当前配置
function loadCurrentConfig() {
  currentConfig.value = getServerBaseUrl()
  serverUrl.value = currentConfig.value
}

// 保存配置
function save() {
  if (!serverUrl.value.trim()) {
    alert('请输入服务器地址')
    return
  }
  
  // 简单的URL验证
  try {
    new URL(serverUrl.value)
  } catch (e) {
    alert('请输入有效的URL地址（例如：http://192.168.1.100:8080/api）')
    return
  }
  
  setServerBaseUrl(serverUrl.value.trim())
  currentConfig.value = serverUrl.value.trim()
  emit('saved')
  close()
  alert('服务器配置已保存，请刷新页面使配置生效')
}

// 恢复默认
function resetToDefault() {
  if (confirm('确定要恢复默认配置吗？')) {
    clearServerConfig()
    loadCurrentConfig()
    emit('saved')
    close()
    alert('已恢复默认配置，请刷新页面使配置生效')
  }
}

// 关闭对话框
function close() {
  emit('update:visible', false)
}

// 点击遮罩层关闭
function handleOverlayClick() {
  close()
}

// 监听visible变化
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadCurrentConfig()
  }
})

onMounted(() => {
  loadCurrentConfig()
})
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
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog-container {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.dialog-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.dialog-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #2c3e50;
}

.input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  box-sizing: border-box;
}

.input-field:focus {
  outline: none;
  border-color: #42b983;
}

.help-text {
  margin-top: 8px;
  font-size: 0.875rem;
  color: #666;
}

.current-config {
  margin-top: 20px;
  padding: 12px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.current-config p {
  margin: 0;
  color: #2c3e50;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-primary {
  background-color: #42b983;
  color: white;
}

.btn-primary:hover {
  background-color: #35a372;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
}

.btn-cancel {
  background-color: #fff;
  color: #333;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background-color: #f5f5f5;
}
</style>


