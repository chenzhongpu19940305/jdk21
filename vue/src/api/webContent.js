import { get } from './index'

/**
 * 获取网页内容
 * @param {string} url 网页 URL
 * @returns {Promise} API 响应
 */
export function getWebContent(url) {
  return get('/web/content', { url })
}










