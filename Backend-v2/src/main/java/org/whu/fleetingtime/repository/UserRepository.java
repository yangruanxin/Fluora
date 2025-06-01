package org.whu.fleetingtime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.whu.fleetingtime.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByPhone(String Phone);
    boolean existsByEmail(String Email);
    User findByUsername(String username);
    User findByPhone(String phone);
    User findByEmail(String email);
}
