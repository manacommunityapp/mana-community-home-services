package com.manacommunity.homeservices.repository;

import com.manacommunity.homeservices.model.entity.HomeServiceReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("homeServiceReviewRepository")
public interface HomeServiceReviewRepository extends JpaRepository<HomeServiceReviewEntity, String> {
    List<HomeServiceReviewEntity> findByRevieweeWorkerIdOrderByCreatedAtDesc(String revieweeWorkerId);
}
