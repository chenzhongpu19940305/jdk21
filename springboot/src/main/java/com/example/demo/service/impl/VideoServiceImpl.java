package com.example.demo.service.impl;

import com.example.demo.entity.Video;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.service.VideoService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.File;
import java.io.FileInputStream;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;
    
    @Value("${minio.endpoint}")
    private String minioEndpoint;
    
    @Autowired
    private VideoMapper videoMapper;

    /**
     * Spring Boot 启动时自动生成并上传默认封面
     */
    @PostConstruct
    public void initDefaultCover() {
        try {
            // 检查默认封面是否已存在
            try {
                minioClient.statObject(
                    StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object("image/default-cover.jpg")
                        .build()
                );
                System.out.println("默认封面已存在");
                return;
            } catch (Exception e) {
                // 封面不存在，继续创建
            }

            // 创建默认封面图片 (320x180)
            BufferedImage image = new BufferedImage(320, 180, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // 绘制渐变背景
            GradientPaint gradient = new GradientPaint(0, 0, new Color(102, 126, 234), 320, 180, new Color(118, 75, 162));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, 320, 180);

            // 绘制播放按钮
            g2d.setColor(new Color(255, 255, 255, 200));
            int[] xPoints = {140, 140, 190};
            int[] yPoints = {60, 120, 90};
            g2d.fillPolygon(xPoints, yPoints, 3);

            // 绘制文字
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            String text = "Video Cover";
            int x = (320 - fm.stringWidth(text)) / 2;
            int y = 150;
            g2d.drawString(text, x, y);
            g2d.dispose();

            // 保存为临时文件
            File tempFile = File.createTempFile("default_cover_", ".jpg");
            ImageIO.write(image, "jpg", tempFile);

            // 上传到 MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object("image/default-cover.jpg")
                    .stream(new FileInputStream(tempFile), tempFile.length(), -1)
                    .contentType("image/jpeg")
                    .build()
            );

            // 清理临时文件
            tempFile.delete();
            System.out.println("默认封面已生成并上传到 MinIO");

        } catch (Exception e) {
            System.err.println("生成默认封面失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String uploadVideo(MultipartFile file) throws Exception {
        try {
            // 确保存储桶存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // MinIO 中的对象名：统一存放在 video/ 目录下
            String fileName = UUID.randomUUID().toString() + fileExtension;
            String objectName = "video/" + fileName;

            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            // 生成封面图
            String coverUrl = generateCover(file, fileName);
            
            // 返回 JSON 格式：包含 videoFileName 和 coverUrl
            return "{\"videoFileName\":\"" + objectName + "\",\"coverUrl\":\"" + (coverUrl != null ? coverUrl : "") + "\"}";
        } catch (Exception e) {
            throw new Exception("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 生成视频封面（使用默认封面）
     */
    private String generateCover(MultipartFile file, String fileName) {
        try {
            // 使用默认封面 URL
            String endpoint = minioEndpoint;
            if (endpoint.endsWith("/")) {
                endpoint = endpoint.substring(0, endpoint.length() - 1);
            }
            
            // 返回默认封面 URL
            String defaultCoverUrl = endpoint + "/" + bucketName + "/image/default-cover.jpg";
            
            return defaultCoverUrl;
            
        } catch (Exception e) {
            System.err.println("生成封面失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public void updateVideoInfo(Video video) {
        if (video.getId() == null) {
            throw new IllegalArgumentException("视频ID不能为空");
        }
        // 只更新标题和描述，其他字段保持不变
        Video update = new Video();
        update.setId(video.getId());
        update.setTitle(video.getTitle());
        update.setDescription(video.getDescription());
        videoMapper.update(update);
    }

    @Override
    public ResponseEntity<Resource> getVideo(String fileName) throws Exception {
        try {
            String objectName = fileName;
            if (objectName != null && !objectName.startsWith("video/")) {
                objectName = "video/" + objectName;
            }
            // 获取文件信息
            StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );

            // 获取文件流
            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(stat.contentType()));
            headers.setContentLength(stat.size());
            headers.setContentDispositionFormData("inline", objectName);

            return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(stream));
        } catch (Exception e) {
            throw new Exception("获取视频失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Resource> getCover(String fileName) throws Exception {
        try {
            String objectName = fileName;
            if (objectName != null && !objectName.startsWith("image/")) {
                objectName = "image/" + objectName;
            }
            // 获取文件信息
            StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );

            // 获取文件流
            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(stat.size());
            headers.setCacheControl("max-age=86400");

            return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(stream));
        } catch (Exception e) {
            throw new Exception("获取封面失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Video saveVideoInfo(Video video) {
        if (video.getId() == null) {
            video.setCreateTime(LocalDateTime.now());
            video.setViewCount(0);
            video.setLikeCount(0);
            video.setCommentCount(0);
            video.setStatus(1); // 默认已发布，方便前台列表直接查询

            // 如果没有显式传入 fileUrl，但有 fileName，则自动拼接 MinIO 访问地址
            if (video.getFileUrl() == null && video.getFileName() != null) {
                String endpoint = minioEndpoint;
                if (endpoint.endsWith("/")) {
                    endpoint = endpoint.substring(0, endpoint.length() - 1);
                }
                video.setFileUrl(endpoint + "/" + bucketName + "/" + video.getFileName());
            }
            videoMapper.insert(video);
        } else {
            video.setUpdateTime(LocalDateTime.now());

            // 更新时同样保证 fileUrl 和 fileName 同步
            if (video.getFileUrl() == null && video.getFileName() != null) {
                String endpoint = minioEndpoint;
                if (endpoint.endsWith("/")) {
                    endpoint = endpoint.substring(0, endpoint.length() - 1);
                }
                video.setFileUrl(endpoint + "/" + bucketName + "/" + video.getFileName());
            }
            videoMapper.update(video);
        }
        return video;
    }

    @Override
    public Video getVideoById(Long id) {
        return videoMapper.selectById(id);
    }

    @Override
    public List<Video> listVideos(Integer pageNum, Integer pageSize, String keyword) {
        int offset = (pageNum - 1) * pageSize;
        return videoMapper.selectPage(offset, pageSize, keyword);
    }

    @Override
    public boolean updateVideoStatus(Long id, Integer status) {
        return videoMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional
    public boolean deleteVideo(Long id) {
        Video video = videoMapper.selectById(id);
        if (video != null) {
            try {
                // 删除MinIO中的文件
                minioClient.removeObject(
                    RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(video.getFileName())
                        .build()
                );
            } catch (Exception e) {
                throw new RuntimeException("删除视频文件失败: " + e.getMessage());
            }
            return videoMapper.deleteById(id) > 0;
        }
        return false;
    }

    @Override
    public List<String> listVideoFiles() throws Exception {
        List<String> videoList = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                videoList.add(item.objectName());
            }
        } catch (Exception e) {
            throw new Exception("获取视频列表失败: " + e.getMessage());
        }
        return videoList;
    }
}
