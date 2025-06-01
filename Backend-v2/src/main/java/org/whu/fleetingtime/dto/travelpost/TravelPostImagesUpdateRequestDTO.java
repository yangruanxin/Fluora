package org.whu.fleetingtime.dto.travelpost;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class TravelPostImagesUpdateRequestDTO {

    @Valid // 对列表中的每个ImageAssociationDTO对象也进行校验
    @Schema(description = "帖子最终应包含的图片信息列表 (图片ID和对应的排序)。" +
            "后端会根据这个列表与现有图片的差异进行添加、移除和排序更新。" +
            "如果想清空所有图片，可以传一个空列表。")
    private List<ImageAssociationDTO> images; // 使用之前创建帖子时用到的ImageAssociationDTO
}