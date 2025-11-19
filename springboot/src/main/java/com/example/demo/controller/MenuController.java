package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateMenuRequest;
import com.example.demo.entity.Menu;
import com.example.demo.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取菜单列表（包含 Tab 子项）
     */
    @GetMapping("/list")
    public ApiResponse<List<Menu>> getMenuList() {
        try {
            List<Menu> menus = menuService.getEnabledMenusWithTabs();
            return ApiResponse.success(menus);
        } catch (Exception e) {
            return ApiResponse.error("获取菜单列表失败：" + e.getMessage());
        }
    }

    /**
     * 创建菜单
     */
    @PostMapping("/create")
    public ApiResponse<Menu> createMenu(@RequestBody CreateMenuRequest request) {
        try {
            Menu menu = menuService.createMenu(request);
            return ApiResponse.success("创建成功", menu);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMenu(@PathVariable Long id) {
        try {
            menuService.deleteMenu(id);
            return ApiResponse.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除失败：" + e.getMessage());
        }
    }
}




