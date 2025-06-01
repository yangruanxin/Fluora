package org.whu.fleetingtime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.whu.fleetingtime.entity.TravelPostImage;



@Repository
public interface TravelPostImageRepository extends JpaRepository<TravelPostImage, String> {
    // JpaRepository 已经提供了 save(), findById() 等常用方法
    // 你可以在这里添加自定义的查询方法，比如根据 objectKey 查找等 (如果需要)
}
