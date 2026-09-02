package com.AMiller.Follow_up_Management.Repository;

import com.AMiller.Follow_up_Management.entities.FollowUpRequests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowUpRequestsRepository extends JpaRepository<FollowUpRequests, Integer> {
    List<FollowUpRequests> findByStatus(String status);
}
