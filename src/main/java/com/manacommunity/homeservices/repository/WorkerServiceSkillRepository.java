package com.manacommunity.homeservices.repository;

import com.manacommunity.homeservices.model.entity.WorkerServiceSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("homeServiceWorkerServiceSkillRepository")
public interface WorkerServiceSkillRepository extends JpaRepository<WorkerServiceSkillEntity, String> {
    List<WorkerServiceSkillEntity> findByWorkerId(String workerId);
    List<WorkerServiceSkillEntity> findByCategoryId(String categoryId);
}
