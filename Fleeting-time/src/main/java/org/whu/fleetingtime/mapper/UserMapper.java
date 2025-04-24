package org.whu.fleetingtime.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.whu.fleetingtime.pojo.User;

@Mapper
public interface UserMapper {
    User selectByUsername(String username);
    int insertUser(User user);
    int  deleteByUserID(Long id);
}
