package com.manacommunity.homeservices.repository;

import com.manacommunity.homeservices.model.entity.PetBookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetBookingRepository extends JpaRepository<PetBookingEntity, Long> {
    List<PetBookingEntity> findByUserIdOrderByBookingDateDesc(Long userId);
    List<PetBookingEntity> findByPetIdOrderByBookingDateDesc(Long petId);
}
