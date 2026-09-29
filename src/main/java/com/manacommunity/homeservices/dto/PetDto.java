package com.manacommunity.homeservices.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private Integer ageYears;
    private String gender;
    private String color;
    private String photoUrl;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private Long communityId;
    private String tower;
    private String flatNumber;
    private Boolean isVaccinated;
    private LocalDate lastVaccinationDate;
    private String vaccinationCertificateUrl;
    private String microchipNumber;
    private Boolean isFriendlyWithKids;
    private Boolean isFriendlyWithPets;
    private String specialNotes;
    private String vetName;
    private String vetContact;
    private String status;
    private LocalDateTime createdAt;
}
