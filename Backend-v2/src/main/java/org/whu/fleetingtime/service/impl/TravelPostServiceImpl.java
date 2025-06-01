package org.whu.fleetingtime.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.travelpost.ImageAssociationDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostCreateRequestDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostCreateResponseDTO;
import org.whu.fleetingtime.dto.travelpost.UploadImgResponseDto;
import org.whu.fleetingtime.entity.TravelPost;
import org.whu.fleetingtime.entity.TravelPostImage;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.repository.TravelPostImageRepository;
import org.whu.fleetingtime.repository.TravelPostRepository;
import org.whu.fleetingtime.service.TravelPostService;
import org.whu.fleetingtime.util.AliyunOssUtil;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TravelPostServiceImpl implements TravelPostService {

    private static final Logger logger = LoggerFactory.getLogger(TravelPostServiceImpl.class); // 定义 Logger 对象

    private final TravelPostImageRepository travelPostImageRepository;
    private final TravelPostRepository travelPostRepository;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Integer EXPIRED_TIME = 5 * 60 * 1000; // 5min

    @Override
    @Transactional // 保证数据库操作的原子性
    public UploadImgResponseDto uploadImage(MultipartFile file, String userId) throws IOException {
        logger.info("【图片上传服务】开始处理用户 {} 的图片上传请求，文件名: {}", userId, file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("【图片上传服务】用户 {} 上传的文件为空", userId);
            throw new BizException("上传的文件不能为空");
        }
        if (userId == null || userId.isEmpty()) {
            logger.warn("【图片上传服务】用户ID不能为空");
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
        travelPostImage.setUserId(userId);
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

    @Override
    @Transactional
    public TravelPostCreateResponseDTO createTravelPost(String userId, TravelPostCreateRequestDTO dto) {
        // 1. 创建并保存TravelPost实体
        TravelPost post = new TravelPost();
        post.setUserId(userId);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setLocationName(dto.getLocationName());
        post.setLatitude(dto.getLatitude());
        post.setLongitude(dto.getLongitude());


        post.setBeginTime(LocalDateTime.parse(dto.getBeginTime(), DATETIME_FORMATTER));
        if (dto.getEndTime() != null && !dto.getEndTime().trim().isEmpty()) {
            post.setEndTime(LocalDateTime.parse(dto.getEndTime(), DATETIME_FORMATTER));
        } else {
            post.setEndTime(post.getBeginTime()); // 结束时间为空则默认为开始时间
        }

        // createdTime 和 updatedTime 会由 @PrePersist 或 @CreationTimestamp/@UpdateTimestamp 自动设置
        // deleted 默认为 false

        TravelPost savedPost = travelPostRepository.save(post);
        List<String> savedImageUrls = new ArrayList<>(); // 用于响应

        // 2. 处理并关联图片
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (ImageAssociationDTO imageInfo : dto.getImages()) {
                TravelPostImage image = travelPostImageRepository.findById(imageInfo.getImageId())
                        .orElseThrow(() -> new BizException(404, "图片未找到，ID: " + imageInfo.getImageId()));

                // 安全校验：确保图片属于当前用户
                if (!image.getUserId().equals(userId)) {
                    throw new BizException(403, "无权操作不属于自己的图片，ID: " + image.getId());
                }
                // 状态校验：确保图片尚未关联到其他帖子
                if (image.getTravelPost() != null && !image.getTravelPost().getId().equals(savedPost.getId())) {
                    // 注意：如果允许一张图片关联到多个帖子，此逻辑需要调整。
                    // 或者，如果图片在编辑帖子时可以从一个帖子“移动”到另一个，也需要不同逻辑。
                    // 对于“创建”帖子，我们通常期望关联的是“未被占用”的图片。
                    throw new BizException(409, "图片 ID: " + image.getId() + " 已被其他帖子关联。");
                }
                image.setTravelPost(savedPost); // 建立关联
                image.setSortOrder(imageInfo.getSortOrder());
                // image.setUpdatedTime(LocalDateTime.now()); // @UpdateTimestamp 会处理
                travelPostImageRepository.save(image); // 保存图片更新（关联和顺序）

                // 假设你有一个方法从objectKey获取完整可访问URL
                String signedUrl = AliyunOssUtil.generatePresignedGetUrl(image.getObjectKey(), EXPIRED_TIME);
                savedImageUrls.add(signedUrl);
            }
        }

        // 3. 构建响应DTO
        return TravelPostCreateResponseDTO.builder()
                .postId(savedPost.getId())
                .userId(savedPost.getUserId())
                .title(savedPost.getTitle())
                .locationName(savedPost.getLocationName())
                .imageUrls(savedImageUrls) // 返回关联图片的URL或标识
                .createdTime(savedPost.getCreatedTime())
                .build();
    }
}
