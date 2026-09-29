package com.manacommunity.homeservices.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pet_service_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetServiceProviderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String serviceType; // VET, WALKER, GROOMER, TRAINER, SITTER, BOARDING

    @Column(length = 1000)
    private String description;

    private String contactNumber;

    private String email;

    private String clinicOrAddress;

    private String priceRange;

    private Double basePrice;

    private Double rating;

    private Integer reviewCount;

    private String photoUrl;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "community_id")
    private Long communityId;

    private String availableHours;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
