package org.whu.fleetingtime.dto.travelpost;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageAssociationDTO {
    @NotBlank(message = "图片ID不能为空")
    @Schema(description = "已上传图片的ID (TravelPostImage的UUID)", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
    private String imageId;

    @NotNull(message = "图片顺序不能为空")
    @Schema(description = "图片在此帖子中的排序值 (从0开始)", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer sortOrder;
}
