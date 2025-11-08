/**
 * API 请求封装
 */

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

/**
 * 通用请求方法
 * @param {string} url - 请求地址
 * @param {object} options - 请求选项
 * @returns {Promise} 请求结果
 */
async function request(url, options = {}) {
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  }

  if (config.body && typeof config.body === 'object') {
    config.body = JSON.stringify(config.body)
  }

  try {
    const response = await fetch(`${BASE_URL}${url}`, config)
    const data = await response.json()

    // 检查业务状态码
    if (data.code && data.code !== 200) {
      const error = new Error(data.message || '请求失败')
      error.code = data.code
      throw error
    }

    // HTTP 状态码错误
    if (!response.ok) {
      const error = new Error(data.message || '请求失败')
      error.code = response.status
      throw error
    }

    return data
  } catch (error) {
    console.error('API 请求错误:', error)
    throw error
  }
}

/**
 * GET 请求
 * @param {string} url - 请求地址
 * @param {object} params - 查询参数
 * @returns {Promise} 请求结果
 */
export function get(url, params = {}) {
  const queryString = new URLSearchParams(params).toString()
  const fullUrl = queryString ? `${url}?${queryString}` : url
  return request(fullUrl, { method: 'GET' })
}

/**
 * POST 请求
 * @param {string} url - 请求地址
 * @param {object} data - 请求数据
 * @returns {Promise} 请求结果
 */
export function post(url, data = {}) {
  return request(url, {
    method: 'POST',
    body: data
  })
}

/**
 * PUT 请求
 * @param {string} url - 请求地址
 * @param {object} data - 请求数据
 * @returns {Promise} 请求结果
 */
export function put(url, data = {}) {
  return request(url, {
    method: 'PUT',
    body: data
  })
}

/**
 * DELETE 请求
 * @param {string} url - 请求地址
 * @returns {Promise} 请求结果
 */
export function del(url) {
  return request(url, { method: 'DELETE' })
}

