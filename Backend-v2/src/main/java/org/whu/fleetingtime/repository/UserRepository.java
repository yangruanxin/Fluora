package org.whu.fleetingtime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.whu.fleetingtime.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByPhone(String Phone);
    boolean existsByEmail(String Email);
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
}
