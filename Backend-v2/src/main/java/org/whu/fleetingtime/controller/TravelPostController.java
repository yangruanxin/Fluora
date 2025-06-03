package org.whu.fleetingtime.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.whu.fleetingtime.common.Result;
import org.springframework.http.MediaType;
import org.whu.fleetingtime.dto.PageRequestDTO;
import org.whu.fleetingtime.dto.PageResponseDTO;
import org.whu.fleetingtime.dto.TravelPostDetailsDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostSummaryDTO;
import org.whu.fleetingtime.dto.travelpost.*;
import org.whu.fleetingtime.service.TravelPostService;


@RestController
@RequestMapping("/api/travel-posts") // 类级别的路径映射
@RequiredArgsConstructor
@Tag(name = "旅行日志接口", description = "进行图片的上传，旅行日志的增删改查操作")
public class TravelPostController {
    private static final Logger logger = LoggerFactory.getLogger(TravelPostController.class); // 定义 Logger 对象

    private final TravelPostService travelPostService;

    @PostMapping
    @Operation(summary = "创建新的旅行日志", description = "包含文本内容和关联已上传的图片ID列表。")
    @ApiResponse(responseCode = "200", description = "旅行日志创建成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TravelPostCreateResponseDTO.class)))
    public Result<TravelPostCreateResponseDTO> createTravelPost(
            @Valid @org.springframework.web.bind.annotation.RequestBody TravelPostCreateRequestDTO createRequestDTO, // 注意是Spring的@RequestBody
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId"); // 从JWT拦截器注入的userId

        logger.info("【创建旅行日志】用户 {} 尝试创建新的旅行日志, 请求数据: {}", userId, createRequestDTO);

        TravelPostCreateResponseDTO responseDTO = travelPostService.createTravelPost(userId, createRequestDTO);

        logger.info("【创建旅行日志】用户 {} 的旅行日志创建成功, 帖子ID: {}", userId, responseDTO.getPostId());
        // HTTP 200 Created 通常更适合创建操作，并可选择在Location头中返回新资源的URI
        return Result.success(responseDTO);
    }

    @PostMapping(value = "/upload/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 明确 consumes 类型
    @Operation(
            summary = "上传图片 (通过DTO定义表单)",
            description = "接收包含图片文件的表单数据并处理上传。",
            // 2. 关键改动：明确描述请求体
            requestBody = @RequestBody( // 使用 io.swagger.v3.oas.annotations.parameters.RequestBody
                    description = "包含图片文件和其他可能的表单字段",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            // schema 指向你的DTO，Swagger会根据DTO中的@Schema注解来渲染表单字段
                            schema = @Schema(implementation = UploadImgRequestDto.class)
                    )
            )
    )
    public Result<UploadImgResponseDto> uploadImage(
            @Valid UploadImgRequestDto requestDto,
            HttpServletRequest request) {

        String originalFilename = requestDto.getImage().getOriginalFilename();
        long fileSize = requestDto.getImage().getSize();
        logger.info("【图片上传接口】接收到上传图片请求: /api/travel-posts/upload/img, 文件名: {}, 文件大小: {} bytes", originalFilename, fileSize);

        // 从 request attribute 中获取 userId
        String userId = (String) request.getAttribute("userId");
        logger.debug("【图片上传接口】从请求属性中获取到的 userId: {}", userId);

        UploadImgResponseDto responseDto = travelPostService.uploadImageAndAssociate(requestDto, userId);

        logger.info("【图片上传接口】用户 {} 的图片 {} 上传成功, imageId: {}, 返回 HTTP 200 OK", userId, originalFilename, responseDto.getImageId());
        return Result.success(responseDto);
    }


    @GetMapping("/me")
    @Operation(summary = "分页查询当前用户的旅行日志", description = "获取当前登录用户创建的旅行日志列表，支持分页和排序。")
    public Result<PageResponseDTO<TravelPostSummaryDTO>> getMyTravelPosts(
            @Valid @ParameterObject PageRequestDTO pageRequestDTO, // 使用 @ParameterObject 让Swagger将DTO字段作为独立参数展示
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");

        logger.info("【查询我的旅行日志】用户 {} 请求分页数据: {}", userId, pageRequestDTO);
        PageResponseDTO<TravelPostSummaryDTO> responseData = travelPostService.getMyTravelPosts(userId, pageRequestDTO);
        logger.info("【查询我的旅行日志】用户 {} 查询成功，返回 {} 条记录，总页数 {}", userId, responseData.getNumberOfElements(), responseData.getTotalPages());

        return Result.success(responseData);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "删除指定的旅行日志", description = "逻辑删除一篇旅行日志及其所有关联图片（包括OSS文件）。")
    public Result<Void> deleteTravelPost(
            @PathVariable String postId,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");

        logger.info("【删除旅行日志接口】用户 {} 请求删除帖子ID: {}", userId, postId);
        travelPostService.deleteTravelPost(userId, postId); // Service层会处理异常并可能抛出BizException
        logger.info("【删除旅行日志接口】用户 {} 的帖子ID {} 删除成功", userId, postId);
        return Result.success("旅行日志删除成功", null); // 成功则返回200 OK 和成功消息
    }

    @PutMapping("/{postId}")
    @Operation(summary = "更新旅行日志的文本内容", description = "根据帖子ID更新其标题、内容、地点、时间等信息。")
    public Result<TravelPostUpdateResponseDTO> updateTravelPostText(
            @PathVariable String postId,
            @Valid @org.springframework.web.bind.annotation.RequestBody TravelPostTextUpdateRequestDTO updateRequestDTO,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        logger.info("【更新日志文本】用户 {} 请求更新帖子ID: {}, 数据: {}", userId, postId, updateRequestDTO);

        TravelPostUpdateResponseDTO responseDTO = travelPostService.updateTravelPostText(userId, postId, updateRequestDTO);

        logger.info("【更新日志文本】用户 {} 的帖子ID {} 文本内容更新成功", userId, postId);
        return Result.success("旅行日志文本更新成功", responseDTO);
    }

    @PutMapping("/{postId}/images")
    @Operation(summary = "更新旅行日志的关联图片和顺序",
            description = "接收一个包含图片ID和顺序的完整列表，作为该帖子最终的图片状态。")
    public Result<TravelPostImagesUpdateResponseDTO> updateTravelPostImages(
            @PathVariable String postId,
            @Valid @org.springframework.web.bind.annotation.RequestBody TravelPostImagesUpdateRequestDTO updateImagesRequestDTO,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        logger.info("【更新日志图片】用户 {} 请求更新帖子ID: {} 的图片, 新图片列表包含 {} 项",
                userId, postId, updateImagesRequestDTO.getImages() != null ? updateImagesRequestDTO.getImages().size() : 0);

        TravelPostImagesUpdateResponseDTO responseDTO = travelPostService.updateTravelPostImages(userId, postId, updateImagesRequestDTO);

        logger.info("【更新日志图片】用户 {} 的帖子ID {} 图片更新成功", userId, postId);
        return Result.success("图片关联和顺序更新成功", responseDTO);
    }


    @GetMapping("/{postId}")
    @Operation(summary = "获取指定ID的旅行日志详情", description = "获取当前用户创建的某一篇特定旅行日志的全部详细信息，包括图片的预签名URL。")
    public ResponseEntity<Result<TravelPostDetailsDTO>> getMyTravelPostDetailsById(
            @PathVariable String postId,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");
        // if (userId == null || userId.trim().isEmpty()) { ... 处理未授权，理论上拦截器会处理 ... }

        logger.info("【查询帖子详情接口】用户 {} 请求查询帖子ID: {}", userId, postId);
        TravelPostDetailsDTO responseData = travelPostService.getMyTravelPostDetails(userId, postId);
        // Service层会处理帖子不存在或无权限的情况并抛出BizException，由GlobalExceptionHandler处理

        logger.info("【查询帖子详情接口】用户 {} 的帖子ID {} 查询成功", userId, postId);
        return ResponseEntity.ok(Result.success(responseData));
    }
}