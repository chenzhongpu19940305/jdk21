package com.example.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Subtitle {
    private Long id;
    private Long videoId;
    private String content;
    private Integer fontSize;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
