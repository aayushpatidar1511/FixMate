package com.fixmate.service;

import com.fixmate.dto.request.LoginRequest;
import com.fixmate.dto.request.ProviderRegistrationRequest;
import com.fixmate.dto.request.UserRegistrationRequest;
import com.fixmate.dto.response.AuthResponse;
import com.fixmate.exception.BadRequestException;
import com.fixmate.exception.ConflictException;
import com.fixmate.model.Customer;
import com.fixmate.model.Provider;
import com.fixmate.model.User;
import com.fixmate.repository.CustomerRepository;
import com.fixmate.repository.ProviderRepository;
import com.fixmate.repository.SlotRepository;
import com.fixmate.repository.UserRepository;
import com.fixmate.security.JwtTokenProvider;
import com.fixmate.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final SlotRepository slotRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       ProviderRepository providerRepository,
                       SlotRepository slotRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.slotRepository = slotRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse registerCustomer(UserRegistrationRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("An account with email " + req.getEmail() + " already exists");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new ConflictException("An account with phone " + req.getPhone() + " already exists");
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");

        Long userId = userRepository.save(user);

        Customer customer = new Customer();
        customer.setUserId(userId);
        customer.setTotalBookings(0);
        customer.setProfileImage("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80");

        Long customerId = customerRepository.save(customer);

        String token = tokenProvider.generateTokenFromUserId(userId, user.getEmail(), user.getRole());

        return new AuthResponse(token, userId, customerId, user.getFullName(), user.getEmail(), user.getRole(), user.getStatus());
    }

    @Transactional
    public AuthResponse registerProvider(ProviderRegistrationRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("An account with email " + req.getEmail() + " already exists");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new ConflictException("An account with phone " + req.getPhone() + " already exists");
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("PROVIDER");
        user.setStatus("ACTIVE");

        Long userId = userRepository.save(user);

        Provider provider = new Provider();
        provider.setUserId(userId);
        provider.setBio(req.getBio());
        provider.setExperienceYears(req.getExperienceYears());
        provider.setAddress(req.getAddress());
        provider.setCity(req.getCity());
        provider.setState(req.getState());
        provider.setPincode(req.getPincode());
        provider.setLatitude(req.getLatitude());
        provider.setLongitude(req.getLongitude());
        provider.setVerificationStatus("PENDING_VERIFICATION");
        provider.setIdProofType(req.getIdProofType() != null ? req.getIdProofType() : "AADHAAR");
        provider.setIdProofNumber(req.getIdProofNumber());

        Long providerId = providerRepository.save(provider);

        // Attach selected services with default base prices
        if (req.getServiceIds() != null && !req.getServiceIds().isEmpty()) {
            for (Long sId : req.getServiceIds()) {
                providerRepository.addOrUpdateService(providerId, sId, new BigDecimal("499.00"));
            }
        }

        // Initialize standard Monday-Saturday weekly working hours (Slots 1 to 4)
        List<Long> defaultSlots = List.of(1L, 2L, 3L, 4L);
        for (int day = 1; day <= 6; day++) {
            slotRepository.updateProviderSlotsForDay(providerId, day, defaultSlots);
        }

        String token = tokenProvider.generateTokenFromUserId(userId, user.getEmail(), user.getRole());

        return new AuthResponse(token, userId, providerId, user.getFullName(), user.getEmail(), user.getRole(), provider.getVerificationStatus());
    }

    public AuthResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();
        String role = principal.getRole();
        String status = principal.getStatus();

        Long profileId = null;
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            profileId = customerRepository.findByUserId(userId).map(Customer::getCustomerId).orElse(null);
        } else if ("PROVIDER".equalsIgnoreCase(role)) {
            profileId = providerRepository.findByUserId(userId).map(Provider::getProviderId).orElse(null);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User profile not found"));

        return new AuthResponse(token, userId, profileId, user.getFullName(), user.getEmail(), role, status);
    }
}
