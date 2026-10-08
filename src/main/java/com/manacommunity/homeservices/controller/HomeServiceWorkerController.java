package com.manacommunity.homeservices.controller;

import com.manacommunity.homeservices.model.entity.*;
import com.manacommunity.homeservices.model.enums.HomeServiceVerificationStatus;
import com.manacommunity.homeservices.service.HomeServiceWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController("homeServiceWorkerController")
@RequestMapping("/api/v1/home-services/workers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeServiceWorkerController {
    private final HomeServiceWorkerService workerService;

    @GetMapping
    public ResponseEntity<List<HomeServiceWorkerEntity>> getWorkers(
            @RequestParam(required = false, defaultValue = "comm-mana-1") String communityId,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) HomeServiceVerificationStatus status) {
        return ResponseEntity.ok(workerService.getWorkers(communityId, minRating, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeServiceWorkerEntity> getWorkerById(@PathVariable String id) {
        return ResponseEntity.ok(workerService.getWorkerById(id));
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<List<WorkerServiceSkillEntity>> getSkills(@PathVariable String id) {
        return ResponseEntity.ok(workerService.getWorkerSkills(id));
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<WorkerAvailabilitySlotEntity>> getSlots(@PathVariable String id) {
        return ResponseEntity.ok(workerService.getWorkerSlots(id));
    }

    @GetMapping("/{id}/flats")
    public ResponseEntity<List<WorkerFlatAssignmentEntity>> getFlats(@PathVariable String id) {
        return ResponseEntity.ok(workerService.getWorkerFlats(id));
    }

    @PostMapping
    public ResponseEntity<HomeServiceWorkerEntity> registerWorker(@RequestBody HomeServiceWorkerEntity worker) {
        return ResponseEntity.ok(workerService.registerWorker(worker));
    }

    @PatchMapping("/{id}/verification")
    public ResponseEntity<HomeServiceWorkerEntity> updateVerification(
            @PathVariable String id,
            @RequestParam HomeServiceVerificationStatus status,
            @RequestParam(required = false, defaultValue = "false") boolean policeVerified,
            @RequestParam(required = false, defaultValue = "false") boolean communityVerified) {
        return ResponseEntity.ok(workerService.updateVerification(id, status, policeVerified, communityVerified));
    }
}
