package com.manacommunity.homeservices.service;

import com.manacommunity.homeservices.dto.HomeServiceAttendanceRequest;
import com.manacommunity.homeservices.dto.MonthlyBillCalculationResult;
import com.manacommunity.homeservices.model.entity.HomeServiceBookingEntity;
import com.manacommunity.homeservices.model.entity.HomeServiceAttendanceEntity;
import com.manacommunity.homeservices.model.enums.HomeServiceAttendanceStatus;
import com.manacommunity.homeservices.repository.HomeServiceBookingRepository;
import com.manacommunity.homeservices.repository.HomeServiceAttendanceRepository;
import com.manacommunity.homeservices.exception.HomeServiceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("homeServiceAttendanceAndBillingService")
@RequiredArgsConstructor
public class HomeServiceAttendanceAndBillingService {
    private final HomeServiceAttendanceRepository attendanceRepository;
    private final HomeServiceBookingRepository bookingRepository;

    @Transactional
    public HomeServiceAttendanceEntity markAttendance(HomeServiceAttendanceRequest req) {
        HomeServiceAttendanceEntity att = HomeServiceAttendanceEntity.builder()
                .id(UUID.randomUUID().toString())
                .bookingId(req.getBookingId())
                .workerId(req.getWorkerId())
                .residentUserId(req.getResidentUserId())
                .serviceDate(req.getServiceDate() != null ? req.getServiceDate() : LocalDate.now())
                .status(req.getStatus() != null ? req.getStatus() : HomeServiceAttendanceStatus.COMPLETED)
                .inTime(req.getInTime())
                .outTime(req.getOutTime())
                .markedBy(req.getMarkedBy())
                .notes(req.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return attendanceRepository.save(att);
    }

    @Transactional
    public HomeServiceAttendanceEntity startJobWithOtp(String attendanceId, String otp) {
        HomeServiceAttendanceEntity att = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new HomeServiceNotFoundException("Job attendance record not found: " + attendanceId));

        if (att.getVerificationPin() != null && !att.getVerificationPin().trim().equals(otp.trim())) {
            throw new com.manacommunity.homeservices.exception.HomeServiceException("Invalid OTP provided. Please ask the resident for the correct 4-digit PIN.");
        }

        att.setStatus(HomeServiceAttendanceStatus.IN_PROGRESS);
        att.setInTime(java.time.LocalTime.now());
        att.setUpdatedAt(LocalDateTime.now());
        return attendanceRepository.save(att);
    }

    @Transactional
    public HomeServiceAttendanceEntity completeJobWithProof(String attendanceId, com.manacommunity.homeservices.dto.HomeServiceJobActionRequest req) {
        HomeServiceAttendanceEntity att = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new HomeServiceNotFoundException("Job attendance record not found: " + attendanceId));

        att.setStatus(HomeServiceAttendanceStatus.COMPLETED);
        att.setOutTime(java.time.LocalTime.now());
        if (req != null) {
            if (req.getBeforePhotoUrl() != null) att.setBeforePhotoUrl(req.getBeforePhotoUrl());
            if (req.getAfterPhotoUrl() != null) att.setAfterPhotoUrl(req.getAfterPhotoUrl());
            if (req.getNotes() != null) att.setNotes(req.getNotes());
        }
        att.setUpdatedAt(LocalDateTime.now());
        return attendanceRepository.save(att);
    }

    @Transactional
    public HomeServiceAttendanceEntity rateJob(String attendanceId, Integer rating, String reviewText) {
        HomeServiceAttendanceEntity att = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new HomeServiceNotFoundException("Job attendance record not found: " + attendanceId));

        att.setRating(rating);
        att.setReviewText(reviewText);
        att.setUpdatedAt(LocalDateTime.now());
        return attendanceRepository.save(att);
    }

    public List<HomeServiceAttendanceEntity> getTodayJobsForWorker(String workerId) {
        return attendanceRepository.findByWorkerIdAndServiceDate(workerId, LocalDate.now());
    }

    public List<HomeServiceAttendanceEntity> getTodayJobsForResident(String residentUserId) {
        return attendanceRepository.findByResidentUserIdAndServiceDate(residentUserId, LocalDate.now());
    }

    public List<HomeServiceAttendanceEntity> getAttendanceHistory(String bookingId) {
        return attendanceRepository.findByBookingIdOrderByServiceDateDesc(bookingId);
    }

    public MonthlyBillCalculationResult calculateMonthlyBill(String bookingId, LocalDate fromDate, LocalDate toDate) {
        HomeServiceBookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new HomeServiceNotFoundException("Booking not found: " + bookingId));

        List<HomeServiceAttendanceEntity> logs = attendanceRepository.findByBookingIdAndServiceDateBetween(bookingId, fromDate, toDate);

        int completed = 0;
        int absent = 0;
        int leave = 0;

        for (HomeServiceAttendanceEntity log : logs) {
            if (log.getStatus() == HomeServiceAttendanceStatus.COMPLETED) completed++;
            else if (log.getStatus() == HomeServiceAttendanceStatus.ABSENT) absent++;
            else if (log.getStatus() == HomeServiceAttendanceStatus.LEAVE) leave++;
        }

        BigDecimal basePrice = booking.getPrice();
        BigDecimal totalBill = BigDecimal.ZERO;
        String formula = "";

        switch (booking.getPricingModel()) {
            case FIXED_MONTHLY:
                totalBill = basePrice;
                formula = "Fixed monthly rate applied.";
                break;
            case PER_DAY:
            case PER_VISIT:
                totalBill = basePrice.multiply(BigDecimal.valueOf(completed));
                formula = completed + " completed days × ₹" + basePrice + "/day";
                break;
            case PER_HOUR:
                totalBill = basePrice.multiply(BigDecimal.valueOf(completed));
                formula = completed + " hours × ₹" + basePrice + "/hr";
                break;
        }

        return MonthlyBillCalculationResult.builder()
                .totalScheduledDays(logs.size())
                .completedDays(completed)
                .absentDays(absent)
                .leaveDays(leave)
                .basePrice(basePrice)
                .totalBill(totalBill)
                .pricingModel(booking.getPricingModel().name())
                .formulaSummary(formula)
                .build();
    }
}
