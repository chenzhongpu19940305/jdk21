package com.example.demo.service;

import com.example.demo.entity.Subtitle;
import com.example.demo.mapper.SubtitleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubtitleService {
    
    @Autowired
    private SubtitleMapper subtitleMapper;
    
    /**
     * 保存或更新字幕文本
     */
    public Subtitle saveSubtitle(Subtitle subtitle) {
        if (subtitle.getId() != null) {
            // 更新现有字幕
            subtitleMapper.update(subtitle);
        } else if (subtitle.getVideoId() != null) {
            // 检查该视频是否已有字幕
            Subtitle existing = subtitleMapper.selectByVideoId(subtitle.getVideoId());
            if (existing != null) {
                subtitle.setId(existing.getId());
                subtitleMapper.update(subtitle);
            } else {
                // 创建新字幕
                subtitleMapper.insert(subtitle);
            }
        } else {
            // 直接插入
            subtitleMapper.insert(subtitle);
        }
        return subtitle;
    }
    
    /**
     * 获取视频的字幕
     */
    public Subtitle getSubtitleByVideoId(Long videoId) {
        return subtitleMapper.selectByVideoId(videoId);
    }
    
    /**
     * 获取字幕详情
     */
    public Subtitle getSubtitleById(Long id) {
        return subtitleMapper.selectById(id);
    }
    
    /**
     * 删除字幕
     */
    public boolean deleteSubtitle(Long id) {
        return subtitleMapper.delete(id) > 0;
    }
    
    /**
     * 删除视频的所有字幕
     */
    public boolean deleteSubtitleByVideoId(Long videoId) {
        return subtitleMapper.deleteByVideoId(videoId) > 0;
    }
}
