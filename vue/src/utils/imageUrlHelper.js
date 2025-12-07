/**
 * 图片 URL 处理工具
 * 将 MinIO 的 URL 转换为后端代理 URL，以便前端可以正常访问
 */

import { getServerBaseUrl } from './index.js'

/**
 * 将 MinIO URL 转换为后端代理 URL
 * @param {string} minioUrl - MinIO 的 URL（如：http://localhost:9000/demo-bucket/images/uuid.jpg）
 * @returns {string} 后端代理 URL（如：http://localhost:8080/api/file/get/images/uuid.jpg）
 */
export function convertMinioUrlToProxyUrl(minioUrl) {
  if (!minioUrl) return minioUrl
  
  // 如果已经是代理 URL，直接返回
  if (minioUrl.includes('/api/file/get/')) {
    return minioUrl
  }
  
  // 获取当前配置的服务器地址
  const BASE_URL = getServerBaseUrl()
  
  // 提取对象名称（如：images/uuid.jpg）
  try {
    const url = new URL(minioUrl)
    const pathParts = url.pathname.split('/').filter(part => part)
    
    // MinIO URL 格式：http://localhost:9000/bucket-name/images/uuid.jpg
    // 需要跳过 bucket 名称，获取后面的路径
    if (pathParts.length >= 2) {
      // 跳过第一个（bucket名称），取后面的部分
      const objectName = pathParts.slice(1).join('/')
      return `${BASE_URL}/file/get/${objectName}`
    }
  } catch (e) {
    console.error('转换 MinIO URL 失败:', e)
    return minioUrl
  }
  
  return minioUrl
}

/**
 * 处理 HTML 内容中的 MinIO URL，转换为代理 URL
 * @param {string} htmlContent - HTML 内容
 * @returns {string} 处理后的 HTML 内容
 */
export function processMinioUrlsInHtml(htmlContent) {
  if (!htmlContent) return htmlContent
  
  // 匹配所有 img 标签中的 src 属性（支持 src 在标签中的任意位置）
  let processed = htmlContent.replace(/<img([^>]*\s+)?src=["']([^"']+)["']([^>]*)>/gi, (match, before, src, after) => {
    const proxyUrl = convertMinioUrlToProxyUrl(src)
    // 保持原有的属性顺序
    if (before) {
      return `<img${before}src="${proxyUrl}"${after}>`
    } else {
      return `<img src="${proxyUrl}"${after}>`
    }
  })
  
  // 匹配所有 video 标签中的 src 属性
  processed = processed.replace(/<video([^>]*\s+)?src=["']([^"']+)["']([^>]*)>/gi, (match, before, src, after) => {
    const proxyUrl = convertMinioUrlToProxyUrl(src)
    if (before) {
      return `<video${before}src="${proxyUrl}"${after}>`
    } else {
      return `<video src="${proxyUrl}"${after}>`
    }
  })
  
  return processed
}

