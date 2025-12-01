package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 上传图片
     */
    @PostMapping("/upload/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error(400, "文件不能为空");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.error(400, "只能上传图片文件");
            }

            // 验证文件大小（限制10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return ApiResponse.error(400, "图片大小不能超过10MB");
            }

            String url = fileService.uploadImage(file);
            return ApiResponse.success("上传成功", url);
        } catch (Exception e) {
            return ApiResponse.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传视频
     */
    @PostMapping("/upload/video")
    public ApiResponse<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ApiResponse.error(400, "文件不能为空");
            }

            // 验证文件类型（放宽验证，支持更多视频格式）
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();
            boolean isVideoFile = false;
            
            if (contentType != null && contentType.startsWith("video/")) {
                isVideoFile = true;
            } else if (originalFilename != null) {
                // 通过文件扩展名判断
                String lowerFilename = originalFilename.toLowerCase();
                if (lowerFilename.endsWith(".mp4") || lowerFilename.endsWith(".webm") || 
                    lowerFilename.endsWith(".ogg") || lowerFilename.endsWith(".avi") ||
                    lowerFilename.endsWith(".mov") || lowerFilename.endsWith(".wmv") ||
                    lowerFilename.endsWith(".flv") || lowerFilename.endsWith(".mkv")) {
                    isVideoFile = true;
                }
            }
            
            if (!isVideoFile) {
                return ApiResponse.error(400, "只能上传视频文件（支持 mp4, webm, ogg, avi, mov, wmv, flv, mkv 格式）");
            }

            // 验证文件大小（限制100MB）
            if (file.getSize() > 100 * 1024 * 1024) {
                return ApiResponse.error(400, "视频大小不能超过100MB");
            }

            String url = fileService.uploadVideo(file);
            return ApiResponse.success("上传成功", url);
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误堆栈
            return ApiResponse.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件（代理访问 MinIO）
     * 支持两种方式：
     * 1. GET /api/file/get/images/uuid.jpg - 从路径中获取对象名称
     * 2. GET /api/file/get?url=http://localhost:9000/demo-bucket/images/uuid.jpg - 从URL参数中获取
     */
    @GetMapping("/get/**")
    public ResponseEntity<InputStreamResource> getFile(@RequestParam(required = false) String url) {
        try {
            String objectName;
            if (url != null && !url.isEmpty()) {
                // 从 URL 参数中提取对象名称
                objectName = fileService.extractObjectName(url);
            } else {
                // 从路径中提取对象名称
                // 路径格式：/api/file/get/images/uuid.jpg
                String path = request.getRequestURI();
                String prefix = "/api/file/get/";
                if (path.startsWith(prefix)) {
                    objectName = path.substring(prefix.length());
                } else {
                    return ResponseEntity.notFound().build();
                }
            }

            if (objectName == null || objectName.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            InputStream inputStream = fileService.getFile(objectName);

            // 根据文件扩展名确定 Content-Type
            String contentType = "application/octet-stream";
            if (objectName.endsWith(".jpg") || objectName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (objectName.endsWith(".png")) {
                contentType = "image/png";
            } else if (objectName.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (objectName.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (objectName.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (objectName.endsWith(".webm")) {
                contentType = "video/webm";
            } else if (objectName.endsWith(".ogg")) {
                contentType = "video/ogg";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("public, max-age=31536000"); // 缓存1年

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}


