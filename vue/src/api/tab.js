import { get, post, del } from './index'

/**
 * 获取 Tab 列表
 * @param {Object} params - 查询参数
 * @param {number} params.menuId - 菜单ID（可选）
 * @returns {Promise} Tab 列表
 */
export function getTabList(params = {}) {
  return get('/tab/list', params)
}

/**
 * 创建 Tab
 * @param {Object} data - Tab 数据
 * @param {number} data.menuId - 菜单ID
 * @param {string} data.name - Tab 名称
 * @param {string} data.code - Tab 代码
 * @param {number} data.sortOrder - 排序顺序（可选）
 * @param {number} data.status - 状态：0-禁用，1-启用（可选，默认1）
 * @returns {Promise} 创建结果
 */
export function createTab(data) {
  return post('/tab/create', data)
}

/**
 * 删除 Tab
 * @param {number} id - Tab ID
 * @returns {Promise} 删除结果
 */
export function deleteTab(id) {
  return del(`/tab/${id}`)
}










