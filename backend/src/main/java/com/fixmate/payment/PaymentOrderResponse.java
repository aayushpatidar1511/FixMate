package com.fixmate.payment;

import java.math.BigDecimal;

public class PaymentOrderResponse {
    private String orderId;
    private String gateway; // 'MOCK' or 'RAZORPAY'
    private BigDecimal amount;
    private String currency;
    private String keyId; // Razorpay public key ID if live, or mock identifier
    private String status;

    public PaymentOrderResponse() {}

    public PaymentOrderResponse(String orderId, String gateway, BigDecimal amount, String currency, String keyId, String status) {
        this.orderId = orderId;
        this.gateway = gateway;
        this.amount = amount;
        this.currency = currency;
        this.keyId = keyId;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getGateway() { return gateway; }
    public void setGateway(String gateway) { this.gateway = gateway; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
