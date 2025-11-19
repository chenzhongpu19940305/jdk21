import { get, post, del } from './index'

/**
 * 获取菜单列表（包含 Tab 子项）
 * @returns {Promise} 菜单列表
 */
export function getMenuList() {
  return get('/menu/list')
}

/**
 * 创建菜单
 * @param {Object} data - 菜单数据
 * @param {string} data.name - 菜单名称
 * @param {string} data.code - 菜单代码
 * @param {string} data.icon - 菜单图标（可选）
 * @param {number} data.sortOrder - 排序顺序（可选）
 * @param {number} data.status - 状态：0-禁用，1-启用（可选，默认1）
 * @returns {Promise} 创建结果
 */
export function createMenu(data) {
  return post('/menu/create', data)
}

/**
 * 删除菜单
 * @param {number} id - 菜单ID
 * @returns {Promise} 删除结果
 */
export function deleteMenu(id) {
  return del(`/menu/${id}`)
}




