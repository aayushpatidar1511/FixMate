package com.fixmate.controller;

import com.fixmate.dto.request.SlotConfigDto;
import com.fixmate.dto.response.ApiResponse;
import com.fixmate.dto.response.BookingSummaryResponse;
import com.fixmate.dto.response.ProviderCardResponse;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Provider;
import com.fixmate.model.Review;
import com.fixmate.model.Slot;
import com.fixmate.repository.ProviderRepository;
import com.fixmate.security.UserPrincipal;
import com.fixmate.service.BookingService;
import com.fixmate.service.ProviderService;
import com.fixmate.service.ReviewService;
import com.fixmate.util.SecurityUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProviderController {

    private final ProviderService providerService;
    private final ProviderRepository providerRepository;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    public ProviderController(ProviderService providerService,
                              ProviderRepository providerRepository,
                              BookingService bookingService,
                              ReviewService reviewService) {
        this.providerService = providerService;
        this.providerRepository = providerRepository;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
    }

    @GetMapping("/providers/search")
    public ResponseEntity<ApiResponse<List<ProviderCardResponse>>> searchProviders(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false, defaultValue = "15.0") Double radiusKm) {
        List<ProviderCardResponse> results = providerService.searchProviders(city, categoryId, serviceId, lat, lon, radiusKm);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/providers/nearby")
    public ResponseEntity<ApiResponse<List<ProviderCardResponse>>> getNearbyProviders(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(required = false, defaultValue = "20.0") Double radius,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) String city) {
        List<ProviderCardResponse> results = providerService.searchProviders(city, null, serviceId, lat, lon, radius);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<ApiResponse<ProviderCardResponse>> getProviderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(providerService.getProviderCard(id)));
    }

    @GetMapping("/providers/{id}/availability")
    public ResponseEntity<ApiResponse<List<Slot>>> getProviderAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Slot> slots = providerService.getAvailableSlots(id, date);
        return ResponseEntity.ok(ApiResponse.ok(slots));
    }

    @GetMapping("/providers/{id}/reviews")
    public ResponseEntity<ApiResponse<List<Review>>> getProviderReviews(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getProviderReviews(id)));
    }

    // --- Provider Portal Protected Endpoints ---

    @GetMapping("/provider/me")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProviderCardResponse>> getMyProviderProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider p = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found for user"));
        return ResponseEntity.ok(ApiResponse.ok(providerService.getProviderCard(p.getProviderId())));
    }

    @GetMapping("/provider/bookings")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<BookingSummaryResponse>>> getMyBookings() {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider p = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getProviderBookings(p.getProviderId())));
    }

    @GetMapping("/provider/earnings")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyEarnings() {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider p = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
        return ResponseEntity.ok(ApiResponse.ok(providerService.getEarningsAndLedger(p.getProviderId())));
    }

    @PutMapping("/provider/schedule")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> updateSchedule(@RequestBody SlotConfigDto dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider p = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
        providerService.updateSchedule(p.getProviderId(), dto);
        return ResponseEntity.ok(ApiResponse.ok("Schedule updated successfully", null));
    }

    @PutMapping("/provider/services/{serviceId}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> updateServicePrice(
            @PathVariable Long serviceId,
            @RequestParam BigDecimal customPrice) {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider p = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
        providerService.setCustomPrice(p.getProviderId(), serviceId, customPrice);
        return ResponseEntity.ok(ApiResponse.ok("Custom service price updated", null));
    }
}
