"""
浏览器启动器 - Windows 客户端
功能：输入 URL，点击按钮在谷歌浏览器中打开对应页面
"""

import tkinter as tk
from tkinter import ttk, messagebox
import webbrowser
import subprocess
import os
import sys


class BrowserLauncher:
    def __init__(self, root):
        self.root = root
        self.root.title("浏览器启动器")
        self.root.geometry("600x400")
        self.root.resizable(False, False)
        
        # 存储 URL 和按钮的列表
        self.url_buttons = []
        
        self.setup_ui()
        
    def setup_ui(self):
        # 标题
        title_label = tk.Label(
            self.root,
            text="浏览器启动器",
            font=("Microsoft YaHei", 18, "bold"),
            pady=20
        )
        title_label.pack()
        
        # URL 输入区域
        input_frame = tk.Frame(self.root, pady=20)
        input_frame.pack(fill=tk.X, padx=20)
        
        url_label = tk.Label(
            input_frame,
            text="输入 URL:",
            font=("Microsoft YaHei", 12)
        )
        url_label.pack(anchor=tk.W)
        
        # URL 输入框和按钮的容器
        input_container = tk.Frame(input_frame)
        input_container.pack(fill=tk.X, pady=10)
        
        self.url_entry = tk.Entry(
            input_container,
            font=("Microsoft YaHei", 11),
            width=50
        )
        self.url_entry.pack(side=tk.LEFT, fill=tk.X, expand=True, padx=(0, 10))
        self.url_entry.bind('<Return>', lambda e: self.add_url_button())
        
        add_button = tk.Button(
            input_container,
            text="添加",
            font=("Microsoft YaHei", 11),
            command=self.add_url_button,
            bg="#4CAF50",
            fg="white",
            padx=20,
            cursor="hand2"
        )
        add_button.pack(side=tk.LEFT)
        
        # 按钮列表区域
        list_frame = tk.Frame(self.root)
        list_frame.pack(fill=tk.BOTH, expand=True, padx=20, pady=10)
        
        # 滚动条
        scrollbar = tk.Scrollbar(list_frame)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        
        # 按钮容器（使用 Canvas 实现滚动）
        self.canvas = tk.Canvas(
            list_frame,
            yscrollcommand=scrollbar.set,
            bg="#f0f0f0"
        )
        self.canvas.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scrollbar.config(command=self.canvas.yview)
        
        # 按钮框架（放在 Canvas 中）
        self.button_frame = tk.Frame(self.canvas, bg="#f0f0f0")
        self.canvas_window = self.canvas.create_window(
            (0, 0),
            window=self.button_frame,
            anchor=tk.NW
        )
        
        # 绑定 Canvas 大小变化事件
        self.button_frame.bind('<Configure>', self.on_frame_configure)
        self.canvas.bind('<Configure>', self.on_canvas_configure)
        
        # 提示文本
        tip_label = tk.Label(
            self.root,
            text="提示：输入 URL 后按回车或点击'添加'按钮创建快捷按钮",
            font=("Microsoft YaHei", 9),
            fg="gray",
            pady=10
        )
        tip_label.pack()
        
    def on_frame_configure(self, event):
        """更新滚动区域"""
        self.canvas.configure(scrollregion=self.canvas.bbox("all"))
        
    def on_canvas_configure(self, event):
        """调整内部框架宽度"""
        canvas_width = event.width
        self.canvas.itemconfig(self.canvas_window, width=canvas_width)
        
    def get_chrome_path(self):
        """获取 Chrome 浏览器路径"""
        possible_paths = [
            r"C:\Program Files\Google\Chrome\Application\chrome.exe",
            r"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe",
            os.path.expanduser(r"~\AppData\Local\Google\Chrome\Application\chrome.exe"),
        ]
        
        for path in possible_paths:
            if os.path.exists(path):
                return path
        return None
        
    def open_url_in_chrome(self, url):
        """在 Chrome 浏览器中打开 URL"""
        chrome_path = self.get_chrome_path()
        
        if chrome_path:
            try:
                # 使用 Chrome 打开 URL
                subprocess.Popen([chrome_path, url])
            except Exception as e:
                messagebox.showerror("错误", f"无法打开 Chrome 浏览器：{str(e)}")
        else:
            # 如果找不到 Chrome，使用默认浏览器
            try:
                webbrowser.open(url)
            except Exception as e:
                messagebox.showerror("错误", f"无法打开浏览器：{str(e)}")
    
    def normalize_url(self, url):
        """规范化 URL"""
        url = url.strip()
        if not url:
            return None
        
        # 如果没有协议，添加 https://
        if not url.startswith(('http://', 'https://')):
            url = 'https://' + url
            
        return url
    
    def add_url_button(self):
        """添加 URL 按钮"""
        url = self.url_entry.get().strip()
        
        if not url:
            messagebox.showwarning("警告", "请输入 URL")
            return
        
        # 规范化 URL
        normalized_url = self.normalize_url(url)
        if not normalized_url:
            return
        
        # 创建按钮容器
        button_container = tk.Frame(self.button_frame, bg="#f0f0f0", pady=5)
        button_container.pack(fill=tk.X, padx=10, pady=5)
        
        # 创建按钮
        url_button = tk.Button(
            button_container,
            text=normalized_url,
            font=("Microsoft YaHei", 10),
            command=lambda u=normalized_url: self.open_url_in_chrome(u),
            bg="#2196F3",
            fg="white",
            padx=15,
            pady=8,
            cursor="hand2",
            anchor=tk.W,
            justify=tk.LEFT
        )
        url_button.pack(side=tk.LEFT, fill=tk.X, expand=True)
        
        # 创建删除按钮
        delete_button = tk.Button(
            button_container,
            text="删除",
            font=("Microsoft YaHei", 9),
            command=lambda container=button_container: self.delete_button(container),
            bg="#f44336",
            fg="white",
            padx=10,
            cursor="hand2"
        )
        delete_button.pack(side=tk.RIGHT, padx=(10, 0))
        
        # 保存引用
        self.url_buttons.append({
            'container': button_container,
            'url': normalized_url
        })
        
        # 清空输入框
        self.url_entry.delete(0, tk.END)
        
        # 更新滚动区域
        self.root.update_idletasks()
        self.canvas.configure(scrollregion=self.canvas.bbox("all"))
    
    def delete_button(self, container):
        """删除按钮"""
        # 从列表中移除
        self.url_buttons = [item for item in self.url_buttons if item['container'] != container]
        # 销毁容器
        container.destroy()
        # 更新滚动区域
        self.root.update_idletasks()
        self.canvas.configure(scrollregion=self.canvas.bbox("all"))


def main():
    root = tk.Tk()
    app = BrowserLauncher(root)
    root.mainloop()


if __name__ == "__main__":
    main()

