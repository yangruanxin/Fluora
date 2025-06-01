package org.whu.fleetingtime.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.travelpost.UploadImgResponseDto;
import org.whu.fleetingtime.entity.TravelPostImage;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.interfaces.ITravelPostService;
import org.whu.fleetingtime.repository.TravelPostImageRepository;
import org.whu.fleetingtime.util.AliyunOssUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TravelPostService implements ITravelPostService {

    private static final Logger logger = LoggerFactory.getLogger(TravelPostService.class); // 定义 Logger 对象

    private final TravelPostImageRepository travelPostImageRepository;

    @Override
    @Transactional // 保证数据库操作的原子性
    public UploadImgResponseDto uploadImage(MultipartFile file, String userId) throws IOException {
        logger.info("【图片上传服务】开始处理用户 {} 的图片上传请求，文件名: {}", userId, file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("【图片上传服务】用户 {} 上传的文件为空", userId);
            throw new BizException("上传的文件不能为空");
        }

        // 1. 生成在OSS中的唯一对象名 (objectKey)
        String originalFilename = file.getOriginalFilename();
        String extension = "." + FilenameUtils.getExtension(originalFilename);
        String objectName = "travel-posts/images/" + userId + "/" + UUID.randomUUID() + extension;
        logger.debug("【图片上传服务】为用户 {} 的图片 {} 生成的OSS objectKey: {}", userId, originalFilename, objectName);

        // 2. 上传文件到OSS
        logger.info("【图片上传服务】准备将图片 {} (objectKey: {}) 上传到OSS...", originalFilename, objectName);
        String imageUrl;
        try (InputStream inputStream = file.getInputStream()) {
            AliyunOssUtil.upload(objectName, inputStream);
            imageUrl = AliyunOssUtil.generatePresignedGetUrl(objectName, 5 * 60 * 1000);
            logger.info("【图片上传服务】图片 {} (objectKey: {}) 成功上传到OSS，访问URL: {}", originalFilename, objectName, imageUrl);
        } catch (Exception e) {
            logger.error("【图片上传服务】用户 {} 上传图片 {} (objectKey: {}) 到OSS失败: {}", userId, originalFilename, objectName, e.getMessage(), e);
            throw new BizException("上传图片失败");
        }

        // 3. 创建 TravelPostImage 实体并保存到数据库
        TravelPostImage travelPostImage = new TravelPostImage();
        travelPostImage.setObjectKey(objectName);
        travelPostImage.setSortOrder(0); // 默认排序值
        // ID 和 createdTime 由注解自动生成
        logger.info("【图片上传服务】准备将图片信息 (objectKey: {}) 保存到数据库...", objectName);
        TravelPostImage savedImage = travelPostImageRepository.save(travelPostImage);
        logger.info("【图片上传服务】图片信息成功保存到数据库，图片ID: {}, objectKey: {}", savedImage.getId(), savedImage.getObjectKey());

        // 4. 构建并返回响应 DTO
        UploadImgResponseDto responseDto = UploadImgResponseDto.builder()
                .imageId(savedImage.getId())
                .objectKey(savedImage.getObjectKey())
                .url(imageUrl)
                .createdTime(savedImage.getCreatedTime())
                .build();
        logger.info("【图片上传服务】用户 {} 的图片上传请求处理完成，返回imageId: {}", userId, responseDto.getImageId());
        return responseDto;
    }
}
