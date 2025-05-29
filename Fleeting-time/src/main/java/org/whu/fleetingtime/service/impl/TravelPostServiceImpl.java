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
import java.util.*;
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

    @Override
    public TravelPostImageUpdateResponseDTO updateTravelPostImages(Long userId, Long postId, TravelPostImageUpdateRequestDTO requestDTO) {
        logger.info("[updateTravelPostImages] 用户 {} 开始更新帖子 {} 的图片", userId, postId);

        // 1. 权限校验: 检查帖子是否存在且属于该用户
        TravelPost post = travelPostMapper.selectByPostId(postId);
        if (post == null) {
            throw new BizException(BizExceptionEnum.POST_NOT_FOUND);
        }
        if (!post.getUserId().equals(userId)) {
            throw new BizException(BizExceptionEnum.UNAUTHORIZED_OPERATION);
        }

        // 初始化参数，处理null情况
        List<MultipartFile> newImageFiles = requestDTO.getNewImages() != null ? requestDTO.getNewImages() : new ArrayList<>(0);
        List<String> deletedImageUrls = requestDTO.getDeletedImageUrls() != null ? requestDTO.getDeletedImageUrls() : new ArrayList<>(0);
        List<String> existingImageUrlsToKeep = requestDTO.getExistingImageUrls() != null ? requestDTO.getExistingImageUrls() : new ArrayList<>(0);
        List<Integer> sortOrders = requestDTO.getSortOrders() != null ? requestDTO.getSortOrders() : new ArrayList<>(0);

        // 2. 校验 sortOrders 长度
        int expectedTotalImages = existingImageUrlsToKeep.size() + newImageFiles.size();
        if (sortOrders.size() != expectedTotalImages) {
            logger.warn("[updateTravelPostImages] sortOrders 长度 ({}) 与预期图片总数 ({}) 不匹配", sortOrders.size(), expectedTotalImages);
            throw new BizException(BizExceptionEnum.INVALID_POST_PARAMETER, "sortOrders 长度与预期图片总数不匹配");
        }

        // 3. 获取当前数据库中的图片信息
        List<TravelPostImage> currentDbImages = travelPostImageMapper.selectByPostId(postId);
        Map<String, TravelPostImage> currentDbImagesMap = currentDbImages.stream()
                .collect(Collectors.toMap(TravelPostImage::getImageUrl, img -> img, (img1, img2) -> img1)); // 处理可能的URL重复（理论上不应有）

        // 4. 处理要删除的图片
        List<TravelPostImage> imagesToDeleteFromDb = new ArrayList<>();
        for (String urlToDelete : deletedImageUrls) {
            if (currentDbImagesMap.containsKey(urlToDelete)) {
                imagesToDeleteFromDb.add(currentDbImagesMap.get(urlToDelete));
                logger.info("[updateTravelPostImages] 标记删除图片 (DB): {}", urlToDelete);
            } else {
                logger.warn("[updateTravelPostImages] 请求删除的图片URL {} 不在当前帖子图片列表中，忽略", urlToDelete);
            }
            // 从要保留的列表中也移除，以防客户端错误提交通时包含在两处
            existingImageUrlsToKeep.remove(urlToDelete);
        }

        // 5. 上传新图片 (先上传，拿到URL后再统一处理数据库)
        List<String> newUploadedImageUrls = new ArrayList<>();
        List<MultipartFile> validNewImageFiles = newImageFiles.stream().filter(f -> f != null && !f.isEmpty()).collect(Collectors.toList());

        for (MultipartFile imageFile : validNewImageFiles) {
            String suffix = StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
            String objectName = "travelPostImage/" + userId + "/" + UUID.randomUUID().toString() + "." + suffix;
            InputStream inputStream;
            try {
                inputStream = imageFile.getInputStream();
                String imageUrl = AliyunOssUtils.upload(objectName, inputStream); // 你的OSS上传方法
                newUploadedImageUrls.add(imageUrl);
                logger.info("[updateTravelPostImages] 新图片上传成功: {}", imageUrl);
            } catch (IOException e) {
                logger.error("[updateTravelPostImages] 获取新图片输入流失败", e);
                throw new BizException(BizExceptionEnum.IMAGE_UPLOAD_FAILED, "新图片上传失败：" + e.getMessage());
            }
        }
        // 确保上传成功的图片数量和参与排序的新图片数量一致
        if (newUploadedImageUrls.size() != validNewImageFiles.size()) {
            // 如果有上传失败，但部分成功，需要决策是回滚还是怎样。这里简单抛异常。
            // 如果OSS上传支持批量且原子性，会更好。否则，需要补偿删除已上传的。
            logger.error("[updateTravelPostImages] 新图片上传数量与预期不符，可能部分上传失败");
            // 清理本轮已上传的图片（如果需要）
            for(String uploadedUrl : newUploadedImageUrls) {
                try { AliyunOssUtils.delete(AliyunOssUtils.extractObjectNameFromUrl(uploadedUrl)); }
                catch(Exception e) { logger.warn("清理部分上传的图片 {} 失败", uploadedUrl, e); }
            }
            throw new BizException(BizExceptionEnum.IMAGE_UPLOAD_FAILED, "部分新图片上传失败");
        }


        // 6. 构建最终要保存到数据库的图片信息列表 (TravelPostImage 对象)
        List<TravelPostImage> finalImagesToSave = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        int sortOrderIdx = 0;

        // 处理要保留的现有图片
        for (String existingUrl : existingImageUrlsToKeep) {
            TravelPostImage dbImage = currentDbImagesMap.get(existingUrl);
            if (dbImage == null) {
                logger.warn("[updateTravelPostImages] existingImageUrls 中的 URL {} 未在数据库中找到，可能已被删除或URL错误", existingUrl);
                // 根据业务决定是报错还是忽略。为保持健壮，这里选择忽略并继续，但前端应保证数据正确性。
                // 若要严格，则应抛异常：throw new BizException(BizExceptionEnum.INVALID_POST_PARAMETER, "要保留的图片URL无效: " + existingUrl);
                // 但如果忽略，会导致 sortOrders 长度与实际处理的图片数量不匹配，所以还是报错比较好
                throw new BizException(BizExceptionEnum.INVALID_POST_PARAMETER, "要保留的图片URL无效或已被删除: " + existingUrl);
            }
            dbImage.setSortOrder(sortOrders.get(sortOrderIdx++));
            finalImagesToSave.add(dbImage);
        }

        // 处理新上传的图片
        for (String newUrl : newUploadedImageUrls) {
            TravelPostImage newPostImage = new TravelPostImage();
            newPostImage.setPostId(postId);
            newPostImage.setImageUrl(newUrl);
            newPostImage.setSortOrder(sortOrders.get(sortOrderIdx++));
            newPostImage.setCreatedTime(now);
            // newPostImage.setUpdatedTime(now); // 如果有
            finalImagesToSave.add(newPostImage);
        }

        // 7. 数据库操作：
        // 7.1 删除标记的旧图片 (DB层面)
        for (TravelPostImage imgToDelete : imagesToDeleteFromDb) {
            travelPostImageMapper.deleteById(imgToDelete.getId()); // 假设有 deleteById
        }
        // 7.2 删除所有当前帖子的其他图片记录 (除了要更新和新加的)，为重新插入或更新做准备
        // 一个更简单的方式是：先删除所有旧的关联记录，然后重新插入 finalImagesToSave
        // 但这需要确保 finalImagesToSave 包含所有要保留的图片，并且它们的ID是用于更新还是新建要分清
        // 为了简化，这里采取先删除所有，再全部插入的策略（但OSS删除需在此之前对真正不要的图片完成）

        // 识别出不再需要的旧图片（既不在删除列表，也不在保留更新列表）
        Set<String> finalUrlsToKeep = finalImagesToSave.stream()
                .map(TravelPostImage::getImageUrl)
                .collect(Collectors.toSet());
        for(TravelPostImage currentDbImg : currentDbImages) {
            boolean explicitlyDeleted = imagesToDeleteFromDb.stream().anyMatch(del -> del.getId().equals(currentDbImg.getId()));
            if (!explicitlyDeleted && !finalUrlsToKeep.contains(currentDbImg.getImageUrl())) {
                // 这张图片没有被要求保留，也没有被明确要求删除，所以它也应该被删除
                imagesToDeleteFromDb.add(currentDbImg); // 加入到待删除列表，统一处理OSS
                travelPostImageMapper.deleteById(currentDbImg.getId()); // 从DB删除
                logger.info("[updateTravelPostImages] 隐式删除图片 (DB): {}", currentDbImg.getImageUrl());
            }
        }


        // 7.3 实际从OSS删除文件 (在DB操作成功前或作为事务一部分，失败需回滚或补偿)
        // 这一步非常关键，建议OSS删除失败时也记录下来，可能需要后续补偿任务
        for (TravelPostImage imgToDelete : imagesToDeleteFromDb) {
            try {
                String objectName = AliyunOssUtils.extractObjectNameFromUrl(imgToDelete.getImageUrl());
                if (objectName != null && !objectName.isEmpty()) {
                    AliyunOssUtils.delete(objectName);
                    logger.info("[updateTravelPostImages] 成功从OSS删除图片: {}", imgToDelete.getImageUrl());
                }
            } catch (Exception e) {
                logger.error("[updateTravelPostImages] 从OSS删除图片 {} 失败: {}", imgToDelete.getImageUrl(), e.getMessage(), e);
                // 根据业务需求决定是否因为OSS删除失败而抛出异常中断整个操作
                // throw new BizException(BizExceptionEnum.OSS_DELETE_ERROR, "OSS文件删除失败: " + imgToDelete.getImageUrl());
            }
        }

        // 7.4 清理旧的DB记录并插入/更新新的
        // 为了处理 sortOrder 的干净更新，通常做法是删除postId关联的所有图片，然后重新批量插入
        travelPostImageMapper.deleteByPostId(postId); // 先删除所有与该postId关联的图片记录
        for (TravelPostImage imageToSave : finalImagesToSave) {
            // 因为上面全删除了，所以这里都是insert
            // 如果 TravelPostImage 的 id 是自增的，需要将 imageToSave.id 设为 null (或确保 insert 不依赖旧 id)
            imageToSave.setId(null); // 确保是新增
            imageToSave.setPostId(postId); // 再次确认postId
            if(imageToSave.getCreatedTime() == null) imageToSave.setCreatedTime(now); // 确保新图片有创建时间
            travelPostImageMapper.insert(imageToSave);
        }
        logger.info("[updateTravelPostImages] 完成数据库图片记录更新 for postId: {}", postId);

        // 8. 更新 TravelPost 的 updatedTime
        post.setUpdatedTime(now);
        travelPostMapper.update(post);

        // 9. 构建响应
        // 从数据库重新查询，确保顺序正确
        List<TravelPostImage> updatedImagesFromDb = travelPostImageMapper.selectByPostId(postId); // 假设有这个按sortOrder排序的方法
        List<String> finalImageUrlsInOrder = updatedImagesFromDb.stream()
                .map(TravelPostImage::getImageUrl)
                .collect(Collectors.toList());

        return TravelPostImageUpdateResponseDTO.builder()
                .postId(postId)
                .finalImageUrlsInOrder(finalImageUrlsInOrder)
                .postUpdatedTime(post.getUpdatedTime())
                .build();
    }
}
