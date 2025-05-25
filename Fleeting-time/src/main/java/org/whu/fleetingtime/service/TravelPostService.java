package org.whu.fleetingtime.service;

import org.whu.fleetingtime.dto.travelpost.TravelPostCreateRequestDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostCreateResponseDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostGetResponseDTO;

import java.util.List;

public interface TravelPostService {
    TravelPostCreateResponseDTO createTravelPost(Long userId, TravelPostCreateRequestDTO request);
    List<TravelPostGetResponseDTO> getTravelPostsByUserId(Long userId);
    void deleteTravelPost(Long userId, Long postId);
}
