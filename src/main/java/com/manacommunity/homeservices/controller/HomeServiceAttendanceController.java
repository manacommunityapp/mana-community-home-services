package com.manacommunity.homeservices.controller;

import com.manacommunity.homeservices.dto.HomeServiceAttendanceRequest;
import com.manacommunity.homeservices.dto.MonthlyBillCalculationResult;
import com.manacommunity.homeservices.model.entity.HomeServiceAttendanceEntity;
import com.manacommunity.homeservices.service.HomeServiceAttendanceAndBillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController("homeServiceAttendanceController")
@RequestMapping("/api/v1/home-services/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeServiceAttendanceController {
    private final HomeServiceAttendanceAndBillingService attendanceService;

    @PostMapping
    public ResponseEntity<HomeServiceAttendanceEntity> markAttendance(@RequestBody HomeServiceAttendanceRequest req) {
        return ResponseEntity.ok(attendanceService.markAttendance(req));
    }

    @GetMapping("/today/worker/{workerId}")
    public ResponseEntity<List<HomeServiceAttendanceEntity>> getTodayJobsForWorker(@PathVariable String workerId) {
        return ResponseEntity.ok(attendanceService.getTodayJobsForWorker(workerId));
    }

    @GetMapping("/today/resident/{residentId}")
    public ResponseEntity<List<HomeServiceAttendanceEntity>> getTodayJobsForResident(@PathVariable String residentUserId) {
        return ResponseEntity.ok(attendanceService.getTodayJobsForResident(residentUserId));
    }

    @PostMapping("/{id}/start-otp")
    public ResponseEntity<HomeServiceAttendanceEntity> startJobWithOtp(
            @PathVariable String id,
            @RequestParam String otp) {
        return ResponseEntity.ok(attendanceService.startJobWithOtp(id, otp));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<HomeServiceAttendanceEntity> completeJob(
            @PathVariable String id,
            @RequestBody com.manacommunity.homeservices.dto.HomeServiceJobActionRequest req) {
        return ResponseEntity.ok(attendanceService.completeJobWithProof(id, req));
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<HomeServiceAttendanceEntity> rateJob(
            @PathVariable String id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String reviewText) {
        return ResponseEntity.ok(attendanceService.rateJob(id, rating, reviewText));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<HomeServiceAttendanceEntity>> getHistory(@PathVariable String bookingId) {
        return ResponseEntity.ok(attendanceService.getAttendanceHistory(bookingId));
    }

    @GetMapping("/booking/{bookingId}/bill")
    public ResponseEntity<MonthlyBillCalculationResult> getMonthlyBill(
            @PathVariable String bookingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.calculateMonthlyBill(bookingId, from, to));
    }
}
