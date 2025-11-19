package com.example;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 浏览器启动器面板
 * 提供 URL 输入和按钮管理功能
 */
public class BrowserLauncherPanel extends JPanel {
    private final Project project;
    private final JTextField urlField;
    private final JPanel buttonPanel;
    private final List<UrlButton> urlButtons;
    private final JScrollPane scrollPane;

    public BrowserLauncherPanel(@NotNull Project project) {
        this.project = project;
        this.urlButtons = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 顶部输入区域
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);
        
        // 中间按钮列表区域
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        scrollPane = new JBScrollPane(buttonPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        
        // 底部提示信息
        JLabel tipLabel = new JLabel("提示：输入 URL 后按回车或点击'添加'按钮创建快捷按钮");
        tipLabel.setFont(new Font(tipLabel.getFont().getName(), Font.PLAIN, 11));
        tipLabel.setForeground(Color.GRAY);
        tipLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(tipLabel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JLabel label = new JLabel("输入 URL:");
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 12));
        panel.add(label, BorderLayout.NORTH);
        
        JPanel inputContainer = new JPanel(new BorderLayout(10, 0));
        
        urlField = new JTextField();
        urlField.setFont(new Font(urlField.getFont().getName(), Font.PLAIN, 12));
        urlField.addActionListener(e -> addUrlButton());
        inputContainer.add(urlField, BorderLayout.CENTER);
        
        JButton addButton = new JButton("添加");
        addButton.setFont(new Font(addButton.getFont().getName(), Font.PLAIN, 12));
        addButton.addActionListener(e -> addUrlButton());
        inputContainer.add(addButton, BorderLayout.EAST);
        
        panel.add(inputContainer, BorderLayout.CENTER);
        
        return panel;
    }

    private void addUrlButton() {
        String url = urlField.getText().trim();
        
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "请输入 URL",
                "提示",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // 规范化 URL
        String normalizedUrl = normalizeUrl(url);
        if (normalizedUrl == null) {
            return;
        }
        
        // 创建 URL 按钮
        UrlButton urlButton = new UrlButton(normalizedUrl, this);
        urlButtons.add(urlButton);
        
        // 添加到面板
        buttonPanel.add(urlButton);
        buttonPanel.revalidate();
        buttonPanel.repaint();
        
        // 滚动到底部
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
        
        // 清空输入框
        urlField.setText("");
        urlField.requestFocus();
    }

    private String normalizeUrl(String url) {
        url = url.trim();
        if (url.isEmpty()) {
            return null;
        }
        
        // 如果没有协议，添加 https://
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        
        return url;
    }

    void openUrlInBrowser(String url) {
        try {
            // 使用 Desktop API 打开浏览器
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                // 备用方案：使用命令行
                String os = System.getProperty("os.name").toLowerCase();
                Runtime runtime = Runtime.getRuntime();
                
                if (os.contains("win")) {
                    // Windows
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    // macOS
                    runtime.exec("open " + url);
                } else {
                    // Linux
                    runtime.exec("xdg-open " + url);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                this,
                "无法打开浏览器: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    void removeUrlButton(UrlButton button) {
        urlButtons.remove(button);
        buttonPanel.remove(button);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
}





