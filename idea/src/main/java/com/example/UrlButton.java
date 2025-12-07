package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * URL 按钮组件
 * 显示 URL 并提供打开和删除功能
 */
public class UrlButton extends JPanel {
    private final String url;
    private final BrowserLauncherPanel parent;
    private final JButton urlButton;
    private final JButton deleteButton;

    public UrlButton(String url, BrowserLauncherPanel parent) {
        this.url = url;
        this.parent = parent;
        
        setLayout(new BorderLayout(10, 0));
        setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // URL 按钮
        urlButton = new JButton(url);
        urlButton.setFont(new Font(urlButton.getFont().getName(), Font.PLAIN, 12));
        urlButton.setHorizontalAlignment(SwingConstants.LEFT);
        urlButton.setBackground(new Color(33, 150, 243)); // 蓝色
        urlButton.setForeground(Color.WHITE);
        urlButton.setFocusPainted(false);
        urlButton.setBorderPainted(false);
        urlButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        urlButton.addActionListener(e -> parent.openUrlInBrowser(url));
        add(urlButton, BorderLayout.CENTER);
        
        // 删除按钮
        deleteButton = new JButton("删除");
        deleteButton.setFont(new Font(deleteButton.getFont().getName(), Font.PLAIN, 11));
        deleteButton.setBackground(new Color(244, 67, 54)); // 红色
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setPreferredSize(new Dimension(60, 30));
        deleteButton.addActionListener(e -> parent.removeUrlButton(this));
        add(deleteButton, BorderLayout.EAST);
    }

    public String getUrl() {
        return url;
    }
}










