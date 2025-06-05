package org.whu.fleetingtime.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.PageRequestDTO;
import org.whu.fleetingtime.dto.PageResponseDTO;
import org.whu.fleetingtime.dto.TravelPostDetailsDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostSummaryDTO;
import org.whu.fleetingtime.dto.travelpost.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelPostServiceImpl implements TravelPostService {

    private static final Logger logger = LoggerFactory.getLogger(TravelPostServiceImpl.class); // 定义 Logger 对象

    private final TravelPostImageRepository travelPostImageRepository;
    private final TravelPostRepository travelPostRepository;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Integer EXPIRED_TIME = 5 * 60 * 1000; // 5min

    // 定义允许排序的字段白名单 (对应TravelPost实体的属性名)
    private static final Set<String> ALLOWED_SORT_FIELDS = new HashSet<>(Arrays.asList(
            "title",
            "locationName",
            "beginTime",
            "endTime",
            "createdTime", // 默认排序字段
            "updatedTime"
            // 注意：如果按关联对象的属性排序，会更复杂，通常不直接支持，需要特殊处理或JPQL
    ));

    @Override
    @Transactional // 保证数据库操作的原子性
    public UploadImgResponseDto uploadImage(MultipartFile file, String userId) {
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
            imageUrl = AliyunOssUtil.generatePresignedGetUrl(objectName, EXPIRED_TIME, "image/resize,l_1600/quality,q_50");
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
        travelPostRepository.flush();
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
    public UploadImgResponseDto uploadImageAndAssociate(UploadImgRequestDto requestDto, String userId) {
        MultipartFile file = requestDto.getImage();
        String travelPostId = requestDto.getTravelPostId(); // 获取可选的帖子ID

        // 文件和用户ID的基础校验 (你之前的逻辑)
        if (file.isEmpty()) {
            logger.warn("【图片上传服务】用户 {} 上传的文件为空", userId);
            throw new BizException(HttpStatus.BAD_REQUEST.value(), "上传的文件不能为空");
        }
        if (userId == null || userId.trim().isEmpty()) {
            logger.warn("【图片上传服务】用户ID为空，无法处理图片上传");
            throw new BizException(HttpStatus.UNAUTHORIZED.value(), "用户认证信息无效");
        }
        logger.info("【图片上传服务】开始处理用户 {} 的图片上传，文件名: {}, 可选关联帖子ID: {}",
                userId, file.getOriginalFilename(), travelPostId);

        // 1. 上传文件到OSS (与之前逻辑类似)
        String originalFilename = file.getOriginalFilename();
        String extension = "." + FilenameUtils.getExtension(originalFilename);
        String objectName = "travel-posts/images/" + userId + "/" + UUID.randomUUID() + extension;
        String imageUrl; // 用于存储预签名URL

        logger.debug("【图片上传服务】生成OSS objectKey: {}", objectName);
        logger.info("【图片上传服务】准备上传图片 {} 到OSS...", originalFilename);
        try (InputStream inputStream = file.getInputStream()) {
            AliyunOssUtil.upload(objectName, inputStream); // 假设此方法仅上传
            imageUrl = AliyunOssUtil.generatePresignedGetUrl(objectName, EXPIRED_TIME,"image/resize,l_1600/quality,q_50"); // 生成预签名URL
            if (imageUrl == null) {
                throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "图片上传成功但生成访问链接失败");
            }
            logger.info("【图片上传服务】图片 {} 上传到OSS成功，预签名URL: {}", originalFilename, imageUrl);
        } catch (IOException ioe) {
            logger.error("【图片上传服务】处理图片 {} IO错误: {}", originalFilename, ioe.getMessage(), ioe);
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "文件处理失败，请稍后再试");
        } catch (Exception e) {
            logger.error("【图片上传服务】上传图片 {} 到OSS失败: {}", originalFilename, e.getMessage(), e);
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "图片上传服务暂时不可用");
        }

        // 2. 创建TravelPostImage实体
        TravelPostImage travelPostImage = new TravelPostImage();
        // ID, createdTime, updatedTime, deleted 由实体注解自动处理
        travelPostImage.setUserId(userId); // 图片的拥有者是当前上传用户
        travelPostImage.setObjectKey(objectName);

        // 3. 如果提供了 travelPostId，则尝试关联帖子并设置sortOrder
        if (travelPostId != null && !travelPostId.trim().isEmpty()) {
            logger.info("【图片上传服务】检测到提供了帖子ID: {}，尝试关联图片...", travelPostId);
            TravelPost post = travelPostRepository.findById(travelPostId)
                    .orElseThrow(() -> {
                        logger.warn("【图片上传服务】尝试关联图片到帖子失败：未找到帖子ID {}", travelPostId);
                        AliyunOssUtil.delete(objectName);
                        return new BizException(HttpStatus.NOT_FOUND.value(), "指定的旅行日志未找到，ID: " + travelPostId);
                    });

            // 权限校验：确保当前用户是帖子的所有者才能向其添加图片
            if (!post.getUserId().equals(userId)) {
                logger.warn("【图片上传服务】用户 {} 尝试向不属于自己的帖子 {} 添加图片 (所有者: {})",
                        userId, travelPostId, post.getUserId());
                AliyunOssUtil.delete(objectName);
                throw new BizException(HttpStatus.UNAUTHORIZED.value(), "无权向此旅行日志添加图片");
            }

            travelPostImage.setTravelPost(post); // 建立关联

            // 设置 sortOrder：追加到现有图片的末尾
            // 需要查询当前帖子的最大sortOrder，或者图片数量
            // 这种方式在高并发下可能有问题（获取最大sortOrder和保存之间可能有其他图片插入）
            // 更安全的方式可能是数据库序列、或者允许短暂的sortOrder冲突后续调整，或者客户端明确指定顺序。
            // 简单起见，我们先用基于当前图片数量的方式：
            long currentImageCount = travelPostImageRepository.countByTravelPost(post); // 需要在Repository中添加此方法
            travelPostImage.setSortOrder((int) currentImageCount); // 新图片排在最后
            logger.info("【图片上传服务】图片将关联到帖子 {}，并设置排序为 {}", travelPostId, travelPostImage.getSortOrder());
        } else {
            logger.info("【图片上传服务】未提供帖子ID，图片将作为用户 {} 的未分类图片保存", userId);
            travelPostImage.setSortOrder(0);
        }

        // 4. 保存TravelPostImage实体到数据库
        logger.info("【图片上传服务】准备将图片信息 (userId: {}, objectKey: {}, postId: {}) 保存到数据库...",
                userId, objectName, travelPostImage.getTravelPost() != null ? travelPostImage.getTravelPost().getId() : "N/A");
        TravelPostImage savedImage;
        try {
            savedImage = travelPostImageRepository.saveAndFlush(travelPostImage); // saveAndFlush确保立即获取ID和时间戳
        } catch (DataAccessException dae) {
            logger.error("【图片上传服务】保存图片信息 (objectKey: {}) 到数据库失败: {}", objectName, dae.getMessage(), dae);
            // 应该尝试删除已上传到OSS的文件，作为补偿
            try {
                AliyunOssUtil.delete(objectName);
            } catch (Exception ossEx) {
                logger.error("【图片上传服务】补偿删除OSS文件 {} 失败", objectName, ossEx);
            }
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "图片信息保存失败");
        }
        logger.info("【图片上传服务】图片信息成功保存到数据库，图片ID: {}, objectKey: {}", savedImage.getId(), savedImage.getObjectKey());

        // 5. 构建并返回响应 DTO
        return UploadImgResponseDto.builder()
                .imageId(savedImage.getId())
                .objectKey(savedImage.getObjectKey())
                .url(imageUrl) // 预签名URL
                .createdTime(savedImage.getCreatedTime())
                .build();
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
            travelPostRepository.flush();
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
                    String presignedUrl = AliyunOssUtil.generatePresignedGetUrl(image.getObjectKey(), EXPIRED_TIME,"image/resize,l_1600/quality,q_50");
                    if (presignedUrl != null) {
                        associatedImageUrls.add(presignedUrl);
                    } else {
                        logger.warn("【创建旅行日志服务】为图片 objectKey: {} 生成预签名URL失败，将不添加到返回列表", image.getObjectKey());
                        // 也可以选择添加一个占位符或者原始OSS Key作为URL
                        associatedImageUrls.add("URL生成失败:" + image.getObjectKey());
                    }
                } catch (DataAccessException dae) {
                    logger.error("【创建旅行日志服务】用户 {} 更新图片 {} 的关联信息到数据库失败: {}", userId, imageIdToAssociate, dae.getMessage(), dae);
                    throw new BizException("出错了，请稍后再试");
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

    @Override
    @Transactional(readOnly = true) // 查询操作，设置为只读事务，可能优化性能
    public PageResponseDTO<TravelPostSummaryDTO> getMyTravelPosts(String userId, PageRequestDTO pageRequestDTO) {
        logger.info("【查询我的旅行日志服务】用户 {} 请求分页数据，请求参数: {}", userId, pageRequestDTO);
        // 0.a 处理userId为null的情况
        if (userId == null || userId.isEmpty()) {
            throw new BizException(401, "用户未认证或认证信息无效");
        }

        // 0.b 校验并处理排序字段
        String sortBy = pageRequestDTO.getSortBy();
        if (sortBy == null || sortBy.trim().isEmpty() || !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            logger.warn("【查询我的旅行日志服务】用户 {} 请求的排序字段 '{}' 无效。",
                    userId, sortBy);
            throw new BizException(400, "无效的排序字段: " + pageRequestDTO.getSortBy());
        }


        // 1. 构建Pageable对象
        Sort.Direction direction = "ASC".equalsIgnoreCase(pageRequestDTO.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        // 注意：PageRequest的页码是从0开始的，而我们DTO设计的是从1开始
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1, // 页码转换为0-based
                pageRequestDTO.getSize(),
                Sort.by(direction, pageRequestDTO.getSortBy())
        );

        // 2. 调用Repository进行分页查询
        Page<TravelPost> postPage = travelPostRepository.findAllByUserId(userId, pageable);
        logger.debug("【查询我的旅行日志服务】用户 {} 查询到 {} 条记录，总共 {} 页", userId, postPage.getTotalElements(), postPage.getTotalPages());

        // 3. 将查询结果 (Page<TravelPost>) 转换为 List<TravelPostSummaryDTO>
        List<TravelPostSummaryDTO> summaryDTOs = postPage.getContent().stream()
                .map(this::convertToSummaryDTO) // 使用一个辅助方法进行转换
                .collect(Collectors.toList());

        // 4. 构建并返回 PageResponseDTO
        return PageResponseDTO.<TravelPostSummaryDTO>builder()
                .content(summaryDTOs)
                .pageNumber(postPage.getNumber() + 1) // Page对象的页码是0-based，转回1-based
                .pageSize(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .first(postPage.isFirst())
                .last(postPage.isLast())
                .numberOfElements(postPage.getNumberOfElements())
                .build();
    }

    @Override
    public void deleteTravelPost(String userId, String postId) {
        logger.info("【删除旅行日志服务】用户 {} 请求删除帖子ID: {}", userId, postId);
        // 0. 校验userId
        if (userId == null || userId.isEmpty()) {
            throw new BizException(401, "用户未认证或认证信息无效");
        }

        // 1. 查找帖子
        // 更稳妥的方式是，如果需要操作关联对象，显式加载它们：
        TravelPost post = travelPostRepository.findById(postId)
                .map(p -> {
                    // 强制初始化懒加载的图片集合 (如果images是LAZY)
                    // 如果images是EAGER，这步不是必须的，但无害
                    // 如果实体本身配置了 @SoftDelete，被软删除的post不会被findById查到，除非修改查询
                    // 但我们的目标是：用户能查到（因为deleted=false），然后才能删除
                    if (p.getImages() != null) {
                        p.getImages().size(); // 访问集合以触发加载
                    }
                    return p;
                })
                .orElseThrow(() -> {
                    logger.warn("【删除旅行日志服务】尝试删除不存在的帖子，ID: {}", postId);
                    return new BizException(404, "要删除的旅行日志未找到");
                });

        // 2. 权限校验：确保是帖子所有者才能删除
        if (!post.getUserId().equals(userId)) {
            logger.warn("【删除旅行日志服务】用户 {} 尝试删除不属于自己的帖子 {} (所有者: {})", userId, postId, post.getUserId());
            throw new BizException(401, "无权删除此旅行日志");
        }

        // 3. (可选，但推荐) 删除OSS上的关联图片文件
        // 注意：这一步应该在数据库记录被逻辑删除之前或之后谨慎处理。
        // 如果OSS删除失败，是否回滚数据库的逻辑删除？这是一个需要权衡的问题。
        // 通常建议：即使OSS删除失败，也继续逻辑删除数据库记录，并将OSS删除失败的项记录下来进行后续补偿处理。
        List<TravelPostImage> imagesToDelete = new ArrayList<>(post.getImages()); // 创建副本以防并发修改问题
        if (!imagesToDelete.isEmpty()) {
            logger.info("【删除旅行日志服务】帖子 {} 包含 {} 张图片，准备从OSS删除...", postId, imagesToDelete.size());
            for (TravelPostImage image : imagesToDelete) {
                if (image.getObjectKey() != null && !image.getObjectKey().isEmpty()) {
                    try {
                        AliyunOssUtil.delete(image.getObjectKey()); // 调用你的OSS删除工具方法
                        logger.info("【删除旅行日志服务】成功从OSS删除图片文件: {}", image.getObjectKey());
                    } catch (Exception e) {
                        // 记录OSS删除失败的日志，但不中断整个帖子的删除流程（业务决策）
                        logger.error("【删除旅行日志服务】从OSS删除图片文件 {} 失败: {}", image.getObjectKey(), e.getMessage(), e);
                        // 你可以在这里添加逻辑，比如把删除失败的objectKey记录到一张待处理表或日志文件中
                    }
                }
            }
        }

        // 4. 逻辑删除帖子 (以及通过级联逻辑删除其关联的TravelPostImage数据库记录)
        // 由于TravelPost和TravelPostImage都配置了@SoftDelete，调用delete方法会触发逻辑删除
        // 同时，TravelPost中对images的@OneToMany配置了cascade=CascadeType.ALL，
        // 这意味着对TravelPost的REMOVE操作会级联到images集合中的每个TravelPostImage，
        // 从而触发它们的@SoftDelete逻辑。
        try {
            travelPostRepository.delete(post); // 这会触发TravelPost的@SoftDelete，并级联到TravelPostImage的@SoftDelete
            logger.info("【删除旅行日志服务】帖子ID {} 及其关联图片已成功逻辑删除 (数据库层面)", postId);
        } catch (DataAccessException dae) {
            logger.error("【删除旅行日志服务】逻辑删除帖子 {} (数据库层面) 失败: {}", postId, dae.getMessage(), dae);
            // 如果OSS文件已删除，但DB逻辑删除失败，这里需要更复杂的补偿或重试机制
            throw new BizException(500, "删除旅行日志时发生错误");
        }
    }

    @Override
    public TravelPostImagesUpdateResponseDTO updateTravelPostImages(String userId, String postId, TravelPostImagesUpdateRequestDTO dto) {
        logger.info("【更新日志图片服务】用户 {} 开始更新帖子 {} 的图片列表", userId, postId);

        if (userId == null || userId.isEmpty()) {
            throw new BizException(401, "用户未认证或认证信息无效");
        }

        TravelPost post = travelPostRepository.findById(postId)
                .orElseThrow(() -> new BizException(404, "要更新图片的旅行日志未找到，ID: " + postId));

        if (!post.getUserId().equals(userId)) {
            logger.warn("【更新日志图片服务】用户 {} 尝试更新不属于自己的帖子 {} 的图片 (所有者: {})", userId, postId, post.getUserId());
            throw new BizException(401, "无权修改此旅行日志的图片");
        }

        // --- 核心逻辑：用新的图片列表替换旧的 ---
        // 1. 获取当前帖子已关联的图片 (确保在事务内，或者images是EAGER加载的)
        // 创建一个映射，方便查找现有图片是否在新的列表中
        List<ImageAssociationDTO> newImageInfos = dto.getImages() == null ? new ArrayList<>() : dto.getImages();
        Map<String, ImageAssociationDTO> newImageInfoMap = newImageInfos.stream()
                .collect(Collectors.toMap(ImageAssociationDTO::getImageId, info -> info, (info1, info2) -> info1)); // 处理可能的重复ID（理论上不应有）

        List<TravelPostImage> imagesToRemove = new ArrayList<>();
        List<TravelPostImage> imagesToKeepAndUpdateSortOrder = new ArrayList<>();

        // 遍历当前帖子已有的图片
        for (TravelPostImage existingImage : new ArrayList<>(post.getImages())) { // 使用副本遍历，方便修改原集合
            if (newImageInfoMap.containsKey(existingImage.getId())) {
                // 这张图片在新的列表中，需要保留，并更新其顺序
                ImageAssociationDTO newInfo = newImageInfoMap.get(existingImage.getId());
                existingImage.setSortOrder(newInfo.getSortOrder());
                // updatedTime会由@UpdateTimestamp自动更新 (如果TravelPostImage上有此注解)
                imagesToKeepAndUpdateSortOrder.add(existingImage);
            } else {
                // 这张图片不在新的列表中，需要解除与当前帖子的关联
                // 如果orphanRemoval=true，从post.getImages()中移除它就会导致它被删除(逻辑删除)
                // 或者，我们可以更明确地将其travelPost设为null，如果它还可以独立存在的话
                imagesToRemove.add(existingImage);
            }
        }

        // 从帖子中移除不再需要的图片 (触发orphanRemoval或后续手动处理)
        for (TravelPostImage imageToRemove : imagesToRemove) {
            post.removeImage(imageToRemove); // 使用你在TravelPost中定义的辅助方法，它会设置image.setTravelPost(null)
            // 并且由于orphanRemoval=true，如果这张图片不再被任何Post引用，它会被逻辑删除
            logger.debug("【更新日志图片服务】图片 {} 已从帖子 {} 解除关联，等待处理", imageToRemove.getId(), postId);
            // 注意：如果这张图片是“孤儿”了（即它的travelPost变为null，且userId是它唯一的归属），
            // 你可能需要一个额外的逻辑来判断是否真的要（逻辑）删除这个TravelPostImage实体，
            // 或者允许它作为用户图库中的“未分类”图片存在。
            // 如果orphanRemoval=true，且TravelPostImage的travelPost是其生命周期的关键，它会被自动删除。
            // 对于我们的@SoftDelete实体，它会被自动逻辑删除。
        }

        // 添加新的图片关联
        List<TravelPostImage> finalImagesForPost = new ArrayList<>(imagesToKeepAndUpdateSortOrder);

        for (ImageAssociationDTO newImageInfo : newImageInfos) {
            boolean alreadyProcessed = imagesToKeepAndUpdateSortOrder.stream()
                    .anyMatch(img -> img.getId().equals(newImageInfo.getImageId()));
            if (!alreadyProcessed) {
                // 这是新加入的图片
                TravelPostImage imageToAdd = travelPostImageRepository.findById(newImageInfo.getImageId())
                        .orElseThrow(() -> new BizException(404, "要关联的新图片未找到，ID: " + newImageInfo.getImageId()));

                // 安全与状态校验
                if (!imageToAdd.getUserId().equals(userId)) {
                    throw new BizException(401, "无权操作不属于自己的图片，ID: " + newImageInfo.getImageId());
                }
                if (imageToAdd.getTravelPost() != null && !imageToAdd.getTravelPost().getId().equals(postId)) {
                    throw new BizException(400, "图片 ID: " + newImageInfo.getImageId() + " 已被其他帖子关联。");
                }

                imageToAdd.setSortOrder(newImageInfo.getSortOrder());
                // imageToAdd.setTravelPost(post); // addImage 辅助方法会做这个
                post.addImage(imageToAdd); // 使用辅助方法添加，它会处理双向关联和userId（如果需要）
                finalImagesForPost.add(imageToAdd); // 也加入到最终列表
            }
        }

        // 对finalImagesForPost可能还需要根据sortOrder再排序一次，以防万一
        // 但理论上，如果前端传入的sortOrder是正确的最终顺序，直接按此顺序处理即可。
        // JPA的 @OrderBy("sortOrder ASC") 也会在下次加载时保证顺序。

        // 更新Post的updatedTime（通常由@UpdateTimestamp自动处理，或者手动设置）
        // post.setUpdatedTime(LocalDateTime.now()); // 如果没有@UpdateTimestamp
        travelPostRepository.save(post); // 保存Post，级联操作会处理images集合的变化
        travelPostRepository.flush();

        logger.info("【更新日志图片服务】帖子 {} 的图片列表更新成功", postId);

        // 重新获取帖子的图片列表，确保返回的是最新状态和正确顺序的URL
        List<String> finalImageUrls = travelPostRepository.findById(postId).get().getImages()
                .stream()
                .sorted(Comparator.comparing(TravelPostImage::getSortOrder)) // 如果 @OrderBy没生效或想再次确认
                .map(img -> AliyunOssUtil.generatePresignedGetUrl(img.getObjectKey(), EXPIRED_TIME,"image/resize,l_1600/quality,q_50"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return TravelPostImagesUpdateResponseDTO.builder()
                .postId(postId)
                .finalImageUrls(finalImageUrls)
                .postUpdatedTime(post.getUpdatedTime()) // 获取更新后的时间
                .build();
    }

    @Override
    public TravelPostDetailsDTO getMyTravelPostDetails(String userId, String postId) {
        logger.info("【查询帖子详情服务】用户 {} 请求查询帖子ID: {}", userId, postId);

        if (userId == null || userId.isEmpty()) {
            throw new BizException(401, "用户未认证或认证信息无效");
        }

        TravelPost post = travelPostRepository.findById(postId)
                .map(p -> {
                    if (p.getImages() != null) {
                        p.getImages().size(); // 访问集合以触发加载
                    }
                    return p;
                })
                .orElseThrow(() -> {
                    logger.warn("【查询帖子详情服务】尝试查询不存在的帖子，ID: {}", postId);
                    return new BizException(404, "旅行日志未找到，ID: " + postId);
                });

        // 2. 权限校验：确保是帖子所有者
        if (!post.getUserId().equals(userId)) {
            logger.warn("【查询帖子详情服务】用户 {} 尝试查询不属于自己的帖子 {} (所有者: {})", userId, postId, post.getUserId());
            throw new BizException(401, "无权查看此旅行日志");
        }

        logger.info("【查询帖子详情服务】用户 {} 的帖子 {} 查询成功", userId, postId);
        // 3. 转换为响应DTO (这个方法内部会处理图片预签名URL)
        return convertToDetailedResponseDTO(post);
    }


    @Override
    public TravelPostUpdateResponseDTO updateTravelPostText(String userId, String postId, TravelPostTextUpdateRequestDTO dto) {
        logger.info("【更新日志文本服务】用户 {} 开始更新帖子 {} 的文本内容", userId, postId);

        TravelPost post = travelPostRepository.findById(postId)
                .orElseThrow(() -> new BizException(404, "要更新的旅行日志未找到，ID: " + postId));

        if (!post.getUserId().equals(userId)) {
            logger.warn("【更新日志文本服务】用户 {} 尝试更新不属于自己的帖子 {} (所有者: {})", userId, postId, post.getUserId());
            throw new BizException(401, "无权修改此旅行日志");
        }

        // 更新字段
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setLocationName(dto.getLocationName());
        post.setLatitude(dto.getLatitude());
        post.setLongitude(dto.getLongitude());

        try {
            post.setBeginTime(LocalDateTime.parse(dto.getBeginTime(), DATETIME_FORMATTER));
            if (dto.getEndTime() != null && !dto.getEndTime().trim().isEmpty()) {
                post.setEndTime(LocalDateTime.parse(dto.getEndTime(), DATETIME_FORMATTER));
            } else {
                post.setEndTime(post.getBeginTime());
            }
        } catch (DateTimeParseException e) {
            throw new BizException(400, "时间格式错误，应为 'yyyy-MM-dd HH:mm:ss'");
        }
        // updatedTime 会由 @UpdateTimestamp 或 @PreUpdate 自动处理

        TravelPost updatedPost = travelPostRepository.save(post);
        logger.info("【更新日志文本服务】帖子 {} 文本内容更新成功", postId);

        // 构建并返回响应
        return convertToResponseDTO(updatedPost); // 转换方法
    }

    // 辅助方法：将TravelPost实体转换为TravelPostSummaryDTO
    private TravelPostSummaryDTO convertToSummaryDTO(TravelPost post) {
        String firstImageUrl = null;
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            // 获取第一张图片 (假设images已按sortOrder排序)
            TravelPostImage firstImage = post.getImages().get(0);
            firstImageUrl = AliyunOssUtil.generatePresignedGetUrl(firstImage.getObjectKey(), EXPIRED_TIME,"image/resize,l_1600/quality,q_50");
        }

        return TravelPostSummaryDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .longitude(post.getLongitude())
                .latitude(post.getLatitude())
                .content(post.getContent().substring(0, Math.min(post.getContent().length(), 1000)))
                .locationName(post.getLocationName())
                .beginTime(post.getBeginTime())
                .endTime(post.getEndTime())
                .createdTime(post.getCreatedTime())
                .firstImageUrl(firstImageUrl)
                .build();
    }

    // 辅助方法：将TravelPost实体转换为TravelPostResponseDTO
    private TravelPostUpdateResponseDTO convertToResponseDTO(TravelPost post) {
        if (post == null) return null;
        List<String> imageUrls = new ArrayList<>();
        if (post.getImages() != null) {
            // 确保在事务内访问，或者images是EAGER加载
            // post.getImages().size(); // 触发懒加载（如果需要）
            imageUrls = post.getImages().stream()
                    .map(img -> AliyunOssUtil.generatePresignedGetUrl(img.getObjectKey(), EXPIRED_TIME,"image/resize,l_1600/quality,q_50")) // 生成预签名URL
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return TravelPostUpdateResponseDTO.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent()) // TODO: 注意：如果content非常大，是否在列表和简单详情中都需要返回？
                .locationName(post.getLocationName())
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .beginTime(post.getBeginTime())
                .endTime(post.getEndTime())
                .createdTime(post.getCreatedTime())
                .updatedTime(post.getUpdatedTime())
                .imageUrls(imageUrls)
                .build();
    }

    // 辅助方法：将TravelPost实体转换为TravelPostResponseDTO
    // 这个方法之前在updateText接口部分已经定义过，确保它能正确生成预签名URL
    private TravelPostDetailsDTO convertToDetailedResponseDTO(TravelPost post) {
        if (post == null) {
            return null;
        }
        List<String> imageUrls = new ArrayList<>();
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            // 确保在事务内或images已加载
            imageUrls = post.getImages().stream()
                    // 假设 TravelPostImage 有 getObjectKey() 方法
                    // 并且 AliyunOssUtil.generateUrl 返回的是预签名URL字符串
                    .map(image -> AliyunOssUtil.generatePresignedGetUrl(image.getObjectKey(), EXPIRED_TIME,"image/resize,l_1600/quality,q_50"))
                    .filter(url -> url != null && !url.isEmpty()) // 过滤掉生成失败或为空的URL
                    .collect(Collectors.toList());
        }

        return TravelPostDetailsDTO.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .locationName(post.getLocationName())
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .beginTime(post.getBeginTime())
                .endTime(post.getEndTime())
                .createdTime(post.getCreatedTime())
                .updatedTime(post.getUpdatedTime())
                .imageUrls(imageUrls) // 这里是预签名URL列表
                .build();
    }
}
