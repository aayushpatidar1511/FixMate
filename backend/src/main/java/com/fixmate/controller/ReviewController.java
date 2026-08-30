package com.fixmate.controller;

import com.fixmate.dto.request.ReviewRequest;
import com.fixmate.dto.response.ApiResponse;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Customer;
import com.fixmate.model.Review;
import com.fixmate.repository.CustomerRepository;
import com.fixmate.service.ReviewService;
import com.fixmate.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final CustomerRepository customerRepository;

    public ReviewController(ReviewService reviewService, CustomerRepository customerRepository) {
        this.reviewService = reviewService;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Long>> submitReview(@Valid @RequestBody ReviewRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        Long reviewId = reviewService.submitReview(request, customer.getCustomerId());
        return ResponseEntity.ok(ApiResponse.ok("Review submitted successfully. Thank you for your feedback!", reviewId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<ApiResponse<List<Review>>> getProviderReviews(@PathVariable Long providerId) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getProviderReviews(providerId)));
    }
}
