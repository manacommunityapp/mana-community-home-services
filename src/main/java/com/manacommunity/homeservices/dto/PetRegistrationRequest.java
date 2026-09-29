package com.manacommunity.homeservices.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetRegistrationRequest {
    @NotBlank(message = "Pet name is required")
    private String name;

    @NotBlank(message = "Species is required (e.g. DOG, CAT, BIRD)")
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
}
