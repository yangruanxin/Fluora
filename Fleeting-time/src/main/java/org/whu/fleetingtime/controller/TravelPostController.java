package org.whu.fleetingtime.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.dto.travelpost.*;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.service.TravelPostService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/travel-posts")
public class TravelPostController {
    private static final Logger logger = LoggerFactory.getLogger(TravelPostController.class);

    @Autowired
    private TravelPostService travelPostService;

    @PostMapping
    public Result<TravelPostCreateResponseDTO> createPost(
            HttpServletRequest request,
            @ModelAttribute TravelPostCreateRequestDTO travelPostRequestDTO
    ) {
        // 从拦截器注入的 request 属性中拿 userId
        String userIdStr = (String) request.getAttribute("userId");
        Long userId = Long.parseLong(userIdStr);
        logger.info("【创建旅行记录请求】id: {}, {}", userIdStr, travelPostRequestDTO);
//        logger.info("看看数组长度, images:{}, order: {}",
//                travelPostRequestDTO.getImages().size(),
//                travelPostRequestDTO.getOrders().size());
        TravelPostCreateResponseDTO response = travelPostService.createTravelPost(userId, travelPostRequestDTO);
        logger.info("【旅行记录创建成功】{}", response);
        return Result.success("post creation success", response);
    }

    @GetMapping("/me")
    public Result<List<TravelPostGetResponseDTO>> getMyTravelPosts(HttpServletRequest request) {
        String userIdStr = (String) request.getAttribute("userId");
        Long userId = Long.parseLong(userIdStr);
        List<TravelPostGetResponseDTO> postList = travelPostService.getTravelPostsByUserId(userId);
        return Result.success(postList);
    }

    @DeleteMapping("/{postId}")
    public Result<Object> deletePost(HttpServletRequest request, @PathVariable Long postId) {
        String userIdStr = (String) request.getAttribute("userId");
        Long userId = Long.parseLong(userIdStr);
        travelPostService.deleteTravelPost(userId, postId);
        return Result.success();
    }

    @PutMapping("/{postId}")
    public Result<TravelPostUpdateResponseDTO> updatePostText(
            HttpServletRequest request,
            @PathVariable Long postId,
            @ModelAttribute TravelPostTextUpdateRequestDTO updateRequestDTO // form-data 对应 @ModelAttribute
    ) {
        String userIdStr = (String) request.getAttribute("userId");
        Long userId = Long.parseLong(userIdStr);
        logger.info("【修改旅行记录文字内容请求】userId: {}, postId: {}, 更新数据: {}", userId, postId, updateRequestDTO);

        TravelPostUpdateResponseDTO response = travelPostService.updateTravelPostText(userId, postId, updateRequestDTO);

        logger.info("【旅行记录文字内容修改成功】响应: {}", response);
        return Result.success("记录更新成功", response);
    }

    @PostMapping("/images/{postId}") // 使用 POST
    public Result<TravelPostImageUpdateResponseDTO> updatePostImages(
            HttpServletRequest request,
            @PathVariable Long postId,
            @ModelAttribute TravelPostImageUpdateRequestDTO imageUpdateRequestDTO // form-data 用 @ModelAttribute
    ) {
        String userIdStr = (String) request.getAttribute("userId");
        Long userId = Long.parseLong(userIdStr);
        logger.info("【修改旅行记录图片请求】userId: {}, postId: {}, 更新数据: {}", userId, postId, imageUpdateRequestDTO);

        TravelPostImageUpdateResponseDTO response = travelPostService.updateTravelPostImages(userId, postId, imageUpdateRequestDTO);

        logger.info("【旅行记录图片修改成功】响应: {}", response);
        return Result.success("图片更新成功", response);
    }
}
