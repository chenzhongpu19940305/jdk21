-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS demo_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE demo_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    email VARCHAR(100) COMMENT '邮箱',
    nickname VARCHAR(50) COMMENT '昵称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建文章表
CREATE TABLE IF NOT EXISTS article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    menu_id BIGINT COMMENT '所属菜单ID',
    tab_code VARCHAR(50) COMMENT '所属Tab代码',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary VARCHAR(500) COMMENT '文章摘要',
    content TEXT NOT NULL COMMENT '文章内容（HTML）',
    tags VARCHAR(200) COMMENT '文章标签（逗号分隔）',
    views INT DEFAULT 0 COMMENT '浏览量',
    likes INT DEFAULT 0 COMMENT '点赞数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_menu_id (menu_id),
    INDEX idx_tab_code (tab_code),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 创建菜单表
CREATE TABLE IF NOT EXISTS menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '菜单代码',
    icon VARCHAR(50) COMMENT '菜单图标',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- 创建 Tab 表
CREATE TABLE IF NOT EXISTS tab (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Tab ID',
    menu_id BIGINT NOT NULL COMMENT '所属菜单ID',
    name VARCHAR(50) NOT NULL COMMENT 'Tab 名称',
    code VARCHAR(50) NOT NULL COMMENT 'Tab 代码',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    INDEX idx_menu_id (menu_id),
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order),
    UNIQUE KEY uk_menu_code (menu_id, code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主页 Tab 表';

-- 插入默认菜单数据
INSERT INTO menu (name, code, icon, sort_order, status) VALUES
('综合', 'comprehensive', '📋', 1, 1),
('关注', 'follow', '⭐', 2, 1),
('后端', 'backend', '⚙️', 3, 1),
('前端', 'frontend', '💻', 4, 1),
('Android', 'android', '📱', 5, 1),
('iOS', 'ios', '🍎', 6, 1),
('人工智能', 'ai', '🤖', 7, 1),
('开发工具', 'tools', '🔧', 8, 1),
('代码人生', 'life', '💡', 9, 1),
('阅读', 'reading', '📖', 10, 1),
('排行榜', 'ranking', '🏆', 11, 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 插入默认 Tab 数据（关联到"综合"菜单）
-- 注意：如果 tab 表已存在但没有 menu_id 字段，请先执行 fix_tab_table.sql
INSERT INTO tab (menu_id, name, code, sort_order, status) 
SELECT 
    (SELECT id FROM menu WHERE code = 'comprehensive' LIMIT 1) as menu_id,
    '推荐' as name,
    'recommend' as code,
    1 as sort_order,
    1 as status
WHERE NOT EXISTS (SELECT 1 FROM tab WHERE code = 'recommend' AND menu_id = (SELECT id FROM menu WHERE code = 'comprehensive' LIMIT 1));

INSERT INTO tab (menu_id, name, code, sort_order, status) 
SELECT 
    (SELECT id FROM menu WHERE code = 'comprehensive' LIMIT 1) as menu_id,
    '最新' as name,
    'latest' as code,
    2 as sort_order,
    1 as status
WHERE NOT EXISTS (SELECT 1 FROM tab WHERE code = 'latest' AND menu_id = (SELECT id FROM menu WHERE code = 'comprehensive' LIMIT 1));

