package com.manacommunity.homeservices.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetServiceProviderDto {
    private Long id;
    private String name;
    private String serviceType;
    private String description;
    private String contactNumber;
    private String email;
    private String clinicOrAddress;
    private String priceRange;
    private Double basePrice;
    private Double rating;
    private Integer reviewCount;
    private String photoUrl;
    private Boolean isVerified;
    private Boolean isAvailable;
    private Long communityId;
    private String availableHours;
}
