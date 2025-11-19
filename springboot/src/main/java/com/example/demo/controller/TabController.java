package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateTabRequest;
import com.example.demo.entity.Tab;
import com.example.demo.service.TabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tab 控制器
 */
@RestController
@RequestMapping("/api/tab")
public class TabController {

    @Autowired
    private TabService tabService;

    /**
     * 根据菜单ID获取 Tab 列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Tab>> getTabList(@RequestParam(required = false) Long menuId) {
        try {
            List<Tab> tabs;
            if (menuId != null) {
                tabs = tabService.getTabsByMenuId(menuId);
            } else {
                tabs = tabService.getEnabledTabs();
            }
            return ApiResponse.success(tabs);
        } catch (Exception e) {
            return ApiResponse.error("获取 Tab 列表失败：" + e.getMessage());
        }
    }

    /**
     * 创建 Tab
     */
    @PostMapping("/create")
    public ApiResponse<Tab> createTab(@RequestBody CreateTabRequest request) {
        try {
            Tab tab = tabService.createTab(request);
            return ApiResponse.success("创建成功", tab);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 删除 Tab
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTab(@PathVariable Long id) {
        try {
            tabService.deleteTab(id);
            return ApiResponse.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除失败：" + e.getMessage());
        }
    }
}





