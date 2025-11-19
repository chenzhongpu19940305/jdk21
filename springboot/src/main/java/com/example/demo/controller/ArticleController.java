package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PublishArticleRequest;
import com.example.demo.entity.Article;
import com.example.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 发布文章
     * 注意：实际项目中应该从 JWT Token 中获取用户ID，这里简化处理
     */
    @PostMapping("/publish")
    public ApiResponse<Article> publishArticle(@RequestBody PublishArticleRequest request) {
        try {
            // 参数验证
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return ApiResponse.error(400, "文章标题不能为空");
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ApiResponse.error(400, "文章内容不能为空");
            }

            // 从请求中获取用户ID
            Long userId = request.getUserId();
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            
            Article article = articleService.publishArticle(userId, request);
            return ApiResponse.success("发布成功", article);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("发布失败：" + e.getMessage());
        }
    }

    /**
     * 获取文章列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getArticleList(
            @RequestParam(required = false) Long menuId,
            @RequestParam(required = false) String tabCode,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<Article> articles;
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 搜索文章
                articles = articleService.searchArticles(keyword, page, pageSize);
            } else if (menuId != null && tabCode != null && !tabCode.trim().isEmpty()) {
                // 按菜单和Tab查询（必须同时指定 menuId 和 tabCode）
                articles = articleService.getArticleListByMenuAndTab(menuId, tabCode, page, pageSize);
            } else {
                // 查询所有文章
                articles = articleService.getArticleList(page, pageSize);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("list", articles);
            result.put("page", page);
            result.put("pageSize", pageSize);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取文章列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Article> getArticleDetail(@PathVariable Long id) {
        try {
            Article article = articleService.getArticleDetail(id);
            return ApiResponse.success(article);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取文章详情失败：" + e.getMessage());
        }
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public ApiResponse<Article> updateArticle(@PathVariable Long id, @RequestBody PublishArticleRequest request) {
        try {
            // 参数验证
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return ApiResponse.error(400, "文章标题不能为空");
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ApiResponse.error(400, "文章内容不能为空");
            }

            // 从请求中获取用户ID
            Long userId = request.getUserId();
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            
            Article article = articleService.updateArticle(id, userId, request);
            return ApiResponse.success("更新成功", article);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteArticle(@PathVariable Long id, @RequestParam Long userId) {
        try {
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            
            articleService.deleteArticle(id, userId);
            return ApiResponse.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除失败：" + e.getMessage());
        }
    }
}

