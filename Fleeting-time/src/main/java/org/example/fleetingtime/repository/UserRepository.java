package org.example.fleetingtime.repository;

import org.example.fleetingtime.bean.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User findByUsername(String username);
    void save(User user);
    void delete(User user);
}
