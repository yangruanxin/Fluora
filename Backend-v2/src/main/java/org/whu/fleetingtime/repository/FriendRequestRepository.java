package org.whu.fleetingtime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.whu.fleetingtime.entity.FriendRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    List<FriendRequest> findByReceiverIdAndStatus(String receiverId, String status);
    Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, String status);
}
