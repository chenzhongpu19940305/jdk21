package com.example.demo.service;

import com.example.demo.dto.PublishArticleRequest;
import com.example.demo.entity.Article;
import com.example.demo.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章服务类
 */
@Service
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private FileService fileService;

    /**
     * 发布文章
     */
    public Article publishArticle(Long userId, PublishArticleRequest request) {
        // 验证内容中是否包含 base64 图片（应该在前端已处理）
        String content = request.getContent();
        if (content != null && content.contains("data:image/") && content.contains("base64")) {
            throw new RuntimeException("文章内容中包含 base64 图片，请先上传图片到 MinIO");
        }
        
        Article article = new Article();
        article.setUserId(userId);
        article.setMenuId(request.getMenuId());
        article.setTabCode(request.getTabCode());
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(content);
        
        // 将标签列表转换为逗号分隔的字符串
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            String tags = String.join(",", request.getTags());
            article.setTags(tags);
        }
        
        int result = articleMapper.insert(article);
        if (result > 0) {
            return articleMapper.findById(article.getId());
        } else {
            throw new RuntimeException("发布文章失败");
        }
    }

    /**
     * 获取文章列表
     */
    public List<Article> getArticleList(Integer page, Integer pageSize) {
        Integer offset = (page - 1) * pageSize;
        return articleMapper.findList(offset, pageSize);
    }

    /**
     * 获取文章详情
     */
    public Article getArticleDetail(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        return article;
    }

    /**
     * 根据菜单ID和Tab代码获取文章列表
     */
    public List<Article> getArticleListByMenuAndTab(Long menuId, String tabCode, Integer page, Integer pageSize) {
        if (menuId == null || tabCode == null || tabCode.trim().isEmpty()) {
            throw new RuntimeException("菜单ID和Tab代码不能为空");
        }
        Integer offset = (page - 1) * pageSize;
        return articleMapper.findByMenuIdAndTabCode(menuId, tabCode, offset, pageSize);
    }

    /**
     * 搜索文章
     */
    public List<Article> searchArticles(String keyword, Integer page, Integer pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getArticleList(page, pageSize);
        }
        Integer offset = (page - 1) * pageSize;
        return articleMapper.searchArticles(keyword.trim(), offset, pageSize);
    }

    /**
     * 更新文章
     */
    public Article updateArticle(Long articleId, Long userId, PublishArticleRequest request) {
        // 先查询文章是否存在
        Article article = articleMapper.findById(articleId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // 检查是否是文章作者
        if (!article.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此文章");
        }
        
        // 验证内容中是否包含 base64 图片（应该在前端已处理）
        String content = request.getContent();
        if (content != null && content.contains("data:image/") && content.contains("base64")) {
            throw new RuntimeException("文章内容中包含 base64 图片，请先上传图片到 MinIO");
        }
        
        // 提取旧内容和新内容中的 MinIO URL
        List<String> oldUrls = extractMinioUrlsFromHtml(article.getContent());
        List<String> newUrls = extractMinioUrlsFromHtml(content);
        
        // 将 URL 转换为对象名称进行比较（避免因 URL 格式不同导致的误判）
        Set<String> oldObjectNames = new HashSet<>();
        for (String url : oldUrls) {
            String objectName = fileService.extractObjectName(url);
            if (objectName != null && !objectName.isEmpty()) {
                oldObjectNames.add(objectName);
            }
        }
        
        Set<String> newObjectNames = new HashSet<>();
        for (String url : newUrls) {
            String objectName = fileService.extractObjectName(url);
            if (objectName != null && !objectName.isEmpty()) {
                newObjectNames.add(objectName);
            }
        }
        
        // 找出被删除的对象名称（在旧内容中但不在新内容中）
        List<String> deletedObjectNames = new ArrayList<>();
        for (String oldObjectName : oldObjectNames) {
            if (!newObjectNames.contains(oldObjectName)) {
                deletedObjectNames.add(oldObjectName);
            }
        }
        
        // 删除 MinIO 中被移除的文件
        if (!deletedObjectNames.isEmpty()) {
            fileService.deleteFiles(deletedObjectNames);
        }
        
        // 更新文章信息
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(content);
        
        // 将标签列表转换为逗号分隔的字符串
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            String tags = String.join(",", request.getTags());
            article.setTags(tags);
        } else {
            article.setTags(null);
        }
        
        int result = articleMapper.update(article);
        if (result > 0) {
            return articleMapper.findById(articleId);
        } else {
            throw new RuntimeException("更新文章失败");
        }
    }

    /**
     * 从 HTML 内容中提取所有 MinIO URL（图片和视频）
     * 支持两种格式：
     * 1. 直接的 MinIO URL：http://localhost:9000/demo-bucket/images/uuid.jpg
     * 2. 代理 URL：/api/file/get/images/uuid.jpg 或 http://localhost:8080/api/file/get/images/uuid.jpg
     * @param htmlContent HTML 内容
     * @return MinIO URL 列表（已转换为可直接提取对象名称的格式）
     */
    private List<String> extractMinioUrlsFromHtml(String htmlContent) {
        List<String> urls = new ArrayList<>();
        if (htmlContent == null || htmlContent.isEmpty()) {
            return urls;
        }

        // 提取 img 标签的 src 属性
        Pattern imgPattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher imgMatcher = imgPattern.matcher(htmlContent);
        while (imgMatcher.find()) {
            String url = imgMatcher.group(1);
            if (url != null && !url.isEmpty()) {
                urls.add(url);
            }
        }

        // 提取 video 标签的 src 属性
        Pattern videoPattern = Pattern.compile("<video[^>]+src=[\"']([^\"']+)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher videoMatcher = videoPattern.matcher(htmlContent);
        while (videoMatcher.find()) {
            String url = videoMatcher.group(1);
            if (url != null && !url.isEmpty()) {
                urls.add(url);
            }
        }

        // 去重
        Set<String> uniqueUrls = new HashSet<>(urls);
        return new ArrayList<>(uniqueUrls);
    }

    /**
     * 删除文章
     */
    public void deleteArticle(Long articleId, Long userId) {
        // 先查询文章是否存在
        Article article = articleMapper.findById(articleId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // 检查是否是文章作者
        if (!article.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此文章");
        }
        
        // 提取文章内容中的所有 MinIO URL
        List<String> minioUrls = extractMinioUrlsFromHtml(article.getContent());
        
        // 删除 MinIO 中的文件
        if (!minioUrls.isEmpty()) {
            List<String> objectNames = new ArrayList<>();
            for (String url : minioUrls) {
                String objectName = fileService.extractObjectName(url);
                if (objectName != null && !objectName.isEmpty()) {
                    objectNames.add(objectName);
                }
            }
            if (!objectNames.isEmpty()) {
                fileService.deleteFiles(objectNames);
            }
        }
        
        // 删除文章
        int result = articleMapper.delete(articleId);
        if (result <= 0) {
            throw new RuntimeException("删除文章失败");
        }
    }
}

