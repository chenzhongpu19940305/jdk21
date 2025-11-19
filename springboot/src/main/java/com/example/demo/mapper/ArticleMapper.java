package com.example.demo.mapper;

import com.example.demo.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章 Mapper 接口
 */
@Mapper
public interface ArticleMapper {
    /**
     * 插入文章
     */
    int insert(Article article);

    /**
     * 根据 ID 查询文章
     */
    Article findById(@Param("id") Long id);

    /**
     * 查询文章列表
     */
    List<Article> findList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据用户 ID 查询文章列表
     */
    List<Article> findByUserId(@Param("userId") Long userId);

    /**
     * 根据菜单ID和Tab代码查询文章列表
     */
    List<Article> findByMenuIdAndTabCode(
            @Param("menuId") Long menuId,
            @Param("tabCode") String tabCode,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    /**
     * 更新文章
     */
    int update(Article article);

    /**
     * 删除文章
     */
    int delete(@Param("id") Long id);

    /**
     * 搜索文章（按标题和内容）
     */
    List<Article> searchArticles(
            @Param("keyword") String keyword,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);
}

