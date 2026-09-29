package com.manacommunity.homeservices.controller;

import com.manacommunity.homeservices.dto.AdminAnalyticsDto;
import com.manacommunity.homeservices.model.entity.HomeServiceWorkerEntity;
import com.manacommunity.homeservices.model.enums.HomeServiceVerificationStatus;
import com.manacommunity.homeservices.service.HomeServiceAdminService;
import com.manacommunity.homeservices.service.HomeServiceWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("homeServiceAdminController")
@RequestMapping("/api/v1/home-services/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeServiceAdminController {
    private final HomeServiceAdminService adminService;
    private final HomeServiceWorkerService workerService;

    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsDto> getAnalytics(@RequestParam(defaultValue = "comm-mana-1") String communityId) {
        return ResponseEntity.ok(adminService.getAnalytics(communityId));
    }

    @PatchMapping("/workers/{workerId}/verify")
    public ResponseEntity<HomeServiceWorkerEntity> verifyWorker(
            @PathVariable String workerId,
            @RequestParam HomeServiceVerificationStatus status,
            @RequestParam(defaultValue = "true") boolean policeVerified,
            @RequestParam(defaultValue = "true") boolean communityVerified) {
        return ResponseEntity.ok(workerService.updateVerification(workerId, status, policeVerified, communityVerified));
    }
}
