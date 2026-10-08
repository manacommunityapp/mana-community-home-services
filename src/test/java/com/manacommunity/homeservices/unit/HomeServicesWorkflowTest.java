package com.manacommunity.homeservices.unit;

import com.manacommunity.homeservices.dto.HomeServiceBookingRequest;
import com.manacommunity.homeservices.dto.HomeServiceReviewRequest;
import com.manacommunity.homeservices.model.entity.*;
import com.manacommunity.homeservices.model.enums.*;
import com.manacommunity.homeservices.repository.*;
import com.manacommunity.homeservices.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Home Services End-to-End Workflow Unit Tests")
class HomeServicesWorkflowTest {

    @Mock
    private HomeServiceBookingRepository bookingRepository;

    @Mock
    private WorkerFlatAssignmentRepository assignmentRepository;

    @Mock
    private HomeServiceWorkerRepository workerRepository;

    @Mock
    private HomeServiceReviewRepository reviewRepository;

    @InjectMocks
    private HomeServiceBookingService bookingService;

    @InjectMocks
    private HomeServiceWorkerService workerService;

    @InjectMocks
    private HomeServiceReviewService reviewService;

    private HomeServiceWorkerEntity sampleWorker;

    @BeforeEach
    void setUp() {
        sampleWorker = HomeServiceWorkerEntity.builder()
                .id("w-101")
                .communityId("comm-1")
                .displayName("Ramesh Kumar")
                .primaryPhone("9876543210")
                .workerType(HomeServiceWorkerType.COMMUNITY_WORKER)
                .verificationStatus(HomeServiceVerificationStatus.PENDING)
                .rating(BigDecimal.valueOf(5.0))
                .build();
    }

    @Test
    @DisplayName("Complete Workflow: Provider Registration -> Verification -> Booking with SLA -> Assignment -> OTP Completion -> Review")
    void testEndToEndHomeServiceLifecycle() {
        // 1. Worker Registration & Verification
        when(workerRepository.findById("w-101")).thenReturn(Optional.of(sampleWorker));
        when(workerRepository.save(any(HomeServiceWorkerEntity.class))).thenAnswer(i -> i.getArgument(0));

        HomeServiceWorkerEntity verifiedWorker = workerService.updateVerification("w-101", HomeServiceVerificationStatus.VERIFIED, true, true);
        assertEquals(HomeServiceVerificationStatus.VERIFIED, verifiedWorker.getVerificationStatus());
        assertTrue(verifiedWorker.getPoliceVerified());
        assertTrue(verifiedWorker.getCommunityVerified());

        // 2. Booking Creation with SLA & OTP Generation
        when(bookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(HomeServiceBookingEntity.class))).thenAnswer(i -> i.getArgument(0));

        HomeServiceBookingRequest req = new HomeServiceBookingRequest();
        req.setCommunityId("comm-1");
        req.setResidentUserId("res-201");
        req.setFlatNumber("B-402");
        req.setTower("Tower B");
        req.setWorkerId("w-101");
        req.setCategoryId("cat-clean");
        req.setBookingType(HomeServiceBookingType.ONE_TIME);
        req.setPricingModel(HomeServicePricingModel.PER_VISIT);
        req.setStartDate(LocalDate.now());
        req.setStartTime(LocalTime.of(10, 0));
        req.setEndTime(LocalTime.of(12, 0));
        req.setSlaHours(6);
        req.setPrice(BigDecimal.valueOf(500.00));

        HomeServiceBookingEntity booking = bookingService.createBooking(req);
        assertNotNull(booking);
        assertNotNull(booking.getCompletionOtp(), "Must generate 4-digit completion OTP");
        assertEquals(HomeServiceBookingStatus.REQUESTED, booking.getStatus());
        assertNotNull(booking.getSlaDueAt());
        assertFalse(booking.getSlaBreached());

        // 3. Worker Confirmation & Flat Assignment
        String otp = booking.getCompletionOtp();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(assignmentRepository.save(any(WorkerFlatAssignmentEntity.class))).thenAnswer(i -> i.getArgument(0));

        HomeServiceBookingEntity confirmedBooking = bookingService.updateBookingStatus(booking.getId(), HomeServiceBookingStatus.CONFIRMED, null);
        assertEquals(HomeServiceBookingStatus.CONFIRMED, confirmedBooking.getStatus());
        verify(assignmentRepository, times(1)).save(any(WorkerFlatAssignmentEntity.class));

        // 4. Job Completion via Resident Verification OTP
        HomeServiceBookingEntity completedBooking = bookingService.completeBookingWithOtp(booking.getId(), otp);
        assertEquals(HomeServiceBookingStatus.COMPLETED, completedBooking.getStatus());
        assertNotNull(completedBooking.getCompletedAt());
        assertFalse(completedBooking.getSlaBreached());

        // 5. Payment settlement
        HomeServiceBookingEntity paidBooking = bookingService.recordPayment(booking.getId(), "TXN-987654");
        assertEquals("PAID", paidBooking.getPaymentStatus());
        assertEquals("TXN-987654", paidBooking.getPaymentTransactionId());

        // 6. Review & Rating Update
        HomeServiceReviewRequest reviewReq = new HomeServiceReviewRequest();
        reviewReq.setBookingId(booking.getId());
        reviewReq.setReviewerUserId("res-201");
        reviewReq.setReviewerName("Aditi Sharma");
        reviewReq.setRevieweeWorkerId("w-101");
        reviewReq.setRating(BigDecimal.valueOf(4.5));
        reviewReq.setWorkQuality(5);
        reviewReq.setPunctuality(4);
        reviewReq.setBehaviour(5);
        reviewReq.setReliability(5);
        reviewReq.setComment("Great job, on time!");

        HomeServiceReviewEntity savedReview = HomeServiceReviewEntity.builder()
                .rating(BigDecimal.valueOf(4.5))
                .build();
        when(reviewRepository.save(any(HomeServiceReviewEntity.class))).thenReturn(savedReview);
        when(reviewRepository.findByRevieweeWorkerIdOrderByCreatedAtDesc("w-101")).thenReturn(List.of(savedReview));

        HomeServiceReviewEntity review = reviewService.submitReview(reviewReq);
        assertNotNull(review);
        assertEquals(BigDecimal.valueOf(4.5), review.getRating());
    }
}
