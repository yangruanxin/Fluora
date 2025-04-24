package org.whu.fleetingtime.service;

import org.whu.fleetingtime.pojo.User;

public interface UserService {
    User login(String username, String password);
    boolean register(User user);
}
