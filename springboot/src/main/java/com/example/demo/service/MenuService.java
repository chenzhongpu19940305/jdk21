package com.example.demo.service;

import com.example.demo.dto.CreateMenuRequest;
import com.example.demo.entity.Menu;
import com.example.demo.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单服务类
 */
@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 获取所有启用的菜单及其 Tab 列表
     */
    public List<Menu> getEnabledMenusWithTabs() {
        List<Menu> menus = menuMapper.findAllEnabledWithTabs();
        
        // 将结果按菜单分组，合并 Tab
        Map<Long, Menu> menuMap = new LinkedHashMap<>();
        
        for (Menu menu : menus) {
            Menu targetMenu = menuMap.get(menu.getId());
            if (targetMenu == null) {
                targetMenu = new Menu();
                targetMenu.setId(menu.getId());
                targetMenu.setName(menu.getName());
                targetMenu.setCode(menu.getCode());
                targetMenu.setIcon(menu.getIcon());
                targetMenu.setSortOrder(menu.getSortOrder());
                targetMenu.setStatus(menu.getStatus());
                targetMenu.setTabs(new ArrayList<>());
                menuMap.put(menu.getId(), targetMenu);
            }
            
            // 添加 Tab 到对应的菜单
            if (menu.getTabs() != null && !menu.getTabs().isEmpty()) {
                for (com.example.demo.entity.Tab tab : menu.getTabs()) {
                    // 检查是否已存在相同的 Tab
                    boolean exists = targetMenu.getTabs().stream()
                        .anyMatch(t -> t.getId().equals(tab.getId()));
                    if (!exists) {
                        targetMenu.getTabs().add(tab);
                    }
                }
            }
        }

        return new ArrayList<>(menuMap.values());
    }

    /**
     * 创建菜单
     */
    public Menu createMenu(CreateMenuRequest request) {
        // 参数验证
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("菜单名称不能为空");
        }
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new RuntimeException("菜单代码不能为空");
        }

        // 检查 code 是否已存在
        Menu existingMenu = menuMapper.findByCode(request.getCode().trim());
        if (existingMenu != null) {
            throw new RuntimeException("菜单代码已存在");
        }

        Menu menu = new Menu();
        menu.setName(request.getName().trim());
        menu.setCode(request.getCode().trim());
        menu.setIcon(request.getIcon() != null ? request.getIcon().trim() : "📋");
        menu.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        menu.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        int result = menuMapper.insert(menu);
        if (result > 0) {
            return menu;
        } else {
            throw new RuntimeException("创建菜单失败");
        }
    }

    /**
     * 删除菜单
     */
    public void deleteMenu(Long id) {
        if (id == null) {
            throw new RuntimeException("菜单 ID 不能为空");
        }

        int result = menuMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除菜单失败，菜单不存在");
        }
    }
}

