package org.whu.fleetingtime.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.whu.fleetingtime.pojo.FriendRequest;
import org.whu.fleetingtime.pojo.UserFriend;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FriendMapper {

    // 插入好友请求
    int insertFriendRequest(FriendRequest request);

    // 查询是否存在好友请求
    FriendRequest selectFriendRequest(@Param("senderId") Long senderId,
                                      @Param("receiverId") Long receiverId);

    // 根据 ID 查询请求
    FriendRequest selectRequestById(@Param("id") Long id);

    // 更新好友请求状态
    int updateFriendRequestStatus(@Param("id") Long id,
                                  @Param("status") String status,
                                  @Param("handledTime") LocalDateTime handledTime);

    // 插入好友关系
    int insertFriend(UserFriend friend);

    // 查询用户好友列表
    List<UserFriend> selectFriendsByUserId(@Param("userId") Long userId);

    // 查询是否已经是好友（用于去重或校验）
    UserFriend selectUserFriend(@Param("userId") Long userId,
                                @Param("friendId") Long friendId);

    // 删除好友关系（双向都要删）
    int deleteUserFriend(@Param("userId") Long userId,
                         @Param("friendId") Long friendId);

    // 查询用户好友请求列表
    @Select("SELECT * FROM friend_requests WHERE receiver_id = #{userId}")
    List<FriendRequest> getReceivedRequests(@Param("userId") Long userId);
}
