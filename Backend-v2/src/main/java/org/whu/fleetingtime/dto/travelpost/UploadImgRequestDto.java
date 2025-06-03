package org.whu.fleetingtime.dto.travelpost;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadImgRequestDto {
    @NotNull
    @Schema(description = "要上传的图片文件", type = "string", format = "binary") // 告诉Swagger这是个文件
    MultipartFile image;

    @Schema(description = "要上传的图片所属的旅行日志的ID", example = "TestPostId")
    String  travelPostId;
}
