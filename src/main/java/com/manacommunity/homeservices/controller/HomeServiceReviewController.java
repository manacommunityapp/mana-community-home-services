package com.manacommunity.homeservices.controller;

import com.manacommunity.homeservices.dto.HomeServiceReviewRequest;
import com.manacommunity.homeservices.model.entity.HomeServiceReviewEntity;
import com.manacommunity.homeservices.service.HomeServiceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController("homeServiceReviewController")
@RequestMapping("/api/v1/home-services/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeServiceReviewController {
    private final HomeServiceReviewService reviewService;

    @PostMapping
    public ResponseEntity<HomeServiceReviewEntity> submitReview(@RequestBody HomeServiceReviewRequest req) {
        return ResponseEntity.ok(reviewService.submitReview(req));
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<HomeServiceReviewEntity>> getReviews(@PathVariable String workerId) {
        return ResponseEntity.ok(reviewService.getReviewsForWorker(workerId));
    }
}
