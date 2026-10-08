package com.manacommunity.homeservices.model.entity;

import com.manacommunity.homeservices.model.enums.HomeServiceBookingType;
import com.manacommunity.homeservices.model.enums.HomeServicePricingModel;
import com.manacommunity.homeservices.model.enums.HomeServiceBookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "home_service_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeServiceBookingEntity {
    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "community_id", nullable = false, length = 64)
    private String communityId;

    @Column(name = "resident_user_id", nullable = false, length = 64)
    private String residentUserId;

    @Column(name = "flat_id", length = 64)
    private String flatId;

    @Column(name = "tower", length = 50)
    private String tower;

    @Column(name = "flat_number", nullable = false, length = 50)
    private String flatNumber;

    @Column(name = "worker_id", nullable = false, length = 64)
    private String workerId;

    @Column(name = "category_id", nullable = false, length = 64)
    private String categoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false, length = 30)
    @Builder.Default
    private HomeServiceBookingType bookingType = HomeServiceBookingType.MONTHLY;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_model", nullable = false, length = 30)
    @Builder.Default
    private HomeServicePricingModel pricingModel = HomeServicePricingModel.FIXED_MONTHLY;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "recurring_days", length = 200)
    private String recurringDays;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private HomeServiceBookingStatus status = HomeServiceBookingStatus.REQUESTED;

    @Column(name = "package_id", length = 64)
    private String packageId;

    @Column(name = "sla_due_at")
    private LocalDateTime slaDueAt;

    @Column(name = "sla_breached")
    @Builder.Default
    private Boolean slaBreached = false;

    @Column(name = "completion_otp", length = 6)
    private String completionOtp;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "payment_status", length = 30)
    @Builder.Default
    private String paymentStatus = "PENDING"; // PENDING, PAID, REFUNDED

    @Column(name = "payment_transaction_id", length = 100)
    private String paymentTransactionId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
