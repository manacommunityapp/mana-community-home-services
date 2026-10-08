package com.manacommunity.homeservices.service;

import com.manacommunity.homeservices.dto.HomeServiceBookingRequest;
import com.manacommunity.homeservices.model.entity.HomeServiceBookingEntity;
import com.manacommunity.homeservices.model.entity.WorkerFlatAssignmentEntity;
import com.manacommunity.homeservices.model.enums.HomeServiceBookingStatus;
import com.manacommunity.homeservices.repository.HomeServiceBookingRepository;
import com.manacommunity.homeservices.repository.WorkerFlatAssignmentRepository;
import com.manacommunity.homeservices.exception.SlotConflictException;
import com.manacommunity.homeservices.exception.HomeServiceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("homeServiceBookingService")
@RequiredArgsConstructor
public class HomeServiceBookingService {
    private final HomeServiceBookingRepository bookingRepository;
    private final WorkerFlatAssignmentRepository assignmentRepository;

    @Transactional
    public HomeServiceBookingEntity createBooking(HomeServiceBookingRequest request) {
        List<HomeServiceBookingEntity> conflicts = bookingRepository.findConflictingBookings(
                request.getWorkerId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new SlotConflictException(
                    "Worker already has an active booking between " + request.getStartTime() + " and " + request.getEndTime()
            );
        }

        int slaHours = request.getSlaHours() != null && request.getSlaHours() > 0 ? request.getSlaHours() : 24;
        LocalDateTime slaDueAt = LocalDateTime.now().plusHours(slaHours);
        String completionOtp = String.format("%04d", (int) (Math.random() * 9000) + 1000);

        HomeServiceBookingEntity booking = HomeServiceBookingEntity.builder()
                .id(UUID.randomUUID().toString())
                .communityId(request.getCommunityId())
                .residentUserId(request.getResidentUserId())
                .flatNumber(request.getFlatNumber())
                .tower(request.getTower())
                .workerId(request.getWorkerId())
                .categoryId(request.getCategoryId())
                .packageId(request.getPackageId())
                .bookingType(request.getBookingType())
                .pricingModel(request.getPricingModel())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .recurringDays(request.getRecurringDays() != null ? String.join(",", request.getRecurringDays()) : null)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .status(HomeServiceBookingStatus.REQUESTED)
                .slaDueAt(slaDueAt)
                .slaBreached(false)
                .completionOtp(completionOtp)
                .paymentStatus("PENDING")
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }

    @Transactional
    public HomeServiceBookingEntity updateBookingStatus(String bookingId, HomeServiceBookingStatus newStatus, String cancellationReason) {
        HomeServiceBookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new HomeServiceNotFoundException("Booking not found: " + bookingId));

        booking.setStatus(newStatus);
        if (cancellationReason != null) {
            booking.setCancellationReason(cancellationReason);
        }
        booking.setUpdatedAt(LocalDateTime.now());

        if (newStatus == HomeServiceBookingStatus.CONFIRMED) {
            WorkerFlatAssignmentEntity assignment = WorkerFlatAssignmentEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .workerId(booking.getWorkerId())
                    .communityId(booking.getCommunityId())
                    .flatNumber(booking.getFlatNumber())
                    .tower(booking.getTower())
                    .residentUserId(booking.getResidentUserId())
                    .serviceCategoryId(booking.getCategoryId())
                    .scheduleSummary(booking.getStartTime() + " - " + booking.getEndTime())
                    .startDate(booking.getStartDate())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            assignmentRepository.save(assignment);
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public HomeServiceBookingEntity completeBookingWithOtp(String bookingId, String enteredOtp) {
        HomeServiceBookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new HomeServiceNotFoundException("Booking not found: " + bookingId));

        if (booking.getCompletionOtp() != null && !booking.getCompletionOtp().equals(enteredOtp.trim())) {
            throw new com.manacommunity.homeservices.exception.HomeServiceException("Invalid completion OTP. Please provide the 4-digit code shown in resident app.");
        }

        booking.setStatus(HomeServiceBookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());
        if (booking.getSlaDueAt() != null && LocalDateTime.now().isAfter(booking.getSlaDueAt())) {
            booking.setSlaBreached(true);
        }
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Transactional
    public HomeServiceBookingEntity recordPayment(String bookingId, String transactionId) {
        HomeServiceBookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new HomeServiceNotFoundException("Booking not found: " + bookingId));

        booking.setPaymentStatus("PAID");
        booking.setPaymentTransactionId(transactionId != null ? transactionId : "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setPaidAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public List<HomeServiceBookingEntity> getBookingsByResident(String residentUserId) {
        return bookingRepository.findByResidentUserIdOrderByCreatedAtDesc(residentUserId);
    }

    public List<HomeServiceBookingEntity> getBookingsByWorker(String workerId) {
        return bookingRepository.findByWorkerIdOrderByCreatedAtDesc(workerId);
    }
}
