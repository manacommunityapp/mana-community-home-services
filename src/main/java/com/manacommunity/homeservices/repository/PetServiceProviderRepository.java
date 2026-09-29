package com.manacommunity.homeservices.repository;

import com.manacommunity.homeservices.model.entity.PetServiceProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetServiceProviderRepository extends JpaRepository<PetServiceProviderEntity, Long> {
    List<PetServiceProviderEntity> findByServiceTypeIgnoreCase(String serviceType);
}
