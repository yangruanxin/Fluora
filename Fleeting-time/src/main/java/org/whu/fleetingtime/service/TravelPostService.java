package org.whu.fleetingtime.service;

import org.whu.fleetingtime.dto.travelpost.*;

import java.util.List;

public interface TravelPostService {
    TravelPostCreateResponseDTO createTravelPost(Long userId, TravelPostCreateRequestDTO request);
    List<TravelPostGetResponseDTO> getTravelPostsByUserId(Long userId);
    void deleteTravelPost(Long userId, Long postId);
    TravelPostUpdateResponseDTO updateTravelPostText(Long userId, Long postId, TravelPostTextUpdateRequestDTO requestDTO);
}
