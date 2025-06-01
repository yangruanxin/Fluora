package org.whu.fleetingtime.interfaces;

import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.travelpost.UploadImgResponseDto;

import java.io.IOException;

public interface ITravelPostService {
    /**
     * 上传单个图片文件到OSS，并在数据库中创建 TravelPostImage 记录。
     *
     * @param file   上传的文件
     * @param userId 进行操作的用户ID
     * @return 包含上传结果的 DTO
     * @throws IOException 文件处理或上传过程中可能发生的IO异常
     */
    UploadImgResponseDto uploadImage(MultipartFile file, String userId) throws IOException;
}
