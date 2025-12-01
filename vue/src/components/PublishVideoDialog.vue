<template>
  <div v-if="visible" class="dialog-overlay">
    <div class="dialog-container" @click.stop>
      <div class="dialog-header">
        <h2>发布视频</h2>
      </div>
      
      <div class="dialog-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-row">
            <div class="form-item form-item-title">
              <label>视频标题 <span class="required">*</span></label>
              <input 
                v-model="form.title" 
                type="text" 
                placeholder="请输入视频标题"
                required
                maxlength="100"
                class="form-input"
              />
              <span class="char-count">{{ form.title.length }}/100</span>
            </div>
            
            <div class="form-item form-item-summary">
              <label>视频简介</label>
              <textarea 
                v-model="form.summary" 
                placeholder="请输入视频简介（可选）"
                rows="3"
                maxlength="200"
                class="form-textarea"
              ></textarea>
              <span class="char-count">{{ form.summary.length }}/200</span>
            </div>
            
            <div class="form-item form-item-tags">
              <label>视频标签</label>
              <div class="tags-input">
                <input 
                  v-model="tagInput" 
                  type="text" 
                  placeholder="输入标签后按回车添加"
                  @keyup.enter="addTag"
                  class="form-input"
                />
                <div class="tags-list">
                  <span 
                    v-for="(tag, index) in form.tags" 
                    :key="index"
                    class="tag-item"
                  >
                    {{ tag }}
                    <span class="tag-remove" @click="removeTag(index)">×</span>
                  </span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="form-item form-item-video">
            <label>视频文件 <span class="required">*</span></label>
            <div class="video-upload-area">
              <input 
                ref="fileInputRef"
                type="file" 
                accept="video/*"
                @change="handleFileChange"
                class="file-input"
                style="display: none"
              />
              <div 
                v-if="!videoFile && !uploadingVideo"
                class="upload-placeholder"
                @click="triggerFileInput"
              >
                <div class="upload-icon">📹</div>
                <div class="upload-text">点击选择视频文件</div>
                <div class="upload-hint">支持 mp4, webm, ogg 格式，最大 100MB</div>
              </div>
              <div 
                v-else-if="uploadingVideo"
                class="upload-progress"
              >
                <div class="progress-text">上传中... {{ uploadProgress }}%</div>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
                </div>
              </div>
              <div 
                v-else-if="videoFile && videoUrl"
                class="video-preview"
              >
                <video 
                  :src="videoUrl" 
                  controls
                  class="preview-video"
                ></video>
                <div class="video-info">
                  <div class="video-name">{{ videoFile.name }}</div>
                  <div class="video-size">{{ formatFileSize(videoFile.size) }}</div>
                  <button 
                    type="button"
                    class="change-video-btn"
                    @click="triggerFileInput"
                  >
                    更换视频
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="error" class="error-message">
            {{ error }}
          </div>
          
          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="close">取消</button>
            <button type="submit" class="submit-btn" :disabled="loading || uploadingVideo || !videoUrl">
              {{ loading ? '发布中...' : '发布视频' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { publishArticle } from '../api/article'
import { uploadVideo } from '../api/file'
import { useUserStore } from '../store/user'

const { userInfo } = useUserStore()

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  menuId: {
    type: Number,
    default: null
  },
  tabCode: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:visible', 'success'])

const loading = ref(false)
const error = ref('')
const tagInput = ref('')
const fileInputRef = ref(null)
const videoFile = ref(null)
const videoUrl = ref('')
const uploadingVideo = ref(false)
const uploadProgress = ref(0)

const form = reactive({
  title: '',
  summary: '',
  content: '',
  tags: []
})

// 监听 visible 变化，重置表单
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetForm()
  }
})

const resetForm = () => {
  form.title = ''
  form.summary = ''
  form.content = ''
  form.tags = []
  tagInput.value = ''
  error.value = ''
  loading.value = false
  videoFile.value = null
  videoUrl.value = ''
  uploadingVideo.value = false
  uploadProgress.value = 0
}

const close = () => {
  emit('update:visible', false)
}

const triggerFileInput = () => {
  fileInputRef.value.click()
}

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('video/')) {
    error.value = '请选择视频文件'
    return
  }

  // 验证文件大小（100MB）
  if (file.size > 100 * 1024 * 1024) {
    error.value = '视频文件大小不能超过100MB'
    return
  }

  error.value = ''
  videoFile.value = file

  // 创建本地预览URL
  videoUrl.value = URL.createObjectURL(file)

  // 上传视频
  uploadingVideo.value = true
  uploadProgress.value = 0

  try {
    // 注意：这里使用fetch API来支持上传进度
    const formData = new FormData()
    formData.append('file', file)

    const xhr = new XMLHttpRequest()
    
    xhr.upload.addEventListener('progress', (e) => {
      if (e.lengthComputable) {
        uploadProgress.value = Math.round((e.loaded / e.total) * 100)
      }
    })

    const uploadPromise = new Promise((resolve, reject) => {
      xhr.addEventListener('load', () => {
        if (xhr.status === 200) {
          try {
            const data = JSON.parse(xhr.responseText)
            if (data.code === 200 && data.data) {
              resolve(data.data)
            } else {
              reject(new Error(data.message || '上传失败'))
            }
          } catch (e) {
            reject(new Error('解析响应失败'))
          }
        } else {
          reject(new Error('上传失败'))
        }
      })

      xhr.addEventListener('error', () => {
        reject(new Error('上传失败'))
      })

      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
      xhr.open('POST', `${apiBaseUrl}/file/upload/video`)
      xhr.send(formData)
    })

    const url = await uploadPromise
    videoUrl.value = url
    form.content = `<video controls style="max-width: 100%;"><source src="${url}" type="${file.type}"></video>`
  } catch (err) {
    console.error('视频上传失败:', err)
    error.value = err.message || '视频上传失败，请重试'
    videoFile.value = null
    videoUrl.value = ''
  } finally {
    uploadingVideo.value = false
    uploadProgress.value = 0
  }
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

const addTag = () => {
  const tag = tagInput.value.trim()
  if (tag && !form.tags.includes(tag) && form.tags.length < 5) {
    form.tags.push(tag)
    tagInput.value = ''
  }
}

const removeTag = (index) => {
  form.tags.splice(index, 1)
}

const handleSubmit = async () => {
  error.value = ''
  
  if (!form.title.trim()) {
    error.value = '请输入视频标题'
    return
  }
  
  if (!videoUrl.value) {
    error.value = '请上传视频文件'
    return
  }
  
  loading.value = true

  try {
    const response = await publishArticle({
      title: form.title,
      summary: form.summary,
      content: form.content,
      tags: form.tags,
      userId: userInfo.value ? userInfo.value.id : null,
      menuId: props.menuId,
      tabCode: props.tabCode
    })
    
    if (response.code === 200) {
      emit('success', response.data)
      close()
    } else {
      error.value = response.message || '发布失败'
    }
  } catch (err) {
    error.value = err.message || '发布失败，请稍后重试'
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
  background-color: #fff;
  display: flex;
  align-items: stretch;
  justify-content: stretch;
  z-index: 1000;
}

.dialog-container {
  background: #fff;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
  width: 100%;
  box-sizing: border-box;
}

.dialog-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #252933;
}

.dialog-body {
  padding: 40px;
  overflow-y: auto;
  flex: 1;
  width: 100%;
  box-sizing: border-box;
}

.dialog-body form {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  width: 100%;
}

.form-item {
  margin-bottom: 24px;
  width: 100%;
}

.form-item-title {
  flex: 1;
  margin-bottom: 0;
  min-width: 200px;
}

.form-item-summary {
  flex: 1;
  margin-bottom: 0;
  min-width: 200px;
}

.form-item-tags {
  flex: 1;
  margin-bottom: 0;
  min-width: 200px;
}

.form-item-video {
  flex: 1;
  width: 100%;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #515767;
  font-weight: 500;
}

.required {
  color: #f56565;
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

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
  resize: vertical;
  font-family: inherit;
}

.form-textarea:focus {
  border-color: #1e80ff;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 12px;
  color: #8a919f;
  margin-top: 4px;
}

.tags-input {
  position: relative;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  background-color: #f2f3f5;
  color: #515767;
  border-radius: 4px;
  font-size: 13px;
}

.tag-remove {
  cursor: pointer;
  color: #8a919f;
  font-size: 16px;
  line-height: 1;
  transition: color 0.3s;
}

.tag-remove:hover {
  color: #f56565;
}

.video-upload-area {
  width: 100%;
  min-height: 300px;
  border: 2px dashed #e5e5e5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.upload-placeholder {
  text-align: center;
  padding: 40px;
  cursor: pointer;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.video-upload-area:hover .upload-placeholder {
  border-color: #1e80ff;
  background-color: #f7f8fa;
}

.upload-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.upload-text {
  font-size: 16px;
  color: #515767;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 12px;
  color: #8a919f;
}

.upload-progress {
  width: 100%;
  padding: 40px;
  text-align: center;
}

.progress-text {
  font-size: 14px;
  color: #515767;
  margin-bottom: 16px;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #1e80ff;
  transition: width 0.3s;
}

.video-preview {
  width: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.preview-video {
  width: 100%;
  max-height: 400px;
  border-radius: 8px;
  background-color: #000;
}

.video-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.video-name {
  flex: 1;
  font-size: 14px;
  color: #515767;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-size {
  font-size: 12px;
  color: #8a919f;
}

.change-video-btn {
  padding: 8px 16px;
  background-color: #fff;
  color: #515767;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.change-video-btn:hover {
  border-color: #1e80ff;
  color: #1e80ff;
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

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn {
  padding: 10px 24px;
  background-color: #fff;
  color: #515767;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn:hover {
  background-color: #f5f5f5;
  border-color: #d0d0d0;
}

.submit-btn {
  padding: 10px 24px;
  background-color: #1e80ff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #1171ee;
}

.submit-btn:disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
}
</style>
