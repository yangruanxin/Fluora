package org.whu.fleetingtime.service;

import org.whu.fleetingtime.dto.travelpost.TravelPostRequestDTO;
import org.whu.fleetingtime.dto.travelpost.TravelPostResponseDTO;

public interface TravelPostService {
    TravelPostResponseDTO createTravelPost(Long userId, TravelPostRequestDTO request);
}
