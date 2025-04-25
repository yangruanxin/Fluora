package org.whu.fleetingtime.service;

import org.whu.fleetingtime.dto.CheckinRequestDTO;
import org.whu.fleetingtime.dto.CheckinResponseDTO;

public interface CheckinsService {
    CheckinResponseDTO checkin(String token, CheckinRequestDTO request);
}
