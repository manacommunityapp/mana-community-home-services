package com.manacommunity.homeservices.controller;

import com.manacommunity.homeservices.dto.HomeServiceReportRequest;
import com.manacommunity.homeservices.model.entity.HomeServiceReportEntity;
import com.manacommunity.homeservices.service.HomeServiceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController("homeServiceReportController")
@RequestMapping("/api/v1/home-services/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeServiceReportController {
    private final HomeServiceReportService reportService;

    @PostMapping
    public ResponseEntity<HomeServiceReportEntity> createReport(@RequestBody HomeServiceReportRequest req) {
        return ResponseEntity.ok(reportService.createReport(req));
    }

    @GetMapping
    public ResponseEntity<List<HomeServiceReportEntity>> getReports(@RequestParam(defaultValue = "comm-mana-1") String communityId) {
        return ResponseEntity.ok(reportService.getCommunityReports(communityId));
    }
}
