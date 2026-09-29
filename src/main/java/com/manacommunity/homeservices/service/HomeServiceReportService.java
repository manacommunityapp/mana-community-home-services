package com.manacommunity.homeservices.service;

import com.manacommunity.homeservices.dto.HomeServiceReportRequest;
import com.manacommunity.homeservices.model.entity.HomeServiceReportEntity;
import com.manacommunity.homeservices.model.enums.HomeServiceReportStatus;
import com.manacommunity.homeservices.repository.HomeServiceReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("homeServiceReportService")
@RequiredArgsConstructor
public class HomeServiceReportService {
    private final HomeServiceReportRepository reportRepository;

    @Transactional
    public HomeServiceReportEntity createReport(HomeServiceReportRequest req) {
        HomeServiceReportEntity report = HomeServiceReportEntity.builder()
                .id(UUID.randomUUID().toString())
                .communityId(req.getCommunityId())
                .workerId(req.getWorkerId())
                .workerName(req.getWorkerName())
                .reporterUserId(req.getReporterUserId())
                .reporterName(req.getReporterName())
                .flatNumber(req.getFlatNumber())
                .category(req.getCategory())
                .severity(req.getSeverity())
                .description(req.getDescription())
                .status(HomeServiceReportStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return reportRepository.save(report);
    }

    public List<HomeServiceReportEntity> getCommunityReports(String communityId) {
        return reportRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
    }
}
