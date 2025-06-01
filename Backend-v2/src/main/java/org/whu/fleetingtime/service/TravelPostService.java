package org.whu.fleetingtime.service;

import org.springframework.web.multipart.MultipartFile;
import org.whu.fleetingtime.dto.PageRequestDTO;
import org.whu.fleetingtime.dto.PageResponseDTO;
import org.whu.fleetingtime.dto.TravelPostSummaryDTO;
import org.whu.fleetingtime.dto.travelpost.*;

import java.io.IOException;

public interface TravelPostService {

    UploadImgResponseDto uploadImage(MultipartFile file, String userId) throws IOException;

    TravelPostCreateResponseDTO createTravelPost(String userId, TravelPostCreateRequestDTO dto);

    PageResponseDTO<TravelPostSummaryDTO> getMyTravelPosts(String userId, PageRequestDTO pageRequestDTO);

    void deleteTravelPost(String userId, String postId);

    TravelPostUpdateResponseDTO updateTravelPostText(String userId, String postId, TravelPostTextUpdateRequestDTO dto);

    TravelPostImagesUpdateResponseDTO updateTravelPostImages(String userId, String postId, TravelPostImagesUpdateRequestDTO dto);

}
