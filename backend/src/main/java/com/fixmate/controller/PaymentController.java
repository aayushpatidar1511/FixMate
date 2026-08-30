package com.fixmate.controller;

import com.fixmate.dto.request.PaymentVerificationRequest;
import com.fixmate.dto.response.ApiResponse;
import com.fixmate.payment.PaymentOrderResponse;
import com.fixmate.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> createOrder(@RequestBody Map<String, Long> payload) {
        Long bookingId = payload.get("bookingId");
        PaymentOrderResponse order = paymentService.createPaymentOrder(bookingId);
        return ResponseEntity.ok(ApiResponse.ok("Payment order initiated", order));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyPayment(@Valid @RequestBody PaymentVerificationRequest request) {
        boolean verified = paymentService.verifyPayment(request);
        return ResponseEntity.ok(ApiResponse.ok("Payment successfully verified and confirmed", verified));
    }

    @PostMapping("/mock-success")
    public ResponseEntity<ApiResponse<Boolean>> simulateMockSuccess(@RequestBody Map<String, Object> payload) {
        Long bookingId = Long.valueOf(payload.get("bookingId").toString());
        String method = payload.containsKey("method") ? payload.get("method").toString() : "UPI";
        boolean success = paymentService.simulateMockSuccess(bookingId, method);
        return ResponseEntity.ok(ApiResponse.ok("Mock payment processed successfully", success));
    }
}
