import { uploadImage, uploadVideo } from '../api/file'

/**
 * 将 base64 图片转换为 Blob
 */
function base64ToBlob(base64, mimeType) {
  const byteCharacters = atob(base64.split(',')[1])
  const byteNumbers = new Array(byteCharacters.length)
  for (let i = 0; i < byteCharacters.length; i++) {
    byteNumbers[i] = byteCharacters.charCodeAt(i)
  }
  const byteArray = new Uint8Array(byteNumbers)
  return new Blob([byteArray], { type: mimeType || 'image/png' })
}

/**
 * 从 data URL 中提取 MIME 类型
 */
function getMimeTypeFromDataUrl(dataUrl) {
  const match = dataUrl.match(/data:([^;]+);base64/)
  return match ? match[1] : 'image/png'
}

/**
 * 上传 base64 图片到 MinIO
 */
async function uploadBase64Image(base64DataUrl) {
  try {
    const mimeType = getMimeTypeFromDataUrl(base64DataUrl)
    const blob = base64ToBlob(base64DataUrl, mimeType)
    
    // 根据 MIME 类型确定文件扩展名
    let extension = '.png'
    if (mimeType.includes('jpeg') || mimeType.includes('jpg')) {
      extension = '.jpg'
    } else if (mimeType.includes('gif')) {
      extension = '.gif'
    } else if (mimeType.includes('webp')) {
      extension = '.webp'
    }
    
    // 创建 File 对象
    const file = new File([blob], `image${extension}`, { type: mimeType })
    
    // 上传到 MinIO
    const response = await uploadImage(file)
    if (response.code === 200 && response.data) {
      return response.data
    }
    throw new Error('上传失败')
  } catch (error) {
    console.error('上传 base64 图片失败:', error)
    throw error
  }
}

/**
 * 处理 HTML 内容中的 base64 图片，将其上传到 MinIO 并替换为 URL
 * @param {string} htmlContent - HTML 内容
 * @returns {Promise<string>} 处理后的 HTML 内容
 */
export async function processBase64Images(htmlContent) {
  if (!htmlContent) return htmlContent
  
  // 匹配所有 base64 格式的图片（data:image/...;base64,...）
  const base64ImageRegex = /<img[^>]+src=["'](data:image\/[^"']+)["'][^>]*>/gi
  const matches = [...htmlContent.matchAll(base64ImageRegex)]
  
  if (matches.length === 0) {
    return htmlContent
  }
  
  let processedContent = htmlContent
  
  // 依次处理每个 base64 图片
  for (const match of matches) {
    const fullMatch = match[0]
    const base64DataUrl = match[1]
    
    try {
      // 上传图片到 MinIO
      const minioUrl = await uploadBase64Image(base64DataUrl)
      
      // 替换 HTML 中的 base64 URL 为 MinIO URL
      processedContent = processedContent.replace(base64DataUrl, minioUrl)
    } catch (error) {
      console.error('处理图片失败:', error)
      // 如果上传失败，保留原 base64 图片（或者可以删除该图片标签）
      // 这里选择保留，避免丢失内容
    }
  }
  
  return processedContent
}

/**
 * 处理 HTML 内容中的 base64 视频，将其上传到 MinIO 并替换为 URL
 * @param {string} htmlContent - HTML 内容
 * @returns {Promise<string>} 处理后的 HTML 内容
 */
export async function processBase64Videos(htmlContent) {
  if (!htmlContent) return htmlContent
  
  // 匹配所有 base64 格式的视频（data:video/...;base64,...）
  const base64VideoRegex = /<video[^>]+src=["'](data:video\/[^"']+)["'][^>]*>/gi
  const matches = [...htmlContent.matchAll(base64VideoRegex)]
  
  if (matches.length === 0) {
    return htmlContent
  }
  
  let processedContent = htmlContent
  
  // 依次处理每个 base64 视频
  for (const match of matches) {
    const fullMatch = match[0]
    const base64DataUrl = match[1]
    
    try {
      const mimeType = getMimeTypeFromDataUrl(base64DataUrl)
      const blob = base64ToBlob(base64DataUrl, mimeType)
      
      // 根据 MIME 类型确定文件扩展名
      let extension = '.mp4'
      if (mimeType.includes('webm')) {
        extension = '.webm'
      } else if (mimeType.includes('ogg')) {
        extension = '.ogg'
      }
      
      // 创建 File 对象
      const file = new File([blob], `video${extension}`, { type: mimeType })
      
      // 上传到 MinIO
      const response = await uploadVideo(file)
      if (response.code === 200 && response.data) {
        const minioUrl = response.data
        // 替换 HTML 中的 base64 URL 为 MinIO URL
        processedContent = processedContent.replace(base64DataUrl, minioUrl)
      }
    } catch (error) {
      console.error('处理视频失败:', error)
      // 如果上传失败，保留原 base64 视频
    }
  }
  
  return processedContent
}

/**
 * 处理 HTML 内容中的所有 base64 媒体文件（图片和视频）
 * @param {string} htmlContent - HTML 内容
 * @returns {Promise<string>} 处理后的 HTML 内容
 */
export async function processBase64Media(htmlContent) {
  if (!htmlContent) return htmlContent
  
  // 先处理图片
  let processed = await processBase64Images(htmlContent)
  
  // 再处理视频
  processed = await processBase64Videos(processed)
  
  return processed
}











