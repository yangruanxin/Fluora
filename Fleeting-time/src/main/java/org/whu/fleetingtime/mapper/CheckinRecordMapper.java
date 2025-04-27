package org.whu.fleetingtime.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.whu.fleetingtime.pojo.CheckinRecord;

import java.util.List;

@Mapper
public interface CheckinRecordMapper {

    // 插入打卡记录
    int insert(CheckinRecord record);

    // 查询用户某个城市是否已经打过卡（判断是否新城市）
    boolean existsByUserIdAndCity(@Param("userId") Long userId, @Param("city") String city);

    // 查询用户高亮城市数量（打卡过的不同城市数）
    int countDistinctCitiesByUserId(@Param("userId") Long userId);

    // 查某用户的所有打卡记录
    List<CheckinRecord> selectByUserId(@Param("userId") Long userId);
}
