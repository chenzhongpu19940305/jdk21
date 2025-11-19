import { post, get, put, del } from './index'

/**
 * 发布文章
 * @param {Object} data - 文章数据
 * @param {string} data.title - 标题
 * @param {string} data.summary - 摘要
 * @param {string} data.content - 内容（HTML）
 * @param {Array} data.tags - 标签
 * @param {number} data.userId - 用户ID
 * @param {number} data.menuId - 菜单ID
 * @param {string} data.tabCode - Tab代码
 * @returns {Promise} 发布结果
 */
export function publishArticle(data) {
  return post('/article/publish', data)
}

/**
 * 获取文章列表
 * @param {Object} params - 查询参数
 * @param {number} params.menuId - 菜单ID（可选）
 * @param {string} params.tabCode - Tab代码（可选）
 * @param {string} params.keyword - 搜索关键词（可选）
 * @param {number} params.page - 页码（默认1）
 * @param {number} params.pageSize - 每页数量（默认10）
 * @returns {Promise} 文章列表
 */
export function getArticleList(params = {}) {
  return get('/article/list', params)
}

/**
 * 获取文章详情
 * @param {number} id - 文章ID
 * @returns {Promise} 文章详情
 */
export function getArticleDetail(id) {
  return get(`/article/${id}`)
}

/**
 * 更新文章
 * @param {number} id - 文章ID
 * @param {Object} data - 文章数据
 * @param {string} data.title - 标题
 * @param {string} data.summary - 摘要
 * @param {string} data.content - 内容（HTML）
 * @param {Array} data.tags - 标签
 * @param {number} data.userId - 用户ID
 * @returns {Promise} 更新结果
 */
export function updateArticle(id, data) {
  return put(`/article/${id}`, data)
}

/**
 * 删除文章
 * @param {number} id - 文章ID
 * @param {number} userId - 用户ID
 * @returns {Promise} 删除结果
 */
export function deleteArticle(id, userId) {
  return del(`/article/${id}?userId=${userId}`)
}

