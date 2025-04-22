package org.example.test.service;

import org.example.test.bean.User;


public interface UserService {
    public boolean register(User user);
    public boolean login(String username, String password) ;
    public boolean logout(User user) ;
}
