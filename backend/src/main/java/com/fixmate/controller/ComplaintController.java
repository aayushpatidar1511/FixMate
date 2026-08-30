package com.fixmate.controller;

import com.fixmate.dto.request.ComplaintRequest;
import com.fixmate.dto.response.ApiResponse;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Complaint;
import com.fixmate.model.Customer;
import com.fixmate.repository.CustomerRepository;
import com.fixmate.service.ComplaintService;
import com.fixmate.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final CustomerRepository customerRepository;

    public ComplaintController(ComplaintService complaintService, CustomerRepository customerRepository) {
        this.complaintService = complaintService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/complaints")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Long>> fileComplaint(@Valid @RequestBody ComplaintRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        Long complaintId = complaintService.fileComplaint(request, customer.getCustomerId());
        return ResponseEntity.ok(ApiResponse.ok("Complaint registered successfully. Our team will review within 24 hours.", complaintId));
    }

    @GetMapping("/customer/complaints")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<Complaint>>> getMyComplaints() {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        return ResponseEntity.ok(ApiResponse.ok(complaintService.getCustomerComplaints(customer.getCustomerId())));
    }

    @GetMapping("/admin/complaints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Complaint>>> getAllComplaints() {
        return ResponseEntity.ok(ApiResponse.ok(complaintService.getAllComplaints()));
    }

    @PatchMapping("/admin/complaints/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String remarks = body.get("remarks");
        complaintService.updateComplaintStatus(id, status, remarks);
        return ResponseEntity.ok(ApiResponse.ok("Complaint updated successfully", null));
    }
}
