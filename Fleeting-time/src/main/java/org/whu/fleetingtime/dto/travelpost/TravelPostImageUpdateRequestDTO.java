package org.whu.fleetingtime.dto.travelpost;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TravelPostImageUpdateRequestDTO {
    // 需要新增的图片文件列表
    private List<MultipartFile> newImages;

    // 需要从记录中删除的旧图片 URL 列表
    private List<String> deletedImageUrls;

    // 客户端希望保留的现有图片的 URL 列表。
    // 这个列表的顺序，加上 newImages 列表的顺序，共同决定了 sortOrders 的应用对象顺序。
    private List<String> existingImageUrls;

    // 排序值数组，代表最终图片集合中各项的排序依据 (例如数据库中的 sort_order 字段的值)。
    // 数组长度必须等于 existingImageUrls.size() + newImages.size()。
    // 数组中的第 i 个值，对应 "existingImageUrls" 和 "newImages" 顺序连接形成的逻辑列表中的第 i 个图片的排序值。
    private List<Integer> sortOrders;
}
