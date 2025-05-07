package org.whu.fleetingtime.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.travelpost.TravelPostRequestDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostResponseDTO;
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
import java.util.List;
import java.util.UUID;

@Service
public class TravelPostServiceImpl implements TravelPostService {

    private static final Logger logger = LoggerFactory.getLogger(TravelPostServiceImpl.class);

    @Autowired
    TravelPostMapper travelPostMapper;
    @Autowired
    TravelPostImageMapper travelPostImageMapper;

    @Override
    public TravelPostResponseDTO createTravelPost(Long userId, TravelPostRequestDTO request) {
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
            if(images.size() != orders.size()) {
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
        return TravelPostResponseDTO.builder()
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
}
