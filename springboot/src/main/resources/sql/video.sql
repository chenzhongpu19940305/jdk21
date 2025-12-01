-- 视频表 t_video

CREATE TABLE IF NOT EXISTS `t_video` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`        VARCHAR(255) NOT NULL               COMMENT '视频标题',
  `description`  TEXT                                 COMMENT '视频描述',
  `file_name`    VARCHAR(255) NOT NULL               COMMENT '视频文件名（MinIO 对象名）',
  `file_url`     VARCHAR(500)                         COMMENT '视频访问 URL（可选）',
  `cover_url`    VARCHAR(500)                         COMMENT '封面图 URL（可选）',
  `user_id`      BIGINT                               COMMENT '上传用户ID',
  `view_count`   INT          DEFAULT 0               COMMENT '播放次数',
  `like_count`   INT          DEFAULT 0               COMMENT '点赞数',
  `comment_count` INT         DEFAULT 0               COMMENT '评论数',
  `status`       TINYINT      DEFAULT 0               COMMENT '状态：0-待审核 1-已发布 2-已下架',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='视频表';
