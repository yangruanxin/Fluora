package org.whu.fleetingtime.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.travelpost.*;
import org.whu.fleetingtime.exception.BizException;
import org.whu.fleetingtime.exception.BizExceptionEnum;
import org.whu.fleetingtime.mapper.TravelPostImageMapper;
import org.whu.fleetingtime.mapper.TravelPostMapper;
import org.whu.fleetingtime.pojo.TravelPost;
import org.whu.fleetingtime.pojo.TravelPostImage;
import org.whu.fleetingtime.service.TravelPostService;
import org.whu.fleetingtime.util.AliyunOssUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TravelPostServiceImpl implements TravelPostService {

    private static final Logger logger = LoggerFactory.getLogger(TravelPostServiceImpl.class);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    TravelPostMapper travelPostMapper;
    @Autowired
    TravelPostImageMapper travelPostImageMapper;

    @Override
    public TravelPostCreateResponseDTO createTravelPost(Long userId, TravelPostCreateRequestDTO request) {
        logger.info("[createTravelPost]收到新建旅行记录请求");
        // 输入校验
        if (request.getContent() == null ||
                request.getContent().trim().isEmpty() ||
                request.getLocationName() == null ||
                request.getLocationName().trim().isEmpty() ||
                request.getLatitude() == null ||
                request.getLongitude() == null ||
                request.getBeginTime() == null ||
                request.getBeginTime().trim().isEmpty()
        ) {
            logger.warn("[createTravelPost]请求参数无效");
            throw new BizException(BizExceptionEnum.INVALID_POST_PARAMETER);
        }

        // 获取当前时间作为 createdTime 和 updatedTime
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 处理endTime
        if (request.getEndTime() == null || request.getEndTime().trim().isEmpty()) {
            request.setEndTime(request.getBeginTime());
        }

        // 构造旅行记录对象
        TravelPost post = new TravelPost();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setLocationName(request.getLocationName());
        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());
        post.setBeginTime(LocalDateTime.parse(request.getBeginTime(), fmt));
        post.setEndTime(LocalDateTime.parse(request.getEndTime(), fmt));
        post.setCreatedTime(now);
        post.setUpdatedTime(now);

        // 存入数据库
        travelPostMapper.insert(post); // post.getId() 可用
        logger.info("[createTravelPost]记录数据插入成功");

        // 4. 上传每张图片到 OSS
        List<MultipartFile> images = request.getImages();
        List<Integer> orders = request.getOrders();

        if (images != null && !images.isEmpty()) {
            if (images.size() != orders.size()) {
                throw new BizException(BizExceptionEnum.INVALID_ORDER_ARRAY);
            }
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);
                if (image == null || image.isEmpty()) {
                    continue;
                }
                String suffix = StringUtils.getFilenameExtension(image.getOriginalFilename());
                String objectName = "travelPostImage/" + userId + "/" + UUID.randomUUID() + "." + suffix;
                InputStream inputStream;
                try {
                    inputStream = image.getInputStream();
                } catch (IOException e) {
                    throw new BizException(BizExceptionEnum.IMAGE_UPLOAD_FAILED);
                }
                String imageUrl = AliyunOssUtils.upload(objectName, inputStream);
                logger.info("[createTravelPost]图片上传成功");
                TravelPostImage postImage = new TravelPostImage();
                postImage.setPostId(post.getId());
                postImage.setImageUrl(imageUrl);
                postImage.setSortOrder(i < orders.size() ? orders.get(i) : i);
                postImage.setCreatedTime(now);
                travelPostImageMapper.insert(postImage);
                logger.info("[createTravelPost]图片数据插入成功");
            }
        }

        List<String> imageUrls = travelPostImageMapper.selectByPostId(post.getId())
                .stream().
                map(TravelPostImage::getImageUrl)
                .toList();

        // 7. 构建响应
        return TravelPostCreateResponseDTO.builder()
                .postId(post.getId())
                .userId(userId)
                .locationName(post.getLocationName())
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .imageUrls(imageUrls)
                .beginTime(post.getBeginTime())
                .endTime(post.getEndTime())
                .build();
    }

    @Override
    public List<TravelPostGetResponseDTO> getTravelPostsByUserId(Long userId) {
        List<TravelPost> posts = travelPostMapper.selectByUserId(userId);
        List<TravelPostGetResponseDTO> result = new ArrayList<>();

        for (TravelPost post : posts) {
            List<TravelPostImage> images = travelPostImageMapper.selectByPostId(post.getId());
            List<String> imageUrls = images.stream()
                    .map(TravelPostImage::getImageUrl)
                    .toList();

            TravelPostGetResponseDTO dto = TravelPostGetResponseDTO.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .locationName(post.getLocationName())
                    .latitude(post.getLatitude())
                    .longitude(post.getLongitude())
                    .beginTime(post.getBeginTime())
                    .endTime(post.getEndTime())
                    .createdTime(post.getCreatedTime())
                    .updatedTime(post.getUpdatedTime())
                    .imageUrls(imageUrls)
                    .build();
            result.add(dto);
        }
        return result;
    }

    // 删除travelPost
    @Override
    @Transactional // 确保删除操作的原子性
    public void deleteTravelPost(Long userId, Long postId) {
        logger.info("[deleteTravelPost] 请求删除旅行记录，userId: {}, postId: {}", userId, postId);
        // 1. 查询 travelPost
        TravelPost post = travelPostMapper.selectByPostId(postId);
        if (post == null) {
            logger.warn("[deleteTravelPost] 记录不存在，postId: {}", postId);
            throw new BizException(BizExceptionEnum.POST_NOT_FOUND);
        }
        // 2. 校验 userId 是否一致
        if (!post.getUserId().equals(userId)) {
            logger.warn("[deleteTravelPost] 非法操作：用户 {} 尝试删除不属于自己的记录 {}", userId, postId);
            throw new BizException(BizExceptionEnum.UNAUTHORIZED_OPERATION);
        }
        // 3. 查询图片记录
        List<TravelPostImage> images = travelPostImageMapper.selectByPostId(postId);
        for (TravelPostImage image : images) {
            String imageUrl = image.getImageUrl();
            String objectName = AliyunOssUtils.extractObjectNameFromUrl(imageUrl);  // 提取 OSS 中的对象路径
            if (objectName != null && !objectName.isEmpty()) {
                try {
                    AliyunOssUtils.delete(objectName);
                    logger.info("[deleteTravelPost] 成功删除 OSS 文件: {}", objectName);
                } catch (Exception e) {
                    logger.warn("[deleteTravelPost] 删除 OSS 文件失败: {}, 原因: {}", objectName, e.getMessage());
                }
            }
        }
        // 3. 删除图片记录
        travelPostImageMapper.deleteByPostId(postId);
        logger.info("[deleteTravelPost] 已删除图片记录，postId: {}", postId);
        // 4. 删除主记录
        travelPostMapper.deleteByPostId(postId);
        logger.info("[deleteTravelPost] 已删除主记录，postId: {}", postId);
    }

    @Override
    @Transactional // 确保更新操作的原子性
    public TravelPostUpdateResponseDTO updateTravelPostText(Long userId, Long postId, TravelPostTextUpdateRequestDTO requestDTO) {
        logger.info("[updateTravelPostText] 用户 {} 请求更新旅行记录 {} 的文字内容, 请求数据: {}", userId, postId, requestDTO);

        // 1. 参数校验 (基础校验)
        if (requestDTO.getContent() == null || requestDTO.getContent().trim().isEmpty() ||
                requestDTO.getLocationName() == null || requestDTO.getLocationName().trim().isEmpty() ||
                requestDTO.getLatitude() == null || requestDTO.getLongitude() == null ||
                requestDTO.getBeginTime() == null || requestDTO.getBeginTime().trim().isEmpty()) {
            logger.warn("[updateTravelPostText] 请求参数无效 for postId: {}", postId);
            throw new BizException(BizExceptionEnum.INVALID_POST_PARAMETER);
        }

        // 2. 查找现有的 TravelPost
        TravelPost postToUpdate = travelPostMapper.selectByPostId(postId);
        if (postToUpdate == null) {
            logger.warn("[updateTravelPostText] 旅行记录不存在, postId: {}", postId);
            throw new BizException(BizExceptionEnum.POST_NOT_FOUND);
        }

        // 3. 校验用户权限 (确保是记录的拥有者)
        if (!postToUpdate.getUserId().equals(userId)) {
            logger.warn("[updateTravelPostText] 用户 {} 无权修改 postId: {}. 该记录属于用户 {}", userId, postId, postToUpdate.getUserId());
            throw new BizException(BizExceptionEnum.UNAUTHORIZED_OPERATION);
        }

        // 4. 更新 TravelPost 对象的属性
        // 如果 title 为 null 或空字符串，则不更新 title (允许用户只修改其他字段而不修改标题)
        // 如果业务要求 title 也可以被清空，则移除这个判断 requestDTO.getTitle() != null
        if (requestDTO.getTitle() != null) {
            postToUpdate.setTitle(requestDTO.getTitle().trim().isEmpty() ? null : requestDTO.getTitle().trim());
        }
        postToUpdate.setContent(requestDTO.getContent().trim());
        postToUpdate.setLocationName(requestDTO.getLocationName().trim());
        postToUpdate.setLatitude(requestDTO.getLatitude());
        postToUpdate.setLongitude(requestDTO.getLongitude());

        try {
            postToUpdate.setBeginTime(LocalDateTime.parse(requestDTO.getBeginTime(), DATETIME_FORMATTER));
            if (requestDTO.getEndTime() != null && !requestDTO.getEndTime().trim().isEmpty()) {
                postToUpdate.setEndTime(LocalDateTime.parse(requestDTO.getEndTime(), DATETIME_FORMATTER));
            } else {
                // 如果 endTime 为空或 null，则将其设置为与 beginTime 相同
                postToUpdate.setEndTime(postToUpdate.getBeginTime());
            }
        } catch (Exception e) {
            logger.warn("[updateTravelPostText] 日期时间格式错误 for postId: {}. Details: {}", postId, e.getMessage());
            throw new BizException(BizExceptionEnum.INVALID_POST_PARAMETER);
        }

        // 校验 beginTime 是否在 endTime 之前或相等
        if (postToUpdate.getBeginTime().isAfter(postToUpdate.getEndTime())) {
            logger.warn("[updateTravelPostText] 开始时间不能晚于结束时间 for postId: {}", postId);
            throw new BizException(BizExceptionEnum.INVALID_POST_PARAMETER);
        }

        postToUpdate.setUpdatedTime(LocalDateTime.now()); // 更新修改时间

        // 5. 保存更新到数据库
        int updatedRows = travelPostMapper.update(postToUpdate);
        if (updatedRows == 0) {
            // 这种情况理论上不应该发生，因为前面已经查询并校验了 postId 的存在性和用户权限
            logger.error("[updateTravelPostText] 更新数据库失败，没有行受到影响 for postId: {}", postId);
            throw new BizException(BizExceptionEnum.DATABASE_ERROR);
        }
        logger.info("[updateTravelPostText] 旅行记录 {} 的文字内容更新成功", postId);

        // 6. 获取当前帖子的图片信息 (因为响应 DTO 需要 imageUrls)
        List<TravelPostImage> images = travelPostImageMapper.selectByPostId(postId);
        List<String> imageUrls = images.stream()
                .map(TravelPostImage::getImageUrl)
                .collect(Collectors.toList());

        // 7. 构建并返回响应 DTO
        return TravelPostUpdateResponseDTO.builder()
                .postId(postToUpdate.getId())
                .userId(postToUpdate.getUserId())
                .title(postToUpdate.getTitle())
                .content(postToUpdate.getContent())
                .locationName(postToUpdate.getLocationName())
                .latitude(postToUpdate.getLatitude())
                .longitude(postToUpdate.getLongitude())
                .beginTime(postToUpdate.getBeginTime())
                .endTime(postToUpdate.getEndTime())
                .updatedTime(postToUpdate.getUpdatedTime())
                .imageUrls(imageUrls) // 包含当前图片列表
                .build();
    }
}
