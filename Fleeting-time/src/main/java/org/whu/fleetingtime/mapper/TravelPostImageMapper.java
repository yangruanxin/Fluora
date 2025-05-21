package org.whu.fleetingtime.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.whu.fleetingtime.pojo.TravelPostImage;

import java.util.List;

@Mapper
public interface TravelPostImageMapper {
    int insert(TravelPostImage travelPostImage);
    List<TravelPostImage> selectByPostId(Long postId);
    int deleteByPostId(Long postId);
    int updateSortOrder(Long id, Integer sortOrder);
}
