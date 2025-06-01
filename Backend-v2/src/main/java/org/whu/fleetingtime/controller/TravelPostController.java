package org.whu.fleetingtime.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whu.fleetingtime.common.Result;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whu.fleetingtime.dto.travelpost.TravelPostCreateRequestDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostCreateResponseDTO;
import org.whu.fleetingtime.dto.travelpost.UploadImgRequestDto;
import org.whu.fleetingtime.dto.travelpost.UploadImgResponseDto;
import org.whu.fleetingtime.service.TravelPostService;

import java.io.IOException;

@RestController
@RequestMapping("/api/travel-posts") // 类级别的路径映射
@RequiredArgsConstructor
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
            HttpServletRequest request) throws IOException {

        String originalFilename = requestDto.getFile().getOriginalFilename();
        long fileSize = requestDto.getFile().getSize();
        logger.info("【图片上传接口】接收到上传图片请求: /api/travel-posts/upload/img, 文件名: {}, 文件大小: {} bytes", originalFilename, fileSize);

        // 从 request attribute 中获取 userId
        String userId = (String) request.getAttribute("userId");
        logger.debug("【图片上传接口】从请求属性中获取到的 userId: {}", userId);

        UploadImgResponseDto responseDto = travelPostService.uploadImage(requestDto.getFile(), userId);

        logger.info("【图片上传接口】用户 {} 的图片 {} 上传成功, imageId: {}, 返回 HTTP 200 OK", userId, originalFilename, responseDto.getImageId());
        return Result.success(responseDto);
    }
}
