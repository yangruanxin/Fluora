package org.whu.fleetingtime.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.whu.fleetingtime.pojo.TravelPost;

import java.util.List;

@Mapper
public interface TravelPostMapper {

    // 插入打卡记录
    int insert(TravelPost post);

    // 查询用户某个城市是否已经打过卡（判断是否新城市）
    boolean existsByUserIdAndCity(@Param("userId") Long userId, @Param("city") String city);

    // 查询用户高亮城市数量（打卡过的不同城市数）
    int countDistinctCitiesByUserId(@Param("userId") Long userId);

    // 查某用户的所有打卡记录
    List<TravelPost> selectByUserId(@Param("userId") Long userId);

    // 查某个postId的记录
    TravelPost selectByPostId(@Param("postId") Long postId);

    // 删除某个打卡记录
    int deleteByPostId(@Param("postId") Long postId);

    // 删除某个用户的所有打卡记录
    int deleteByUserId(@Param("userId") Long userId);

    // 更新某个打卡记录
    int update(TravelPost post);

}
