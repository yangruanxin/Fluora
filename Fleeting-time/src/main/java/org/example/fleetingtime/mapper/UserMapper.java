package org.example.fleetingtime.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.fleetingtime.bean.User;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    void save(User user);
    void delete(Long id);
}
