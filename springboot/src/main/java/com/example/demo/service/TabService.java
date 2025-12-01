package com.example.demo.service;

import com.example.demo.dto.CreateTabRequest;
import com.example.demo.entity.Tab;
import com.example.demo.mapper.TabMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Tab 服务类
 */
@Service
public class TabService {

    @Autowired
    private TabMapper tabMapper;

    /**
     * 获取所有启用的 Tab 列表
     */
    public List<Tab> getEnabledTabs() {
        return tabMapper.findAllEnabled();
    }

    /**
     * 获取所有 Tab 列表
     */
    public List<Tab> getAllTabs() {
        return tabMapper.findAll();
    }

    /**
     * 根据菜单ID获取 Tab 列表
     */
    public List<Tab> getTabsByMenuId(Long menuId) {
        return tabMapper.findByMenuId(menuId);
    }

    /**
     * 创建 Tab
     */
    public Tab createTab(CreateTabRequest request) {
        // 参数验证
        if (request.getMenuId() == null) {
            throw new RuntimeException("菜单ID不能为空");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Tab 名称不能为空");
        }
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new RuntimeException("Tab 代码不能为空");
        }

        // 检查同一菜单下是否已存在相同的 code
        List<Tab> existingTabs = tabMapper.findByMenuId(request.getMenuId());
        boolean codeExists = existingTabs.stream()
                .anyMatch(tab -> tab.getCode().equals(request.getCode()));
        if (codeExists) {
            throw new RuntimeException("该菜单下已存在相同的 Tab 代码");
        }

        Tab tab = new Tab();
        tab.setMenuId(request.getMenuId());
        tab.setName(request.getName().trim());
        tab.setCode(request.getCode().trim());
        tab.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        tab.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        int result = tabMapper.insert(tab);
        if (result > 0) {
            return tab;
        } else {
            throw new RuntimeException("创建 Tab 失败");
        }
    }

    /**
     * 删除 Tab
     */
    public void deleteTab(Long id) {
        if (id == null) {
            throw new RuntimeException("Tab ID 不能为空");
        }

        int result = tabMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除 Tab 失败，Tab 不存在");
        }
    }
}









