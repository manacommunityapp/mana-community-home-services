package com.manacommunity.homeservices.controller;

import com.manacommunity.homeservices.model.entity.HomeServiceCategoryEntity;
import com.manacommunity.homeservices.repository.HomeServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController("homeServiceCategoryController")
@RequestMapping("/api/v1/home-services/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeServiceCategoryController {
    private final HomeServiceCategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<HomeServiceCategoryEntity>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findByActiveTrueOrderByDisplayOrderAsc());
    }
}
