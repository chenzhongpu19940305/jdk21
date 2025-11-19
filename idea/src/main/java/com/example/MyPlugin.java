package com.example;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * 浏览器启动器插件主类
 * 在 IDE 启动时执行初始化操作
 */
public class MyPlugin implements StartupActivity {
    
    @Override
    public void runActivity(@NotNull Project project) {
        // 插件启动时的初始化代码
        System.out.println("浏览器启动器插件已加载！");
    }
}

