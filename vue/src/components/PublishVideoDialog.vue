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
              <label>视频描述</label>
              <textarea 
                v-model="form.summary" 
                placeholder="请输入视频描述（可选）"
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
                ref="videoInputRef"
                type="file" 
                accept="video/*"
                @change="handleVideoSelect"
                class="video-input"
                style="display: none"
              />
              <div v-if="!videoFile && !uploadingVideo" class="upload-placeholder" @click="triggerVideoSelect">
                <div class="upload-icon">📹</div>
                <div class="upload-text">点击选择视频文件</div>
                <div class="upload-hint">支持 MP4、WebM、OGG 等格式</div>
              </div>
              <div v-if="videoFile && !uploadingVideo" class="video-preview">
                <div class="video-info">
                  <div class="video-name">{{ videoFile.name }}</div>
                  <div class="video-size">{{ formatFileSize(videoFile.size) }}</div>
                </div>
                <button type="button" class="change-video-btn" @click="triggerVideoSelect">更换视频</button>
              </div>
              <div v-if="uploadingVideo" class="uploading-status">
                <div class="upload-progress">上传中... {{ uploadProgress }}%</div>
              </div>
              <div v-if="videoUrl" class="video-preview-url">
                <video :src="videoUrl" controls class="preview-video"></video>
              </div>
            </div>
          </div>
          
          <div v-if="error" class="error-message">
            {{ error }}
          </div>
          
          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="close">取消</button>
            <button type="submit" class="submit-btn" :disabled="loading || !videoUrl">
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
const videoInputRef = ref(null)
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

const triggerVideoSelect = () => {
  if (videoInputRef.value) {
    videoInputRef.value.click()
  }
}

const handleVideoSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('video/')) {
    error.value = '请选择视频文件'
    return
  }

  // 验证文件大小（限制为500MB）
  const maxSize = 500 * 1024 * 1024
  if (file.size > maxSize) {
    error.value = '视频文件大小不能超过500MB'
    return
  }

  error.value = ''
  videoFile.value = file

  // 创建预览URL
  if (videoUrl.value) {
    URL.revokeObjectURL(videoUrl.value)
  }
  videoUrl.value = URL.createObjectURL(file)

  // 上传视频
  uploadingVideo.value = true
  uploadProgress.value = 0

  try {
    const response = await uploadVideo(file)
    if (response.code === 200 && response.data) {
      videoUrl.value = response.data
      form.content = `<video src="${response.data}" controls style="max-width: 100%;"></video>`
      uploadProgress.value = 100
    } else {
      error.value = response.message || '视频上传失败'
      videoFile.value = null
      videoUrl.value = ''
    }
  } catch (err) {
    error.value = err.message || '视频上传失败，请稍后重试'
    videoFile.value = null
    videoUrl.value = ''
    console.error('视频上传失败:', err)
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
  position: relative;
  background-color: #fafafa;
}

.upload-placeholder {
  text-align: center;
  cursor: pointer;
  padding: 40px;
}

.upload-icon {
  font-size: 48px;
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

.video-preview {
  width: 100%;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.video-info {
  flex: 1;
}

.video-name {
  font-size: 14px;
  color: #252933;
  margin-bottom: 4px;
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

.uploading-status {
  width: 100%;
  padding: 40px;
  text-align: center;
}

.upload-progress {
  font-size: 14px;
  color: #1e80ff;
}

.video-preview-url {
  width: 100%;
  padding: 20px;
}

.preview-video {
  width: 100%;
  max-width: 800px;
  border-radius: 8px;
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

