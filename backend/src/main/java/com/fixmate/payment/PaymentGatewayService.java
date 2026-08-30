package com.fixmate.payment;

import java.math.BigDecimal;

public interface PaymentGatewayService {
    String getGatewayType();
    PaymentOrderResponse createOrder(Long bookingId, BigDecimal amount, String currency);
    boolean verifyPaymentSignature(String orderId, String paymentId, String signature);
    boolean processRefund(String paymentId, BigDecimal amount, String reason);
}
