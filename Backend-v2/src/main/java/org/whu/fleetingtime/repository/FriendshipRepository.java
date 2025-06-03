package org.whu.fleetingtime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.whu.fleetingtime.entity.Friendship;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, String> {
    List<Friendship> findByUserId(String userId);
    boolean existsByUserIdAndFriendId(String userId, String friendId);
    void deleteByUserIdAndFriendId(String userId, String friendId);
}