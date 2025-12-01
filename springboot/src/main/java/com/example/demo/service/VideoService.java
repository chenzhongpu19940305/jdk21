package com.example.demo.service;

import com.example.demo.entity.Video;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    String uploadVideo(MultipartFile file) throws Exception;
    
    Video saveVideoInfo(Video video);
    
    Video getVideoById(Long id);
    
    List<Video> listVideos(Integer pageNum, Integer pageSize, String keyword);
    
    boolean updateVideoStatus(Long id, Integer status);
    
    boolean deleteVideo(Long id);
    
    ResponseEntity<Resource> getVideo(String fileName) throws Exception;

    ResponseEntity<Resource> getCover(String fileName) throws Exception;

    void updateVideoInfo(Video video);

    List<String> listVideoFiles() throws Exception;
}
