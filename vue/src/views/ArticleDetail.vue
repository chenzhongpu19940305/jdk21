<template>
  <div class="article-detail-container">
    <div class="article-detail">
      <!-- 返回按钮和操作按钮 -->
      <div class="back-button">
        <button @click="goBack" class="back-btn">← 返回</button>
        <div v-if="article && isAuthor" class="action-buttons">
          <button v-if="!isEditing" @click="startEdit" class="edit-btn">✏️ 编辑</button>
          <template v-else>
            <button @click="cancelEdit" class="cancel-btn">取消</button>
            <button @click="saveArticle" :disabled="saving" class="save-btn">
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </template>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading">加载中...</div>

      <!-- 文章不存在 -->
      <div v-else-if="!article" class="empty-state">
        <p>文章不存在</p>
      </div>

      <!-- 编辑模式 -->
      <div v-else-if="isEditing" class="edit-form">
        <div class="form-item">
          <label>文章标题 <span class="required">*</span></label>
          <input 
            v-model="editForm.title" 
            type="text" 
            placeholder="请输入文章标题"
            maxlength="100"
            class="form-input"
          />
          <span class="char-count">{{ editForm.title.length }}/100</span>
        </div>
        
        <div class="form-item">
          <label>文章摘要</label>
          <textarea 
            v-model="editForm.summary" 
            placeholder="请输入文章摘要（可选）"
            rows="3"
            maxlength="200"
            class="form-textarea"
          ></textarea>
          <span class="char-count">{{ editForm.summary.length }}/200</span>
        </div>
        
        <div class="form-item">
          <label>文章内容 <span class="required">*</span></label>
          <RichTextEditor 
            v-model="editForm.content"
            placeholder="请输入文章内容..."
          />
        </div>
        
        <div class="form-item">
          <label>文章标签</label>
          <div class="tags-input">
            <input 
              v-model="tagInput" 
              type="text" 
              placeholder="输入标签后按回车添加"
              @keyup.enter="addTag"
              class="tag-input"
            />
            <div class="tags-list">
              <span 
                v-for="(tag, index) in editForm.tags" 
                :key="index"
                class="tag-item"
              >
                {{ tag }}
                <button @click="removeTag(index)" class="tag-remove">×</button>
              </span>
            </div>
          </div>
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>
      </div>

      <!-- 查看文章模式 -->
      <article v-else class="article-content">
        <h1 class="article-title">{{ article.title }}</h1>
        
        <div class="article-meta">
          <span class="views">👁️ {{ formatNumber(article.views) }}</span>
          <span class="likes">👍 {{ formatNumber(article.likes) }}</span>
          <span class="create-time">{{ formatDate(article.createTime) }}</span>
        </div>

        <div v-if="article.tags && article.tags.length > 0" class="article-tags">
          <span 
            v-for="tag in article.tags" 
            :key="tag"
            class="tag"
          >
            {{ tag }}
          </span>
        </div>

        <div 
          class="article-body" 
          v-html="article.content"
        ></div>
      </article>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticleDetail, updateArticle } from '../api/article'
import { useUserStore } from '../store/user'
import RichTextEditor from '../components/RichTextEditor.vue'
import { processBase64Media } from '../utils/imageHelper'
import { processMinioUrlsInHtml } from '../utils/imageUrlHelper'
import { convertUrlsToLinksAdvanced } from '../utils/urlHelper'

const route = useRoute()
const router = useRouter()
const { userInfo, isLoggedIn } = useUserStore()

const article = ref(null)
const loading = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const error = ref('')
const tagInput = ref('')

const editForm = ref({
  title: '',
  summary: '',
  content: '',
  tags: []
})

// 判断是否是文章作者
const isAuthor = computed(() => {
  return isLoggedIn.value && article.value && userInfo.value && 
         article.value.userId === userInfo.value.id
})

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载文章详情
const loadArticleDetail = async () => {
  const articleId = route.params.id
  if (!articleId) {
    article.value = null
    return
  }

  loading.value = true
  try {
    const response = await getArticleDetail(articleId)
    
    if (response && response.code === 200 && response.data) {
      const articleData = response.data
      if (articleData.id || articleData.title) {
        let content = articleData.content || ''
        if (typeof content !== 'string') {
          content = String(content || '')
        }
        content = processMinioUrlsInHtml(content)
        content = convertUrlsToLinksAdvanced(content)
        
        article.value = {
          ...articleData,
          content: content,
          tags: articleData.tags ? (typeof articleData.tags === 'string' 
            ? articleData.tags.split(',').filter(tag => tag.trim()) 
            : articleData.tags) : []
        }
      } else {
        article.value = null
      }
    } else {
      article.value = null
    }
  } catch (error) {
    article.value = null
  } finally {
    loading.value = false
  }
}

// 开始编辑
const startEdit = () => {
  if (!article.value) return
  
  editForm.value = {
    title: article.value.title || '',
    summary: article.value.summary || '',
    content: article.value.content || '',
    tags: [...(article.value.tags || [])]
  }
  isEditing.value = true
  error.value = ''
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  error.value = ''
  tagInput.value = ''
}

// 添加标签
const addTag = () => {
  const tag = tagInput.value.trim()
  if (tag && !editForm.value.tags.includes(tag)) {
    editForm.value.tags.push(tag)
    tagInput.value = ''
  }
}

// 删除标签
const removeTag = (index) => {
  editForm.value.tags.splice(index, 1)
}

// 保存文章
const saveArticle = async () => {
  error.value = ''
  
  if (!editForm.value.title.trim()) {
    error.value = '请输入文章标题'
    return
  }
  if (!editForm.value.content.trim()) {
    error.value = '请输入文章内容'
    return
  }
  
  saving.value = true
  try {
    // 处理内容中的 base64 图片和视频，上传到 MinIO
    let processedContent = editForm.value.content
    try {
      processedContent = await processBase64Media(editForm.value.content)
    } catch (e) {
      console.error('处理图片/视频失败:', e)
      error.value = '处理图片/视频失败，请重试'
      saving.value = false
      return
    }
    
    const response = await updateArticle(article.value.id, {
      title: editForm.value.title,
      summary: editForm.value.summary,
      content: processedContent, // 使用处理后的内容（base64 已替换为 MinIO URL）
      tags: editForm.value.tags,
      userId: userInfo.value ? userInfo.value.id : null
    })
    
    if (response.code === 200 && response.data) {
      // 保存成功后，重新加载文章详情以确保数据完整
      isEditing.value = false
      tagInput.value = ''
      // 重新加载文章详情
      await loadArticleDetail()
    } else {
      error.value = response.message || '保存失败'
    }
  } catch (e) {
    error.value = e.message || '保存失败'
    console.error('保存文章失败:', e)
  } finally {
    saving.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadArticleDetail()
})
</script>

<style scoped>
.article-detail-container {
  height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.article-detail {
  width: 100%;
  height: 100%;
  background-color: #fff;
  padding: 20px 40px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.back-button {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.edit-btn,
.save-btn,
.cancel-btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.edit-btn {
  background-color: #1e80ff;
  color: #fff;
}

.edit-btn:hover {
  background-color: #1171ee;
}

.save-btn {
  background-color: #1e80ff;
  color: #fff;
}

.save-btn:hover:not(:disabled) {
  background-color: #1171ee;
}

.save-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #515767;
  border: 1px solid #e5e5e5;
}

.cancel-btn:hover {
  background-color: #e5e5e5;
}

.back-btn {
  background: none;
  border: 1px solid #e5e5e5;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  color: #515767;
  cursor: pointer;
  transition: all 0.3s;
}

.back-btn:hover {
  background-color: #f5f5f5;
  border-color: #1e80ff;
  color: #1e80ff;
}

.loading {
  text-align: center;
  padding: 60px 20px;
  color: #8a919f;
  font-size: 16px;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #8a919f;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-state p {
  font-size: 16px;
}

.article-content {
  width: 100%;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.article-title {
  font-size: 32px;
  font-weight: 700;
  color: #252933;
  line-height: 1.4;
  margin-bottom: 20px;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #8a919f;
}

.views,
.likes {
  display: flex;
  align-items: center;
  gap: 4px;
}

.create-time {
  margin-left: auto;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 30px;
}

.tag {
  display: inline-block;
  padding: 4px 12px;
  background-color: #f2f3f5;
  color: #515767;
  border-radius: 4px;
  font-size: 13px;
}

.article-body {
  font-size: 16px;
  line-height: 1.8;
  color: #252933;
  word-wrap: break-word;
  pointer-events: auto;
  position: relative;
  z-index: 1;
}

/* 文章内容样式 */
:deep(.article-body) {
  font-size: 16px;
  line-height: 1.8;
  color: #252933;
}

:deep(.article-body h1),
:deep(.article-body h2),
:deep(.article-body h3),
:deep(.article-body h4),
:deep(.article-body h5),
:deep(.article-body h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  color: #252933;
}

:deep(.article-body h1) {
  font-size: 28px;
}

:deep(.article-body h2) {
  font-size: 24px;
}

:deep(.article-body h3) {
  font-size: 20px;
}

:deep(.article-body p) {
  margin-bottom: 16px;
}

:deep(.article-body img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 20px 0;
}

:deep(.article-body video) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 20px 0;
}

:deep(.article-body ul),
:deep(.article-body ol) {
  margin-bottom: 16px;
  padding-left: 30px;
}

:deep(.article-body li) {
  margin-bottom: 8px;
}

:deep(.article-body blockquote) {
  border-left: 4px solid #1e80ff;
  padding-left: 16px;
  margin: 20px 0;
  color: #8a919f;
  font-style: italic;
}

:deep(.article-body code) {
  background-color: #f2f3f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

:deep(.article-body pre) {
  background-color: #f2f3f5;
  padding: 16px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 20px 0;
}

:deep(.article-body pre code) {
  background-color: transparent;
  padding: 0;
}

:deep(.article-body a) {
  color: #1e80ff;
  text-decoration: none;
  cursor: pointer;
  pointer-events: auto;
  position: relative;
  z-index: 10;
}

:deep(.article-body a:hover) {
  text-decoration: underline;
  color: #1171ee;
}

:deep(.article-body a:active) {
  color: #0d5fd3;
}

/* URL 链接样式 */
:deep(.article-body .article-url-link) {
  color: #1e80ff;
  text-decoration: none;
  word-break: break-all;
  border-bottom: 1px solid transparent;
  transition: all 0.3s;
  cursor: pointer;
  pointer-events: auto;
  position: relative;
  z-index: 10;
  display: inline;
}

:deep(.article-body .article-url-link:hover) {
  color: #1171ee;
  text-decoration: underline;
  border-bottom-color: #1171ee;
}

:deep(.article-body .article-url-link:active) {
  color: #0d5fd3;
}

/* 编辑表单样式 */
.edit-form {
  width: 100%;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.form-item {
  margin-bottom: 24px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #252933;
}

.required {
  color: #ff4757;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
  border-color: #1e80ff;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.char-count {
  display: block;
  text-align: right;
  margin-top: 4px;
  font-size: 12px;
  color: #8a919f;
}

.tags-input {
  width: 100%;
}

.tag-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
  margin-bottom: 12px;
}

.tag-input:focus {
  border-color: #1e80ff;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background-color: #f2f3f5;
  color: #515767;
  border-radius: 4px;
  font-size: 13px;
}

.tag-remove {
  background: none;
  border: none;
  color: #8a919f;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  padding: 0;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s;
}

.tag-remove:hover {
  color: #ff4757;
}

.error-message {
  padding: 12px;
  background-color: #fff5f5;
  border: 1px solid #ffc9c9;
  border-radius: 4px;
  color: #ff4757;
  font-size: 14px;
  margin-top: 16px;
}
</style>

