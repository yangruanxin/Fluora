package org.whu.fleetingtime.service;

import org.whu.fleetingtime.dto.checkin.CheckinRequestDTO;
import org.whu.fleetingtime.dto.checkin.CheckinResponseDTO;

public interface TravelPostService {
    CheckinResponseDTO checkin(Long userId, CheckinRequestDTO request);
}
