<template>
  <div class="web-content-reader">
    <div class="container">
      <div class="header">
        <h1>网页内容阅读器</h1>
        <div class="url-input-section">
          <input
            v-model="url"
            type="text"
            placeholder="请输入网页 URL（例如：https://www.livesinabox.com/friends/season1/101pilot.htm）"
            class="url-input"
            @keyup.enter="fetchContent"
          />
          <button 
            @click="fetchContent" 
            :disabled="loading"
            class="fetch-btn"
          >
            {{ loading ? '加载中...' : '获取内容' }}
          </button>
        </div>
        <div v-if="error" class="error-message">{{ error }}</div>
      </div>

      <div v-if="content" class="content-display">
        <div class="content-text" :style="{ fontSize: fontSize + 'px' }">
          <pre>{{ content }}</pre>
        </div>
        <div class="controls">
          <label>
            字体大小：
            <input 
              type="range" 
              v-model.number="fontSize" 
              min="16" 
              max="72" 
              step="2"
              class="font-size-slider"
            />
            <span>{{ fontSize }}px</span>
          </label>
          <button @click="copyContent" class="copy-btn">复制内容</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getWebContent } from '../api/webContent'

const url = ref('')
const content = ref('')
const loading = ref(false)
const error = ref('')
const fontSize = ref(32)

const fetchContent = async () => {
  if (!url.value || !url.value.trim()) {
    error.value = '请输入 URL'
    return
  }

  loading.value = true
  error.value = ''
  content.value = ''

  try {
    const response = await getWebContent(url.value.trim())
    if (response.code === 200 && response.data) {
      content.value = response.data
    } else {
      error.value = response.message || '获取内容失败'
    }
  } catch (err) {
    error.value = err.message || '获取内容失败'
    console.error('获取网页内容失败:', err)
  } finally {
    loading.value = false
  }
}

const copyContent = () => {
  if (content.value) {
    navigator.clipboard.writeText(content.value).then(() => {
      alert('内容已复制到剪贴板')
    }).catch(err => {
      console.error('复制失败:', err)
      alert('复制失败')
    })
  }
}
</script>

<style scoped>
.web-content-reader {
  min-height: 100vh;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.container {
  width: 100%;
  height: 100%;
  margin: 0;
  background: white;
  border-radius: 0;
  box-shadow: none;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 20px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  flex-shrink: 0;
}

.header h1 {
  margin: 0 0 15px 0;
  font-size: 24px;
  font-weight: 600;
}

.url-input-section {
  display: flex;
  gap: 12px;
  margin-bottom: 15px;
}

.url-input {
  flex: 1;
  padding: 14px 18px;
  font-size: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.95);
  color: #333;
  transition: all 0.3s;
}

.url-input:focus {
  outline: none;
  border-color: white;
  background: white;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.3);
}

.fetch-btn {
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
  background: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.fetch-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.fetch-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  padding: 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  color: #ffebee;
  font-size: 14px;
}

.content-display {
  padding: 20px 30px;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.content-text {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 40px;
  margin-bottom: 20px;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  line-height: 1.8;
  font-family: 'Georgia', 'Times New Roman', serif;
  color: #2c3e50;
  min-height: 0;
}

.content-text pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
  font-size: inherit;
  line-height: inherit;
}

.controls {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  flex-wrap: wrap;
  flex-shrink: 0;
}

.controls label {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  color: #555;
  font-weight: 500;
}

.font-size-slider {
  width: 200px;
  height: 6px;
  border-radius: 3px;
  background: #ddd;
  outline: none;
  -webkit-appearance: none;
}

.font-size-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #667eea;
  cursor: pointer;
}

.font-size-slider::-moz-range-thumb {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #667eea;
  cursor: pointer;
  border: none;
}

.copy-btn {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  background: #667eea;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.copy-btn:hover {
  background: #5568d3;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

/* 滚动条样式 */
.content-text::-webkit-scrollbar {
  width: 10px;
}

.content-text::-webkit-scrollbar-track {
  background: #e0e0e0;
  border-radius: 5px;
}

.content-text::-webkit-scrollbar-thumb {
  background: #667eea;
  border-radius: 5px;
}

.content-text::-webkit-scrollbar-thumb:hover {
  background: #5568d3;
}
</style>

