package org.whu.fleetingtime.service;

import org.whu.fleetingtime.dto.checkin.CheckinRequestDTO;
import org.whu.fleetingtime.dto.checkin.CheckinResponseDTO;

public interface CheckinsService {
    CheckinResponseDTO checkin(String token, CheckinRequestDTO request);
}
