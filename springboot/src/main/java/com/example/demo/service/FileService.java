package com.example.demo.service;

import io.minio.MinioClient;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;

/**
 * 文件服务类
 */
@Service
public class FileService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 初始化时检查并创建bucket
     */
    @PostConstruct
    public void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化MinIO bucket失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传文件到 MinIO
     * @param file 文件
     * @param folder 文件夹路径（如：images/、videos/）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = folder + UUID.randomUUID().toString() + extension;

            // 上传文件
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 返回文件访问URL
            return endpoint + "/" + bucket + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        return uploadFile(file, "images/");
    }

    /**
     * 上传视频
     */
    public String uploadVideo(MultipartFile file) {
        return uploadFile(file, "videos/");
    }

    /**
     * 从 MinIO 获取文件流
     * @param objectName 对象名称（如：images/uuid.jpg）
     * @return 文件输入流
     */
    public InputStream getFile(String objectName) {
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
            return response;
        } catch (Exception e) {
            throw new RuntimeException("获取文件失败：" + e.getMessage(), e);
        }
    }

    /**
     * 从 URL 中提取对象名称
     * 支持两种格式：
     * 1. 直接的 MinIO URL：http://localhost:9000/demo-bucket/images/uuid.jpg
     * 2. 代理 URL：/api/file/get/images/uuid.jpg 或 http://localhost:8080/api/file/get/images/uuid.jpg
     * @param url MinIO URL 或代理 URL
     * @return 对象名称（如：images/uuid.jpg）
     */
    public String extractObjectName(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        // 处理代理 URL：/api/file/get/images/uuid.jpg 或 http://localhost:8080/api/file/get/images/uuid.jpg
        String proxyPrefix = "/api/file/get/";
        if (url.contains(proxyPrefix)) {
            int index = url.indexOf(proxyPrefix);
            String objectName = url.substring(index + proxyPrefix.length());
            // 移除可能的查询参数
            int queryIndex = objectName.indexOf('?');
            if (queryIndex > 0) {
                objectName = objectName.substring(0, queryIndex);
            }
            return objectName;
        }
        
        // 处理直接的 MinIO URL：http://localhost:9000/demo-bucket/images/uuid.jpg
        String prefix = endpoint + "/" + bucket + "/";
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        
        // 如果 URL 不包含完整路径，尝试直接使用（可能是相对路径）
        return url;
    }

    /**
     * 删除 MinIO 中的文件
     * @param objectName 对象名称（如：images/uuid.jpg）
     */
    public void deleteFile(String objectName) {
        try {
            if (objectName == null || objectName.isEmpty()) {
                return;
            }
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            // 记录错误但不抛出异常，避免影响文章删除流程
            System.err.println("删除 MinIO 文件失败：" + objectName + ", 错误：" + e.getMessage());
        }
    }

    /**
     * 批量删除 MinIO 中的文件
     * @param objectNames 对象名称列表
     */
    public void deleteFiles(java.util.List<String> objectNames) {
        if (objectNames == null || objectNames.isEmpty()) {
            return;
        }
        for (String objectName : objectNames) {
            deleteFile(objectName);
        }
    }
}

