package com.manacommunity.homeservices.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetBookingRequest {
    @NotNull(message = "Pet ID is required")
    private Long petId;
    private String petName;
    private Long userId;
    private String userName;
    private String userPhone;
    private String tower;
    private String flatNumber;
    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;
    @NotNull(message = "Time slot is required")
    private String timeSlot;
    private String notes;
}
