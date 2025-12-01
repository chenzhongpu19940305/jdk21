package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Subtitle;
import com.example.demo.service.SubtitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subtitles")
public class SubtitleController {
    
    @Autowired
    private SubtitleService subtitleService;
    
    /**
     * 保存或更新字幕
     */
    @PostMapping
    public Result<Subtitle> saveSubtitle(@RequestBody Subtitle subtitle) {
        try {
            Subtitle saved = subtitleService.saveSubtitle(subtitle);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error("保存字幕失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取视频的字幕
     */
    @GetMapping("/video/{videoId}")
    public Result<Subtitle> getSubtitleByVideoId(@PathVariable Long videoId) {
        try {
            Subtitle subtitle = subtitleService.getSubtitleByVideoId(videoId);
            if (subtitle != null) {
                return Result.success(subtitle);
            } else {
                return Result.success(null);
            }
        } catch (Exception e) {
            return Result.error("获取字幕失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取字幕详情
     */
    @GetMapping("/{id}")
    public Result<Subtitle> getSubtitleById(@PathVariable Long id) {
        try {
            Subtitle subtitle = subtitleService.getSubtitleById(id);
            if (subtitle != null) {
                return Result.success(subtitle);
            } else {
                return Result.error("字幕不存在");
            }
        } catch (Exception e) {
            return Result.error("获取字幕失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除字幕
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteSubtitle(@PathVariable Long id) {
        try {
            boolean success = subtitleService.deleteSubtitle(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("字幕不存在或删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
