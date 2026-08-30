package com.fixmate.payment;

import com.fixmate.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.HexFormat;
import java.util.UUID;

@Service("razorpayPaymentGatewayService")
public class RazorpayPaymentGatewayService implements PaymentGatewayService {

    private static final Logger log = LoggerFactory.getLogger(RazorpayPaymentGatewayService.class);
    private final AppProperties appProperties;

    public RazorpayPaymentGatewayService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public String getGatewayType() {
        return "RAZORPAY";
    }

    @Override
    public PaymentOrderResponse createOrder(Long bookingId, BigDecimal amount, String currency) {
        String keyId = appProperties.getPayment().getRazorpayKeyId();
        String orderId = "order_rzp_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        log.info("[RAZORPAY GATEWAY] Created order {} for booking #{} with amount {}", orderId, bookingId, amount);

        return new PaymentOrderResponse(
            orderId,
            "RAZORPAY",
            amount,
            currency != null ? currency : "INR",
            keyId != null && !keyId.isEmpty() ? keyId : "rzp_test_FixMateDefaultKey",
            "CREATED"
        );
    }

    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        String secret = appProperties.getPayment().getRazorpayKeySecret();
        if (secret == null || secret.isEmpty()) {
            log.warn("[RAZORPAY GATEWAY] No secret key configured, accepting test signature");
            return true;
        }

        try {
            String payload = orderId + "|" + paymentId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = HexFormat.of().formatHex(hash);

            return generatedSignature.equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("[RAZORPAY GATEWAY] Signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean processRefund(String paymentId, BigDecimal amount, String reason) {
        log.info("[RAZORPAY GATEWAY] Refund initiated for payment {}: amount={} INR, reason={}", paymentId, amount, reason);
        return true;
    }
}
