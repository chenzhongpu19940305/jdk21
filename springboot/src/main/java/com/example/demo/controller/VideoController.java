package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Video;
import com.example.demo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("/upload")
    public Result<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = videoService.uploadVideo(file);
            return Result.success(fileName);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/info")
    public Result<Video> saveVideoInfo(@RequestBody Video video) {
        try {
            Video savedVideo = videoService.saveVideoInfo(video);
            return Result.success(savedVideo);
        } catch (Exception e) {
            return Result.error("保存视频信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Video> getVideoInfo(@PathVariable Long id) {
        try {
            Video video = videoService.getVideoById(id);
            if (video != null) {
                return Result.success(video);
            } else {
                return Result.error("视频不存在");
            }
        } catch (Exception e) {
            return Result.error("获取视频信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Video>> listVideos(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            List<Video> videos = videoService.listVideos(pageNum, pageSize, keyword);
            return Result.success(videos);
        } catch (Exception e) {
            return Result.error("获取视频列表失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<?> updateVideoStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = videoService.updateVideoStatus(id, status);
            if (success) {
                return Result.success("更新状态成功");
            } else {
                return Result.error("视频不存在或更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新状态失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateVideo(@PathVariable Long id, @RequestBody Video video) {
        video.setId(id);
        videoService.updateVideoInfo(video);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteVideo(@PathVariable Long id) {
        try {
            boolean success = videoService.deleteVideo(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("视频不存在或删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/play/{fileName}")
    public ResponseEntity<Resource> playVideo(@PathVariable String fileName) {
        try {
            return videoService.getVideo(fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cover/{fileName}")
    public ResponseEntity<Resource> getCover(@PathVariable String fileName) {
        try {
            return videoService.getCover(fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/files")
    public Result<List<String>> listVideoFiles() {
        try {
            List<String> files = videoService.listVideoFiles();
            return Result.success(files);
        } catch (Exception e) {
            return Result.error("获取视频文件列表失败: " + e.getMessage());
        }
    }
}
