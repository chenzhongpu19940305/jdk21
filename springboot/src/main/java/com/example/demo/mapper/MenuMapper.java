package com.example.demo.mapper;

import com.example.demo.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单 Mapper 接口
 */
@Mapper
public interface MenuMapper {
    /**
     * 查询所有启用的菜单及其 Tab，按排序顺序
     */
    List<Menu> findAllEnabledWithTabs();

    /**
     * 查询所有菜单
     */
    List<Menu> findAll();

    /**
     * 插入菜单
     */
    int insert(Menu menu);

    /**
     * 根据 ID 删除菜单
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据 code 查询菜单
     */
    Menu findByCode(@Param("code") String code);
}




