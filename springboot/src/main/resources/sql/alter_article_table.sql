-- 为文章表添加菜单ID和Tab代码字段
-- 如果表已存在，执行此脚本添加字段

USE demo_db;

-- 添加 menu_id 字段
ALTER TABLE article 
ADD COLUMN menu_id BIGINT COMMENT '所属菜单ID' AFTER user_id;

-- 添加 tab_code 字段
ALTER TABLE article 
ADD COLUMN tab_code VARCHAR(50) COMMENT '所属Tab代码' AFTER menu_id;

-- 添加索引
ALTER TABLE article 
ADD INDEX idx_menu_id (menu_id),
ADD INDEX idx_tab_code (tab_code);













