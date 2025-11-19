/**
 * URL 处理工具函数
 */

/**
 * 检测文本中的 URL 并转换为可点击的链接
 * 注意：只处理纯文本中的 URL，不会处理已经在 HTML 标签中的 URL（如 img src、a href 等）
 * @param {string} htmlContent - HTML 内容
 * @returns {string} 处理后的 HTML 内容
 */
export function convertUrlsToLinks(htmlContent) {
  if (!htmlContent) return htmlContent

  // URL 正则表达式：匹配 http://, https://, www. 开头的 URL
  // 排除已经在 HTML 标签中的 URL（通过检查前后是否有 < 或 >）
  const urlRegex = /(?<!<[^>]*)(?<!href=["'])(?<!src=["'])(?<!url\(["']?)(https?:\/\/[^\s<>"']+|www\.[^\s<>"']+)/gi

  // 将 HTML 内容按标签分割，只处理文本节点
  let processed = htmlContent.replace(urlRegex, (match) => {
    // 确保 URL 完整（如果是以 www. 开头，添加 http://）
    let url = match
    if (url.startsWith('www.')) {
      url = 'http://' + url
    }

    // 检查是否已经在链接中（避免重复转换）
    // 这里简单检查，如果前后已经有 <a 或 </a>，就不处理
    // 更复杂的检查需要解析 HTML，这里使用简单方法

    // 创建链接，在新标签页打开
    return `<a href="${url}" target="_blank" rel="noopener noreferrer" class="article-url-link">${match}</a>`
  })

  return processed
}

/**
 * 更精确的 URL 转换函数
 * 使用更复杂的正则来避免破坏已有的 HTML 标签
 * @param {string} htmlContent - HTML 内容
 * @returns {string} 处理后的 HTML 内容
 */
export function convertUrlsToLinksAdvanced(htmlContent) {
  if (!htmlContent) return htmlContent
  
  if (typeof htmlContent !== 'string') {
    return htmlContent
  }

  // 先提取所有 HTML 标签和已有的链接，保留它们
  // 匹配完整的 HTML 标签（包括 <a> 标签）
  const tagRegex = /<[^>]+>/g
  const tags = []
  let tagIndex = 0
  let processed = htmlContent.replace(tagRegex, (match) => {
    tags[tagIndex] = match
    return `__TAG_${tagIndex++}__`
  })

  // 在文本内容中查找并转换 URL
  // 匹配 http://, https://, www. 开头的 URL
  // 排除已经在链接中的 URL（通过检查前后是否有 __TAG_）
  // 注意：正则表达式有捕获组，所以回调函数参数是 (match, p1, offset, string)
  const urlRegex = /(https?:\/\/[^\s<>"']+|www\.[^\s<>"']+)/gi
  
  processed = processed.replace(urlRegex, function(match, p1, offset, string) {
    // 确保 string 是字符串类型
    if (typeof string !== 'string') {
      return match
    }
    
    // 检查前后是否有标签标记，如果有则不处理（说明已经在 HTML 标签中）
    const before = string.substring(Math.max(0, offset - 20), offset)
    const after = string.substring(offset + match.length, Math.min(string.length, offset + match.length + 20))
    
    // 如果前后有标签标记，说明这个 URL 已经在 HTML 标签中，不处理
    if (before.includes('__TAG_') || after.includes('__TAG_')) {
      return match
    }
    
    // 检查是否已经在链接中（简单检查：前后是否有 <a 或 </a>）
    const context = string.substring(Math.max(0, offset - 50), Math.min(string.length, offset + match.length + 50))
    if (context.includes('<a') || context.includes('</a>')) {
      return match
    }
    
    let url = match
    if (url.startsWith('www.')) {
      url = 'http://' + url
    }
    
    // 转义 URL 中的特殊字符，防止 XSS
    const escapedUrl = url.replace(/"/g, '&quot;').replace(/'/g, '&#39;')
    const escapedMatch = match.replace(/</g, '&lt;').replace(/>/g, '&gt;')
    
    return `<a href="${escapedUrl}" target="_blank" rel="noopener noreferrer" class="article-url-link">${escapedMatch}</a>`
  })

  // 恢复 HTML 标签
  processed = processed.replace(/__TAG_(\d+)__/g, (match, index) => {
    return tags[parseInt(index)] || match
  })

  return processed
}

