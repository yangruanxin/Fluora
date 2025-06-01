package org.whu.fleetingtime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.whu.fleetingtime.entity.TravelPost;

@Repository
public interface TravelPostRepository extends JpaRepository<TravelPost, String> {

}
