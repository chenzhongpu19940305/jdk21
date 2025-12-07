/**
 * 工具函数
 */

/**
 * 格式化日期
 * @param {Date|string|number} date - 日期
 * @param {string} format - 格式
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 防抖函数
 * @param {Function} func - 要防抖的函数
 * @param {number} wait - 等待时间（毫秒）
 * @returns {Function} 防抖后的函数
 */
export function debounce(func, wait = 300) {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

/**
 * 节流函数
 * @param {Function} func - 要节流的函数
 * @param {number} limit - 时间限制（毫秒）
 * @returns {Function} 节流后的函数
 */
export function throttle(func, limit = 300) {
  let inThrottle
  return function executedFunction(...args) {
    if (!inThrottle) {
      func.apply(this, args)
      inThrottle = true
      setTimeout(() => (inThrottle = false), limit)
    }
  }
}

/**
 * 服务器配置相关工具函数
 */
const SERVER_CONFIG_KEY = 'vue_app_server_config'

/**
 * 获取服务器基础URL
 * @returns {string} 服务器基础URL
 */
export function getServerBaseUrl() {
  // 优先级：localStorage > 环境变量 > 默认值
  const savedConfig = localStorage.getItem(SERVER_CONFIG_KEY)
  if (savedConfig) {
    try {
      const config = JSON.parse(savedConfig)
      if (config.baseUrl) {
        return config.baseUrl
      }
    } catch (e) {
      console.error('解析服务器配置失败:', e)
    }
  }
  
  // 环境变量配置
  const envUrl = import.meta.env.VITE_API_BASE_URL
  if (envUrl) {
    return envUrl
  }
  
  // 默认值
  return 'http://localhost:8080/api'
}

/**
 * 设置服务器基础URL
 * @param {string} baseUrl - 服务器基础URL（例如：http://192.168.1.100:8080/api）
 */
export function setServerBaseUrl(baseUrl) {
  const config = {
    baseUrl: baseUrl,
    updatedAt: new Date().toISOString()
  }
  localStorage.setItem(SERVER_CONFIG_KEY, JSON.stringify(config))
}

/**
 * 获取服务器配置
 * @returns {object} 服务器配置对象
 */
export function getServerConfig() {
  const savedConfig = localStorage.getItem(SERVER_CONFIG_KEY)
  if (savedConfig) {
    try {
      return JSON.parse(savedConfig)
    } catch (e) {
      console.error('解析服务器配置失败:', e)
    }
  }
  return null
}

/**
 * 清除服务器配置（恢复默认）
 */
export function clearServerConfig() {
  localStorage.removeItem(SERVER_CONFIG_KEY)
}













