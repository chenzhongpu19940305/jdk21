package com.example.demo.mapper;

import com.example.demo.entity.Tab;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Tab Mapper 接口
 */
@Mapper
public interface TabMapper {
    /**
     * 查询所有启用的 Tab，按排序顺序
     */
    List<Tab> findAllEnabled();

    /**
     * 查询所有 Tab
     */
    List<Tab> findAll();

    /**
     * 插入 Tab
     */
    int insert(Tab tab);

    /**
     * 根据 ID 删除 Tab
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据菜单ID查询 Tab 列表
     */
    List<Tab> findByMenuId(@Param("menuId") Long menuId);
}




