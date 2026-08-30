package com.fixmate.controller;

import com.fixmate.dto.request.AddressRequest;
import com.fixmate.dto.response.ApiResponse;
import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Address;
import com.fixmate.model.Customer;
import com.fixmate.repository.AddressRepository;
import com.fixmate.repository.CustomerRepository;
import com.fixmate.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/addresses")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerAddressController {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public CustomerAddressController(AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Address>>> getMyAddresses() {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return ResponseEntity.ok(ApiResponse.ok(addressRepository.findByCustomerId(customer.getCustomerId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addAddress(@Valid @RequestBody AddressRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Address address = new Address();
        address.setCustomerId(customer.getCustomerId());
        address.setLabel(request.getLabel());
        address.setStreetAddress(request.getStreetAddress());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setIsDefault(request.getIsDefault());

        Long id = addressRepository.save(address);
        return ResponseEntity.ok(ApiResponse.ok("Address saved successfully", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        addressRepository.delete(id, customer.getCustomerId());
        return ResponseEntity.ok(ApiResponse.ok("Address removed", null));
    }
}
