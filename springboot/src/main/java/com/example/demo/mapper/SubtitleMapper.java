package com.example.demo.mapper;

import com.example.demo.entity.Subtitle;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SubtitleMapper {
    
    @Insert("INSERT INTO subtitle (video_id, content, font_size, user_id, create_time, update_time) " +
            "VALUES (#{videoId}, #{content}, #{fontSize}, #{userId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Subtitle subtitle);
    
    @Select("SELECT * FROM subtitle WHERE id = #{id}")
    Subtitle selectById(Long id);
    
    @Select("SELECT * FROM subtitle WHERE video_id = #{videoId} ORDER BY update_time DESC LIMIT 1")
    Subtitle selectByVideoId(Long videoId);
    
    @Update("UPDATE subtitle SET content = #{content}, font_size = #{fontSize}, update_time = NOW() " +
            "WHERE id = #{id}")
    int update(Subtitle subtitle);
    
    @Update("UPDATE subtitle SET content = #{content}, font_size = #{fontSize}, update_time = NOW() " +
            "WHERE video_id = #{videoId}")
    int updateByVideoId(Subtitle subtitle);
    
    @Delete("DELETE FROM subtitle WHERE id = #{id}")
    int delete(Long id);
    
    @Delete("DELETE FROM subtitle WHERE video_id = #{videoId}")
    int deleteByVideoId(Long videoId);
}
