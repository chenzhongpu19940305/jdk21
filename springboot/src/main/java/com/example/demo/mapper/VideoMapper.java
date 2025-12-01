package com.example.demo.mapper;

import com.example.demo.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMapper {

    Video selectById(@Param("id") Long id);

    List<Video> selectPage(@Param("offset") int offset,
                           @Param("pageSize") int pageSize,
                           @Param("keyword") String keyword);

    int insert(Video video);

    int update(Video video);

    int deleteById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
