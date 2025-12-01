package com.example.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Video {
    private Long id;
    private String title;
    private String description;
    private String fileName;
    private String fileUrl;
    private String coverUrl;
    private Long userId;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status; // 0-待审核 1-已发布 2-已下架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
