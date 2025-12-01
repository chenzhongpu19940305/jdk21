import { post } from './index'

/**
 * 上传图片
 * @param {File} file - 图片文件
 * @returns {Promise} 上传结果，包含图片URL
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'}/file/upload/image`, {
    method: 'POST',
    body: formData
  }).then(async (response) => {
    const data = await response.json()
    if (data.code && data.code !== 200) {
      const error = new Error(data.message || '上传失败')
      error.code = data.code
      throw error
    }
    if (!response.ok) {
      const error = new Error(data.message || '上传失败')
      error.code = response.status
      throw error
    }
    return data
  })
}

/**
 * 上传视频
 * @param {File} file - 视频文件
 * @returns {Promise} 上传结果，包含视频URL
 */
export function uploadVideo(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'}/file/upload/video`, {
    method: 'POST',
    body: formData
  }).then(async (response) => {
    const data = await response.json()
    if (data.code && data.code !== 200) {
      const error = new Error(data.message || '上传失败')
      error.code = data.code
      throw error
    }
    if (!response.ok) {
      const error = new Error(data.message || '上传失败')
      error.code = response.status
      throw error
    }
    return data
  })
}











