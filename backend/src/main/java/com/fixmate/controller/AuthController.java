package com.fixmate.controller;

import com.fixmate.dto.request.LoginRequest;
import com.fixmate.dto.request.ProviderRegistrationRequest;
import com.fixmate.dto.request.UserRegistrationRequest;
import com.fixmate.dto.response.ApiResponse;
import com.fixmate.dto.response.AuthResponse;
import com.fixmate.model.Customer;
import com.fixmate.model.Provider;
import com.fixmate.model.User;
import com.fixmate.repository.CustomerRepository;
import com.fixmate.repository.ProviderRepository;
import com.fixmate.repository.UserRepository;
import com.fixmate.security.UserPrincipal;
import com.fixmate.service.AuthService;
import com.fixmate.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          CustomerRepository customerRepository,
                          ProviderRepository providerRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
    }

    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCustomer(@Valid @RequestBody UserRegistrationRequest request) {
        AuthResponse response = authService.registerCustomer(request);
        return ResponseEntity.ok(ApiResponse.ok("Customer registered successfully", response));
    }

    @PostMapping("/register/provider")
    public ResponseEntity<ApiResponse<AuthResponse>> registerProvider(@Valid @RequestBody ProviderRegistrationRequest request) {
        AuthResponse response = authService.registerProvider(request);
        return ResponseEntity.ok(ApiResponse.ok("Service professional registered successfully. Application under verification.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        UserPrincipal principal = SecurityUtils.getCurrentUser();
        User user = userRepository.findById(principal.getUserId()).orElseThrow();

        Map<String, Object> details = new HashMap<>();
        details.put("userId", user.getUserId());
        details.put("fullName", user.getFullName());
        details.put("email", user.getEmail());
        details.put("phone", user.getPhone());
        details.put("role", user.getRole());
        details.put("status", user.getStatus());

        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            customerRepository.findByUserId(user.getUserId()).ifPresent(c -> {
                details.put("customerId", c.getCustomerId());
                details.put("profileImage", c.getProfileImage());
                details.put("totalBookings", c.getTotalBookings());
            });
        } else if ("PROVIDER".equalsIgnoreCase(user.getRole())) {
            providerRepository.findByUserId(user.getUserId()).ifPresent(p -> {
                details.put("providerId", p.getProviderId());
                details.put("city", p.getCity());
                details.put("verificationStatus", p.getVerificationStatus());
                details.put("ratingAvg", p.getRatingAvg());
                details.put("ratingCount", p.getRatingCount());
                details.put("walletBalance", p.getWalletBalance());
            });
        }

        return ResponseEntity.ok(ApiResponse.ok(details));
    }
}
