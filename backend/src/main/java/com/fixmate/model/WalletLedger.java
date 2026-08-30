package com.fixmate.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletLedger {
    private Long ledgerId;
    private Long providerId;
    private Long bookingId;
    private String transactionType; // 'CREDIT_BOOKING_PAYOUT', 'DEBIT_COMMISSION', 'DEBIT_WITHDRAWAL', 'CREDIT_ADJUSTMENT'
    private BigDecimal amount;
    private BigDecimal runningBalance;
    private String description;
    private LocalDateTime createdAt;

    public WalletLedger() {}

    public Long getLedgerId() { return ledgerId; }
    public void setLedgerId(Long ledgerId) { this.ledgerId = ledgerId; }
    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getRunningBalance() { return runningBalance; }
    public void setRunningBalance(BigDecimal runningBalance) { this.runningBalance = runningBalance; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
