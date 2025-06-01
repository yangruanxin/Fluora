package org.whu.fleetingtime.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
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
import java.time.format.DateTimeParseException;
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
        // 0. 校验userId (如果拦截器没有处理，或者作为双重保险)
        if (userId == null || userId.trim().isEmpty()) {
            logger.warn("【创建旅行日志服务】尝试创建帖子但用户ID为空或无效");
            throw new BizException(401, "用户未认证或认证信息无效");
        }
        logger.info("【创建旅行日志服务】用户 {} 开始创建新的旅行日志，请求数据: {}", userId, dto);

        // 1. 创建并准备TravelPost实体
        TravelPost post = new TravelPost();
        post.setUserId(userId);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setLocationName(dto.getLocationName());
        post.setLatitude(dto.getLatitude());
        post.setLongitude(dto.getLongitude());
        logger.debug("【创建旅行日志服务】用户 {} 准备解析帖子时间...", userId);
        try {
            post.setBeginTime(LocalDateTime.parse(dto.getBeginTime(), DATETIME_FORMATTER));
            if (dto.getEndTime() != null && !dto.getEndTime().trim().isEmpty()) {
                post.setEndTime(LocalDateTime.parse(dto.getEndTime(), DATETIME_FORMATTER));
            } else {
                post.setEndTime(post.getBeginTime());
                logger.debug("【创建旅行日志服务】用户 {} 未提供结束时间，默认为开始时间: {}", userId, post.getBeginTime());
            }
        } catch (DateTimeParseException e) {
            logger.warn("【创建旅行日志服务】用户 {} 提供的时间格式错误: beginTime={}, endTime={}. 错误: {}",
                    userId, dto.getBeginTime(), dto.getEndTime(), e.getMessage());
            throw new BizException(400, "时间格式错误，应为 'yyyy-MM-dd HH:mm:ss'");
        }
        logger.info("【创建旅行日志服务】用户 {} 准备保存旅行日志主体...", userId);
        TravelPost savedPost;
        try {
            savedPost = travelPostRepository.save(post);
        } catch (DataAccessException dae) {
            logger.error("【创建旅行日志服务】用户 {} 保存旅行日志主体到数据库失败: {}", userId, dae.getMessage(), dae);
            throw new BizException("创建旅行日志失败，请稍后再试");
        }
        logger.info("【创建旅行日志服务】用户 {} 的旅行日志主体保存成功，帖子ID: {}", userId, savedPost.getId());
        List<String> associatedImageUrls = new ArrayList<>();
        // 2. 处理并关联图片
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            logger.info("【创建旅行日志服务】用户 {} 的帖子 {} 准备关联 {} 张图片", userId, savedPost.getId(), dto.getImages().size());
            for (ImageAssociationDTO imageInfo : dto.getImages()) {
                String imageIdToAssociate = imageInfo.getImageId();
                Integer sortOrder = imageInfo.getSortOrder();
                logger.debug("【创建旅行日志服务】准备关联图片ID: {}, 顺序: {}", imageIdToAssociate, sortOrder);
                TravelPostImage image = travelPostImageRepository.findById(imageIdToAssociate)
                        .orElseThrow(() -> {
                            logger.warn("【创建旅行日志服务】用户 {} 尝试关联的图片未找到，ID: {}", userId, imageIdToAssociate);
                            return new BizException(404, "图片未找到，请稍后再试");
                        });
                // 安全校验：确保图片属于当前用户
                if (!image.getUserId().equals(userId)) {
                    logger.warn("【创建旅行日志服务】用户 {} 尝试关联不属于自己的图片，图片ID: {}, 图片所属用户: {}",
                            userId, imageIdToAssociate, image.getUserId());
                    throw new BizException(401, "无权操作不属于自己的图片");
                }
                // 状态校验：确保图片尚未关联到其他帖子 (如果业务逻辑是图片只能属于一个帖子)
                if (image.getTravelPost() != null && !image.getTravelPost().getId().equals(savedPost.getId())) {
                    logger.warn("【创建旅行日志服务】用户 {} 尝试关联的图片ID: {} 已被帖子 {} 关联。",
                            userId, imageIdToAssociate, image.getTravelPost().getId());
                    throw new BizException(401, "图片已失效，请重新上传");
                }

                image.setTravelPost(savedPost); // 建立关联
                image.setSortOrder(sortOrder);
                // updatedTime 会由 @UpdateTimestamp 或 @PreUpdate 自动处理

                try {
                    travelPostImageRepository.save(image); // 保存图片更新（关联和顺序）
                    logger.debug("【创建旅行日志服务】图片ID: {} 成功关联到帖子ID: {}，顺序: {}", imageIdToAssociate, savedPost.getId(), sortOrder);
                    String presignedUrl = AliyunOssUtil.generatePresignedGetUrl(image.getObjectKey(), EXPIRED_TIME);
                    if (presignedUrl != null) {
                        associatedImageUrls.add(presignedUrl);
                    } else {
                        logger.warn("【创建旅行日志服务】为图片 objectKey: {} 生成预签名URL失败，将不添加到返回列表", image.getObjectKey());
                        // 也可以选择添加一个占位符或者原始OSS Key作为URL
                        associatedImageUrls.add("URL生成失败:" + image.getObjectKey());
                    }
                } catch (DataAccessException dae) {
                    logger.error("【创建旅行日志服务】用户 {} 更新图片 {} 的关联信息到数据库失败: {}", userId, imageIdToAssociate, dae.getMessage(), dae);
                    throw new BizException(50003, "出错了，请稍后再试");
                }
            }
            logger.info("【创建旅行日志服务】用户 {} 的帖子 {} 图片关联处理完成。", userId, savedPost.getId());
        } else {
            logger.info("【创建旅行日志服务】用户 {} 的帖子 {} 没有需要关联的图片。", userId, savedPost.getId());
        }

        // 3. 构建响应DTO
        TravelPostCreateResponseDTO responseDto = TravelPostCreateResponseDTO.builder()
                .postId(savedPost.getId())
                .userId(savedPost.getUserId())
                .title(savedPost.getTitle())
                .locationName(savedPost.getLocationName())
                .imageUrls(associatedImageUrls)
                .createdTime(savedPost.getCreatedTime())
                .build();
        logger.info("【创建旅行日志服务】用户 {} 的旅行日志创建流程完成，返回响应: {}", userId, responseDto);
        return responseDto;
    }
}
