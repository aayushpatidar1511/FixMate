package com.fixmate.controller;

import com.fixmate.dto.request.BookingCreateRequest;
import com.fixmate.dto.response.ApiResponse;
import com.fixmate.dto.response.BookingSummaryResponse;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Customer;
import com.fixmate.model.Provider;
import com.fixmate.repository.CustomerRepository;
import com.fixmate.repository.ProviderRepository;
import com.fixmate.security.UserPrincipal;
import com.fixmate.service.BookingService;
import com.fixmate.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;

    public BookingController(BookingService bookingService,
                             CustomerRepository customerRepository,
                             ProviderRepository providerRepository) {
        this.bookingService = bookingService;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingSummaryResponse>> createBooking(
            @Valid @RequestBody BookingCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        BookingSummaryResponse summary = bookingService.createBooking(request, customer.getCustomerId());
        return ResponseEntity.ok(ApiResponse.ok("Booking created successfully. Pending confirmation.", summary));
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<BookingSummaryResponse>> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getBookingDetails(id)));
    }

    @GetMapping("/customer/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<BookingSummaryResponse>>> getMyCustomerBookings() {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        return ResponseEntity.ok(ApiResponse.ok(bookingService.getCustomerBookings(customer.getCustomerId())));
    }

    @PatchMapping("/bookings/{id}/accept")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> acceptBooking(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        bookingService.acceptBooking(id, provider.getProviderId());
        return ResponseEntity.ok(ApiResponse.ok("Booking confirmed and scheduled", null));
    }

    @PatchMapping("/bookings/{id}/reject")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> rejectBooking(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        String reason = body != null && body.containsKey("reason") ? body.get("reason") : "Provider unavailable";
        bookingService.rejectBooking(id, provider.getProviderId(), reason);
        return ResponseEntity.ok(ApiResponse.ok("Booking request rejected", null));
    }

    @PatchMapping("/bookings/{id}/start-travel")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> startTravel(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        bookingService.startTravel(id, provider.getProviderId());
        return ResponseEntity.ok(ApiResponse.ok("Status updated: On the way to customer", null));
    }

    @PatchMapping("/bookings/{id}/start-service")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> startService(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        bookingService.startService(id, provider.getProviderId());
        return ResponseEntity.ok(ApiResponse.ok("Status updated: Service in progress", null));
    }

    @PatchMapping("/bookings/{id}/complete")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> completeService(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Provider provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        bookingService.completeService(id, provider.getProviderId());
        return ResponseEntity.ok(ApiResponse.ok("Service marked as completed. Earnings credited to your wallet!", null));
    }

    @PatchMapping("/bookings/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        UserPrincipal user = SecurityUtils.getCurrentUser();
        String reason = body != null && body.containsKey("reason") ? body.get("reason") : "Cancelled by user";

        bookingService.cancelBooking(id, user.getUserId(), reason, user.getRole());
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled successfully", null));
    }
}
