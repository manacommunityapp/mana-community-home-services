package com.manacommunity.homeservices.repository;

import com.manacommunity.homeservices.model.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<PetEntity, Long> {
    List<PetEntity> findByCommunityIdOrderByIdDesc(Long communityId);
    List<PetEntity> findByOwnerIdOrderByIdDesc(Long ownerId);

    @Query("SELECT p FROM PetEntity p WHERE (:species IS NULL OR LOWER(p.species) = LOWER(:species)) " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.ownerName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<PetEntity> searchPets(@Param("species") String species, @Param("search") String search);
}
