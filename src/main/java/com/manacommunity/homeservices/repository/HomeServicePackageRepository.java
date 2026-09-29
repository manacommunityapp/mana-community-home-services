package com.manacommunity.homeservices.repository;

import com.manacommunity.homeservices.model.entity.HomeServicePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("homeServicePackageRepository")
public interface HomeServicePackageRepository extends JpaRepository<HomeServicePackageEntity, String> {
    List<HomeServicePackageEntity> findByCommunityIdAndActiveTrue(String communityId);
}
