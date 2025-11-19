package com.example.demo.dto;

import java.util.List;

/**
 * 发布文章请求 DTO
 */
public class PublishArticleRequest {
    private String title;
    private String summary;
    private String content;
    private List<String> tags;
    private Long userId;
    private Long menuId;
    private String tabCode;

    public PublishArticleRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getTabCode() {
        return tabCode;
    }

    public void setTabCode(String tabCode) {
        this.tabCode = tabCode;
    }
}

