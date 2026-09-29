package com.manacommunity.homeservices.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetBookingDto {
    private Long id;
    private Long providerId;
    private String providerName;
    private String serviceType;
    private Long petId;
    private String petName;
    private Long userId;
    private String userName;
    private String userPhone;
    private String tower;
    private String flatNumber;
    private LocalDate bookingDate;
    private String timeSlot;
    private String notes;
    private String status;
    private Double amount;
    private LocalDateTime createdAt;
}
