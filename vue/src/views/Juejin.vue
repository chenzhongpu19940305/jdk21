<template>
  <div class="juejin-container">
    <!-- 顶部导航栏 -->
    <header class="top-navbar">
      <div class="nav-wrapper">
        <div class="nav-left">
          <div class="logo">
            <span class="logo-icon">⛏️</span>
            <span class="logo-text">中朴掘金</span>
          </div>
        </div>
        <div class="nav-center">
          <div class="search-box">
            <input 
              v-model="searchKeyword" 
              type="text" 
              placeholder="探索中朴掘金" 
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <span class="search-icon" @click="handleSearch">🔍</span>
          </div>
        </div>
        <div class="nav-right">
          <template v-if="isLoggedIn">
            <button class="publish-btn" @click="showPublishDialog">✍️ 发布</button>
            <div class="user-menu">
              <span class="user-name">{{ userInfo.nickname || userInfo.username }}</span>
              <button class="logout-btn" @click="handleLogout">退出</button>
            </div>
          </template>
          <template v-else>
            <a href="#" class="nav-item" @click.prevent="showLoginDialog('login')">登录</a>
            <a href="#" class="nav-item register-btn" @click.prevent="showLoginDialog('register')">注册</a>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <div class="main-wrapper">
      <!-- 左侧分类导航 -->
      <aside class="sidebar-left">
        <ul class="category-list">
          <li 
            v-for="menu in menus" 
            :key="menu.id"
            :class="['category-item', { active: currentCategory === menu.code }]"
            @click="handleMenuClick(menu)"
          >
            <span class="category-icon">{{ menu.icon }}</span>
            <span class="category-name">{{ menu.name }}</span>
            <span 
              v-if="isLoggedIn" 
              class="category-delete-btn"
              @click.stop="handleDeleteMenu(menu.id)"
              title="删除菜单"
            >
              ×
            </span>
          </li>
        </ul>
        <button 
          v-if="isLoggedIn" 
          class="add-menu-btn"
          @click="showAddMenuDialog = true"
          title="添加菜单"
        >
          + 添加菜单
        </button>
      </aside>

      <!-- 中间内容区 -->
      <main class="content-main">
        <div v-if="currentTabs.length > 0 || isLoggedIn" class="content-tabs-wrapper">
          <div class="content-tabs">
            <button 
              v-for="tab in currentTabs" 
              :key="tab.id"
              :class="['tab-item', { active: activeTab === tab.code }]"
              @click="activeTab = tab.code"
            >
              {{ tab.name }}
              <span 
                v-if="isLoggedIn" 
                class="tab-delete-btn"
                @click.stop="handleDeleteTab(tab.id)"
                title="删除 Tab"
              >
                ×
              </span>
            </button>
          </div>
          <button 
            v-if="isLoggedIn && currentMenuId" 
            class="add-tab-btn"
            @click="showAddTabDialog = true"
            title="添加 Tab"
          >
            + 添加 Tab
          </button>
        </div>

        <div v-if="loadingArticles" class="loading">加载中...</div>
        <div v-else-if="articles.length === 0" class="empty-state">
          <p>暂无文章</p>
        </div>
        <div v-else class="article-list">
          <article 
            v-for="article in articles" 
            :key="article.id"
            class="article-item"
          >
            <div class="article-content" @click="goToArticleDetail(article.id)">
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-summary">{{ article.summary }}</p>
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
            </div>
            <div 
              v-if="isLoggedIn && userInfo && article.userId === userInfo.id" 
              class="article-actions"
              @click.stop
            >
              <button 
                class="delete-btn" 
                @click="handleDeleteArticle(article.id)"
                title="删除文章"
              >
                🗑️
              </button>
            </div>
          </article>
        </div>
      </main>
    </div>
    
    <!-- 登录/注册对话框 -->
    <LoginDialog 
      v-model:visible="showDialog" 
      :default-mode="dialogMode"
      @success="handleLoginSuccess"
    />
    
    <!-- 发布文章对话框 -->
    <PublishArticleDialog 
      v-model:visible="showPublishArticleDialog"
      :menu-id="currentMenuId"
      :tab-code="activeTab"
      @success="handlePublishSuccess"
    />
    
    <!-- 添加 Tab 对话框 -->
    <div v-if="showAddTabDialog" class="dialog-overlay" @click="showAddTabDialog = false">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>添加 Tab</h3>
          <button class="dialog-close" @click="showAddTabDialog = false">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>Tab 名称 <span class="required">*</span></label>
            <input 
              v-model="newTab.name" 
              type="text" 
              placeholder="请输入 Tab 名称"
              maxlength="20"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label>Tab 代码 <span class="required">*</span></label>
            <input 
              v-model="newTab.code" 
              type="text" 
              placeholder="请输入 Tab 代码（英文，如：hot）"
              maxlength="50"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label>排序顺序</label>
            <input 
              v-model.number="newTab.sortOrder" 
              type="number" 
              placeholder="数字越小越靠前（默认0）"
              class="form-input"
            />
          </div>
          <div v-if="tabError" class="error-message">{{ tabError }}</div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="showAddTabDialog = false">取消</button>
          <button class="btn-confirm" @click="handleCreateTab" :disabled="creatingTab">
            {{ creatingTab ? '创建中...' : '创建' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- 添加菜单对话框 -->
    <div v-if="showAddMenuDialog" class="dialog-overlay" @click="showAddMenuDialog = false">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>添加菜单</h3>
          <button class="dialog-close" @click="showAddMenuDialog = false">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>菜单名称 <span class="required">*</span></label>
            <input 
              v-model="newMenu.name" 
              type="text" 
              placeholder="请输入菜单名称"
              maxlength="20"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label>菜单代码 <span class="required">*</span></label>
            <input 
              v-model="newMenu.code" 
              type="text" 
              placeholder="请输入菜单代码（英文，如：new-category）"
              maxlength="50"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label>菜单图标</label>
            <input 
              v-model="newMenu.icon" 
              type="text" 
              placeholder="请输入图标（如：📋、⭐、💻等）"
              maxlength="10"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label>排序顺序</label>
            <input 
              v-model.number="newMenu.sortOrder" 
              type="number" 
              placeholder="数字越小越靠前（默认0）"
              class="form-input"
            />
          </div>
          <div v-if="menuError" class="error-message">{{ menuError }}</div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="showAddMenuDialog = false">取消</button>
          <button class="btn-confirm" @click="handleCreateMenu" :disabled="creatingMenu">
            {{ creatingMenu ? '创建中...' : '创建' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import LoginDialog from '../components/LoginDialog.vue'
import PublishArticleDialog from '../components/PublishArticleDialog.vue'
import { useUserStore } from '../store/user'
import { getMenuList, createMenu, deleteMenu } from '../api/menu'
import { getArticleList, deleteArticle } from '../api/article'
import { createTab, deleteTab } from '../api/tab'

const router = useRouter()

const { userInfo, isLoggedIn, clearUser } = useUserStore()

const showDialog = ref(false)
const dialogMode = ref('login')
const showPublishArticleDialog = ref(false)

const menus = ref([])
const currentCategory = ref('')
const activeTab = ref('')
const articles = ref([])
const loadingArticles = ref(false)
const searchKeyword = ref('')
const showAddTabDialog = ref(false)
const creatingTab = ref(false)
const tabError = ref('')
const newTab = ref({
  name: '',
  code: '',
  sortOrder: 0
})
const showAddMenuDialog = ref(false)
const creatingMenu = ref(false)
const menuError = ref('')
const newMenu = ref({
  name: '',
  code: '',
  icon: '📋',
  sortOrder: 0
})

// 当前选中的菜单对应的 Tab 列表
const currentTabs = computed(() => {
  const currentMenu = menus.value.find(menu => menu.code === currentCategory.value)
  if (currentMenu && currentMenu.tabs) {
    return currentMenu.tabs
  }
  return []
})

// 当前选中的菜单ID
const currentMenuId = computed(() => {
  const currentMenu = menus.value.find(menu => menu.code === currentCategory.value)
  return currentMenu ? currentMenu.id : null
})


const showLoginDialog = (mode) => {
  dialogMode.value = mode
  showDialog.value = true
}

const handleLoginSuccess = (user) => {
  console.log('登录成功:', user)
}

const handleLogout = () => {
  clearUser()
}

const showPublishDialog = () => {
  if (!isLoggedIn.value) {
    showLoginDialog('login')
    return
  }
  showPublishArticleDialog.value = true
}

const handlePublishSuccess = (article) => {
  console.log('文章发布成功:', article)
  // 刷新文章列表
  loadArticles()
}

// 跳转到文章详情页
const goToArticleDetail = (articleId) => {
  router.push(`/article/${articleId}`)
}

// 加载菜单列表
const loadMenus = async (keepCurrentMenu = false) => {
  try {
    // 保存当前选中的菜单 code
    const currentMenuCode = keepCurrentMenu ? currentCategory.value : null
    
    const response = await getMenuList()
    if (response.code === 200 && response.data) {
      menus.value = response.data
      
      if (keepCurrentMenu && currentMenuCode) {
        // 保持当前菜单选中
        const currentMenu = menus.value.find(menu => menu.code === currentMenuCode)
        if (currentMenu) {
          // 恢复当前菜单和 Tab
          currentCategory.value = currentMenuCode
          if (currentMenu.tabs && currentMenu.tabs.length > 0) {
            // 如果之前有选中的 Tab，尝试恢复；否则选择第一个
            if (!activeTab.value || !currentMenu.tabs.find(tab => tab.code === activeTab.value)) {
              activeTab.value = currentMenu.tabs[0].code
            }
          } else {
            activeTab.value = ''
          }
        } else {
          // 如果当前菜单不存在了，选择第一个
          if (menus.value.length > 0) {
            handleMenuClick(menus.value[0])
          }
        }
      } else {
        // 设置默认选中的菜单（第一个）
        if (menus.value.length > 0) {
          handleMenuClick(menus.value[0])
        }
      }
      
      // 菜单加载完成后，加载文章列表
      await loadArticles()
    }
  } catch (error) {
    console.error('加载菜单列表失败:', error)
    // 如果接口失败，使用默认值
    menus.value = [
      {
        id: 1,
        name: '综合',
        code: 'comprehensive',
        icon: '📋',
        sortOrder: 1,
        status: 1,
        tabs: [
          { id: 1, name: '推荐', code: 'recommend', sortOrder: 1, status: 1 },
          { id: 2, name: '最新', code: 'latest', sortOrder: 2, status: 1 }
        ]
      }
    ]
    handleMenuClick(menus.value[0])
  }
}

// 处理菜单点击
const handleMenuClick = (menu) => {
  currentCategory.value = menu.code
  // 设置默认选中的 tab（第一个）
  if (menu.tabs && menu.tabs.length > 0) {
    activeTab.value = menu.tabs[0].code
    // 菜单和Tab都设置完成后，触发加载文章列表
    // 使用 nextTick 确保响应式更新完成（currentMenuId computed 会更新）
    nextTick(() => {
      if (!searchKeyword.value || !searchKeyword.value.trim()) {
        if (currentMenuId.value && activeTab.value) {
          loadArticles()
        }
      }
    })
  } else {
    activeTab.value = ''
    articles.value = []
  }
}

// 格式化数字（浏览量、点赞数）
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
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

// 加载文章列表
const loadArticles = async () => {
  // 如果有搜索关键词，使用搜索模式
  if (searchKeyword.value && searchKeyword.value.trim()) {
    loadingArticles.value = true
    try {
      const response = await getArticleList({
        keyword: searchKeyword.value.trim(),
        page: 1,
        pageSize: 20
      })
      
      if (response.code === 200 && response.data && response.data.list) {
        // 处理文章数据：将tags字符串转换为数组
        articles.value = response.data.list.map(article => ({
          ...article,
          tags: article.tags ? article.tags.split(',').filter(tag => tag.trim()) : []
        }))
      } else {
        articles.value = []
      }
    } catch (error) {
      console.error('搜索文章失败:', error)
      articles.value = []
    } finally {
      loadingArticles.value = false
    }
    return
  }
  
  // 没有搜索关键词时，按菜单和Tab加载
  if (!currentMenuId.value || !activeTab.value) {
    articles.value = []
    return
  }
  
  loadingArticles.value = true
  try {
    const response = await getArticleList({
      menuId: currentMenuId.value,
      tabCode: activeTab.value,
      page: 1,
      pageSize: 20
    })
    
    if (response.code === 200 && response.data && response.data.list) {
      // 处理文章数据：将tags字符串转换为数组
      articles.value = response.data.list.map(article => ({
        ...article,
        tags: article.tags ? article.tags.split(',').filter(tag => tag.trim()) : []
      }))
    } else {
      articles.value = []
    }
  } catch (error) {
    console.error('加载文章列表失败:', error)
    articles.value = []
  } finally {
    loadingArticles.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  loadArticles()
}

// 创建 Tab
const handleCreateTab = async () => {
  tabError.value = ''
  
  if (!newTab.value.name.trim()) {
    tabError.value = '请输入 Tab 名称'
    return
  }
  if (!newTab.value.code.trim()) {
    tabError.value = '请输入 Tab 代码'
    return
  }
  
  if (!currentMenuId.value) {
    tabError.value = '请先选择菜单'
    return
  }
  
  creatingTab.value = true
  try {
    const response = await createTab({
      menuId: currentMenuId.value,
      name: newTab.value.name.trim(),
      code: newTab.value.code.trim(),
      sortOrder: newTab.value.sortOrder || 0,
      status: 1
    })
    
    if (response.code === 200 && response.data) {
      // 刷新菜单列表以获取最新的 Tab（保持当前菜单选中）
      await loadMenus(true)
      // 设置新创建的 Tab 为当前选中
      activeTab.value = response.data.code
      // 关闭对话框并重置表单
      showAddTabDialog.value = false
      newTab.value = { name: '', code: '', sortOrder: 0 }
      tabError.value = ''
    } else {
      tabError.value = response.message || '创建失败'
    }
  } catch (error) {
    tabError.value = error.message || '创建失败'
    console.error('创建 Tab 失败:', error)
  } finally {
    creatingTab.value = false
  }
}

// 删除 Tab
const handleDeleteTab = async (tabId) => {
  if (!confirm('确定要删除这个 Tab 吗？删除后该 Tab 下的文章将无法通过菜单访问。')) {
    return
  }
  
  try {
    const response = await deleteTab(tabId)
    if (response.code === 200) {
      // 刷新菜单列表（保持当前菜单选中）
      await loadMenus(true)
      // 如果删除的是当前选中的 Tab，切换到第一个 Tab
      const currentMenu = menus.value.find(menu => menu.code === currentCategory.value)
      if (currentMenu && currentMenu.tabs && currentMenu.tabs.length > 0) {
        activeTab.value = currentMenu.tabs[0].code
      } else {
        activeTab.value = ''
      }
    } else {
      alert(response.message || '删除失败')
    }
  } catch (error) {
    alert(error.message || '删除失败')
    console.error('删除 Tab 失败:', error)
  }
}

// 创建菜单
const handleCreateMenu = async () => {
  menuError.value = ''
  
  if (!newMenu.value.name.trim()) {
    menuError.value = '请输入菜单名称'
    return
  }
  if (!newMenu.value.code.trim()) {
    menuError.value = '请输入菜单代码'
    return
  }
  
  creatingMenu.value = true
  try {
    const response = await createMenu({
      name: newMenu.value.name.trim(),
      code: newMenu.value.code.trim(),
      icon: newMenu.value.icon.trim() || '📋',
      sortOrder: newMenu.value.sortOrder || 0,
      status: 1
    })
    
    if (response.code === 200 && response.data) {
      // 刷新菜单列表
      await loadMenus()
      // 设置新创建的菜单为当前选中
      currentCategory.value = response.data.code
      // 关闭对话框并重置表单
      showAddMenuDialog.value = false
      newMenu.value = { name: '', code: '', icon: '📋', sortOrder: 0 }
      menuError.value = ''
      // 触发菜单点击，加载文章列表
      const newMenuObj = menus.value.find(menu => menu.code === response.data.code)
      if (newMenuObj) {
        handleMenuClick(newMenuObj)
      }
    } else {
      menuError.value = response.message || '创建失败'
    }
  } catch (error) {
    menuError.value = error.message || '创建失败'
    console.error('创建菜单失败:', error)
  } finally {
    creatingMenu.value = false
  }
}

// 删除菜单
const handleDeleteMenu = async (menuId) => {
  if (!confirm('确定要删除这个菜单吗？删除后该菜单及其下的所有 Tab 和文章将无法通过菜单访问。')) {
    return
  }
  
  try {
    const response = await deleteMenu(menuId)
    if (response.code === 200) {
      // 刷新菜单列表
      await loadMenus()
      // 如果删除的是当前选中的菜单，切换到第一个菜单
      if (menus.value.length > 0) {
        handleMenuClick(menus.value[0])
      } else {
        currentCategory.value = ''
        activeTab.value = ''
        articles.value = []
      }
    } else {
      alert(response.message || '删除失败')
    }
  } catch (error) {
    alert(error.message || '删除失败')
    console.error('删除菜单失败:', error)
  }
}

// 删除文章
const handleDeleteArticle = async (articleId) => {
  if (!isLoggedIn.value || !userInfo.value) {
    return
  }
  
  if (!confirm('确定要删除这篇文章吗？删除后无法恢复。')) {
    return
  }
  
  try {
    const response = await deleteArticle(articleId, userInfo.value.id)
    if (response.code === 200) {
      // 删除成功，刷新文章列表
      loadArticles()
    } else {
      alert(response.message || '删除失败')
    }
  } catch (error) {
    console.error('删除文章失败:', error)
    alert(error.message || '删除失败')
  }
}

// 监听菜单和Tab变化，重新加载文章（仅在非搜索模式下）
watch([currentMenuId, activeTab], () => {
  if (!searchKeyword.value || !searchKeyword.value.trim()) {
    if (currentMenuId.value && activeTab.value) {
      loadArticles()
    }
  }
})

// 监听搜索关键词变化，清空时恢复菜单和Tab模式
watch(searchKeyword, (newVal) => {
  if (!newVal || !newVal.trim()) {
    // 搜索关键词清空时，恢复菜单和Tab模式
    if (currentMenuId.value && activeTab.value) {
      loadArticles()
    }
  }
})

onMounted(() => {
  // 加载菜单列表
  loadMenus()
})
</script>

<style scoped>
.juejin-container {
  height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏 */
.top-navbar {
  background-color: #fff;
  border-bottom: 1px solid #e5e5e5;
  position: relative;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.nav-wrapper {
  width: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  height: 60px;
}

.nav-left {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #1e80ff;
  cursor: pointer;
}

.logo-icon {
  font-size: 24px;
}

.nav-item {
  color: #515767;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
  position: relative;
}

.nav-item:hover {
  color: #1e80ff;
}

.nav-center {
  flex: 1;
  min-width: 0;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  width: 100%;
  height: 36px;
  padding: 0 36px 0 16px;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.search-input:focus {
  border-color: #1e80ff;
}

.search-icon {
  position: absolute;
  right: 12px;
  color: #8a919f;
  cursor: pointer;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  color: #fff;
  font-size: 14px;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.register-btn {
  background-color: #1e80ff;
  color: #fff;
  padding: 6px 16px;
  border-radius: 4px;
  margin-left: 8px;
}

.register-btn:hover {
  background-color: #1171ee;
}

.publish-btn {
  background-color: #1e80ff;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-right: 8px;
}

.publish-btn:hover {
  background-color: #1171ee;
}

/* 主内容区布局 */
.main-wrapper {
  width: 100%;
  flex: 1;
  padding: 16px 24px;
  display: flex;
  gap: 16px;
  overflow: hidden;
  min-height: 0;
}

/* 左侧分类导航 */
.sidebar-left {
  width: 200px;
  background-color: #fff;
  border-radius: 4px;
  padding: 16px 0;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  max-height: 100%;
}

.category-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.category-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  cursor: pointer;
  transition: background-color 0.3s;
  color: #515767;
}

.category-item:hover {
  background-color: #f7f8fa;
}

.category-item.active {
  background-color: #e8f3ff;
  color: #1e80ff;
  border-right: 3px solid #1e80ff;
}

.category-icon {
  font-size: 18px;
}

.category-name {
  font-size: 14px;
  flex: 1;
}

.category-delete-btn {
  display: inline-block;
  width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 50%;
  background-color: #f0f0f0;
  color: #8a919f;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  opacity: 0;
  visibility: hidden;
}

.category-item:hover .category-delete-btn {
  opacity: 1;
  visibility: visible;
}

.category-delete-btn:hover {
  background-color: #fee;
  color: #f56565;
}

.add-menu-btn {
  width: 100%;
  margin-top: 8px;
  padding: 10px;
  border: 1px dashed #e5e5e5;
  background: #fff;
  color: #515767;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.add-menu-btn:hover {
  border-color: #1e80ff;
  color: #1e80ff;
  background-color: #f7f8fa;
}

/* 中间内容区 */
.content-main {
  flex: 1;
  background-color: #fff;
  border-radius: 4px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.content-tabs-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e5e5e5;
  margin-bottom: 16px;
  gap: 16px;
}

.content-tabs {
  display: flex;
  gap: 8px;
  flex: 1;
}

.tab-item {
  position: relative;
  padding: 12px 24px;
  border: none;
  background: none;
  font-size: 14px;
  color: #515767;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab-item:hover {
  color: #1e80ff;
}

.tab-item.active {
  color: #1e80ff;
  border-bottom-color: #1e80ff;
}

.tab-delete-btn {
  display: inline-block;
  margin-left: 8px;
  width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 50%;
  background-color: #f0f0f0;
  color: #8a919f;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  opacity: 0;
  visibility: hidden;
}

.tab-item:hover .tab-delete-btn {
  opacity: 1;
  visibility: visible;
}

.tab-delete-btn:hover {
  background-color: #fee;
  color: #f56565;
}

.add-tab-btn {
  padding: 8px 16px;
  border: 1px solid #e5e5e5;
  background: #fff;
  color: #515767;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.add-tab-btn:hover {
  border-color: #1e80ff;
  color: #1e80ff;
}

/* 对话框样式 */
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

.dialog-content {
  background: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e5e5;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #252933;
}

.dialog-close {
  background: none;
  border: none;
  font-size: 24px;
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

.dialog-close:hover {
  background-color: #f5f5f5;
  color: #252933;
}

.dialog-body {
  padding: 24px;
}

.dialog-body .form-item {
  margin-bottom: 20px;
}

.dialog-body .form-item:last-child {
  margin-bottom: 0;
}

.dialog-body label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #252933;
  font-weight: 500;
}

.dialog-body .required {
  color: #f56565;
}

.dialog-body .form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.dialog-body .form-input:focus {
  border-color: #1e80ff;
}

.error-message {
  color: #f56565;
  font-size: 13px;
  margin-top: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e5e5;
}

.btn-cancel,
.btn-confirm {
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-cancel {
  background: #f5f5f5;
  color: #515767;
}

.btn-cancel:hover {
  background: #e5e5e5;
}

.btn-confirm {
  background: #1e80ff;
  color: #fff;
}

.btn-confirm:hover:not(:disabled) {
  background: #1171ee;
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.article-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
  position: relative;
}

.article-item:hover {
  background-color: #fafafa;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.article-content {
  flex: 1;
  cursor: pointer;
}

.article-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 16px;
  opacity: 0;
  transition: opacity 0.3s;
}

.article-item:hover .article-actions {
  opacity: 1;
}

.delete-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
  color: #8a919f;
}

.delete-btn:hover {
  background-color: #fee;
  color: #f56565;
  transform: scale(1.1);
}

.article-title {
  font-size: 16px;
  font-weight: 600;
  color: #252933;
  margin-bottom: 8px;
  line-height: 1.5;
  transition: color 0.3s;
}

.article-item:hover .article-title {
  color: #1e80ff;
}

.article-summary {
  font-size: 14px;
  color: #8a919f;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #8a919f;
  margin-bottom: 12px;
}

.author {
  color: #515767;
}

.article-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 2px 8px;
  background-color: #f2f3f5;
  color: #515767;
  font-size: 12px;
  border-radius: 2px;
}

.article-thumbnail {
  width: 120px;
  height: 80px;
  margin-left: 16px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.article-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #8a919f;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #8a919f;
}

.empty-state p {
  font-size: 16px;
}

.create-time {
  color: #8a919f;
  font-size: 13px;
}
</style>

