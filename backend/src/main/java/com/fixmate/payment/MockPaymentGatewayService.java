package com.fixmate.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service("mockPaymentGatewayService")
public class MockPaymentGatewayService implements PaymentGatewayService {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGatewayService.class);

    @Override
    public String getGatewayType() {
        return "MOCK";
    }

    @Override
    public PaymentOrderResponse createOrder(Long bookingId, BigDecimal amount, String currency) {
        String mockOrderId = "order_mock_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        log.info("[MOCK PAYMENT GATEWAY] Generated test order {} for booking #{} of amount {} {}", 
                mockOrderId, bookingId, amount, currency);

        return new PaymentOrderResponse(
            mockOrderId,
            "MOCK",
            amount,
            currency,
            "mock_key_fixmate_sandbox",
            "CREATED"
        );
    }

    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        log.info("[MOCK PAYMENT GATEWAY] Verifying mock payment: orderId={}, paymentId={}", orderId, paymentId);
        // In mock mode, any non-blank paymentId that doesn't explicitly start with 'FAIL' is accepted
        return paymentId != null && !paymentId.trim().isEmpty() && !paymentId.toUpperCase().startsWith("FAIL");
    }

    @Override
    public boolean processRefund(String paymentId, BigDecimal amount, String reason) {
        log.info("[MOCK PAYMENT GATEWAY] Processed simulated refund for payment {} of amount {} (Reason: {})", 
                paymentId, amount, reason);
        return true;
    }
}
