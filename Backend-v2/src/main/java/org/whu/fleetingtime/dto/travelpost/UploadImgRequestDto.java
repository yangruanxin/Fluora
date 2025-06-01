package org.whu.fleetingtime.dto.travelpost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadImgRequestDto {
    @Schema(description = "要上传的图片文件", type = "string", format = "binary") // 告诉Swagger这是个文件
    MultipartFile file;
}
