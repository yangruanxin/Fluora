package org.whu.fleetingtime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.whu.fleetingtime.entity.TravelPost;

import java.util.List;

@Repository
public interface TravelPostRepository extends JpaRepository<TravelPost, String> {
    // Spring Data JPA会根据方法名自动实现分页和排序查询
    // 我们只需要传入一个 Pageable 对象即可
    Page<TravelPost> findAllByUserId(String userId, Pageable pageable);

    // 如果你还需要不分页的列表（虽然有分页后这个可能用得少）
    List<TravelPost> findAllByUserIdOrderByCreatedTimeDesc(String userId);
}
