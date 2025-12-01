-- 创建字幕表
CREATE TABLE IF NOT EXISTS subtitle (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字幕ID',
    video_id BIGINT NOT NULL COMMENT '视频ID',
    content LONGTEXT COMMENT '字幕内容',
    font_size INT DEFAULT 16 COMMENT '字体大小',
    user_id BIGINT COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_video_id (video_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字幕表';
