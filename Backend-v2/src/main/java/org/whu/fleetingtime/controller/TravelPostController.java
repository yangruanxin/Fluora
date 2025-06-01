package org.whu.fleetingtime.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whu.fleetingtime.dto.travelpost.UploadImgRequestDto;
import org.whu.fleetingtime.dto.travelpost.UploadImgResponseDto;
import org.whu.fleetingtime.interfaces.ITravelPostService;

import java.io.IOException;

@RestController
@RequestMapping("/api/travel-posts") // 类级别的路径映射
@RequiredArgsConstructor
public class TravelPostController {
    private static final Logger logger = LoggerFactory.getLogger(TravelPostController.class); // 定义 Logger 对象

    private final ITravelPostService travelPostService;

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
    public ResponseEntity<?> uploadImage(
            UploadImgRequestDto requestDto,
            HttpServletRequest request) {

        String originalFilename = requestDto.getFile().getOriginalFilename();
        long fileSize = requestDto.getFile().getSize();
        logger.info("【图片上传接口】接收到上传图片请求: /api/travel-posts/upload/img, 文件名: {}, 文件大小: {} bytes", originalFilename, fileSize);

        // 从 request attribute 中获取 userId
        String userId = (String) request.getAttribute("userId");
        logger.debug("【图片上传接口】从请求属性中获取到的 userId: {}", userId);


        // 基本的校验
//        if (userId == null || userId.trim().isEmpty()) {
//            logger.warn("【图片上传接口】无法获取用户信息 (userId is null or empty), 请求来自文件名: {}", originalFilename);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无法获取用户信息，请先登录。");
//        }
        userId = "TEST" ;
        if (requestDto.getFile().isEmpty()) {
            logger.warn("【图片上传接口】用户 {} 上传的文件为空, 原始文件名: {}", userId, originalFilename);
            return ResponseEntity.badRequest().body("上传文件不能为空。");
        }

        logger.info("【图片上传接口】用户 {} 准备调用 travelPostService.uploadImage 处理图片: {}", userId, originalFilename);
        try {
            UploadImgResponseDto responseDto = travelPostService.uploadImage(requestDto.getFile(), userId);
            logger.info("【图片上传接口】用户 {} 的图片 {} 上传成功, imageId: {}, 返回 HTTP 200 OK", userId, originalFilename, responseDto.getImageId());
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            logger.warn("【图片上传接口】用户 {} 上传图片 {} 失败 (参数错误): {}", userId, originalFilename, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            logger.error("【图片上传接口】用户 {} 上传图片 {} 失败 (IO异常): {}", userId, originalFilename, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("图片上传失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("【图片上传接口】用户 {} 上传图片 {} 时发生未知错误: {}", userId, originalFilename, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误，请稍后再试。");
        }
    }
}
