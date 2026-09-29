package com.manacommunity.homeservices.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species; // DOG, CAT, BIRD, RABBIT, FISH, OTHER

    private String breed;

    private Integer ageYears;

    private String gender; // MALE, FEMALE

    private String color;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "owner_phone")
    private String ownerPhone;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    private String tower;

    @Column(name = "flat_number", nullable = false)
    private String flatNumber;

    @Column(name = "is_vaccinated")
    private Boolean isVaccinated;

    @Column(name = "last_vaccination_date")
    private LocalDate lastVaccinationDate;

    @Column(name = "vaccination_certificate_url")
    private String vaccinationCertificateUrl;

    @Column(name = "microchip_number")
    private String microchipNumber;

    @Column(name = "is_friendly_with_kids")
    private Boolean isFriendlyWithKids;

    @Column(name = "is_friendly_with_pets")
    private Boolean isFriendlyWithPets;

    @Column(name = "special_notes", length = 1000)
    private String specialNotes;

    @Column(name = "vet_name")
    private String vetName;

    @Column(name = "vet_contact")
    private String vetContact;

    @Builder.Default
    @Column(name = "status")
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, DECEASED, RELOCATED

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
