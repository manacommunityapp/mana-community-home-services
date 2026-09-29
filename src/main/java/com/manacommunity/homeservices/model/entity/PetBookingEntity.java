package com.manacommunity.homeservices.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_service_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetBookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "service_type", nullable = false)
    private String serviceType;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "pet_name", nullable = false)
    private String petName;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "tower")
    private String tower;

    @Column(name = "flat_number")
    private String flatNumber;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @Column(length = 1000)
    private String notes;

    @Builder.Default
    private String status = "CONFIRMED"; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    private Double amount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
