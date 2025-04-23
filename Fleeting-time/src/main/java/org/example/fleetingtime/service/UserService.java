package org.example.fleetingtime.service;

import org.example.fleetingtime.bean.User;


public interface UserService {
    public boolean register(User user);
    public boolean login(String username, String password) ;
    public boolean deactiveAccount(User user) ;
}
