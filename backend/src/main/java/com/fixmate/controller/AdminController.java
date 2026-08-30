package com.fixmate.controller;

import com.fixmate.dto.response.ApiResponse;
import com.fixmate.dto.response.BookingSummaryResponse;
import com.fixmate.dto.response.DashboardStatsResponse;
import com.fixmate.model.Customer;
import com.fixmate.model.Provider;
import com.fixmate.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardKPIs() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getDashboardStats()));
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAllCustomers()));
    }

    @GetMapping("/providers")
    public ResponseEntity<ApiResponse<List<Provider>>> getAllProviders() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAllProviders()));
    }

    @PatchMapping("/providers/{id}/verify")
    public ResponseEntity<ApiResponse<Void>> verifyProvider(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status"); // 'ACTIVE', 'REJECTED', 'BLOCKED'
        adminService.updateProviderVerification(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Provider verification status updated to " + status, null));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingSummaryResponse>>> getAllBookings(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAllBookings(status)));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        adminService.updateUserStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("User status updated to " + status, null));
    }
}
