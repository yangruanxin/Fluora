package org.whu.fleetingtime.service;

import org.whu.fleetingtime.dto.checkin.CheckinRequestDTO;
import org.whu.fleetingtime.dto.checkin.CheckinResponseDTO;

public interface CheckinService {
    CheckinResponseDTO checkin(String token, CheckinRequestDTO request);
}
