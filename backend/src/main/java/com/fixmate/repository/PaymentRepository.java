package com.fixmate.repository;

import com.fixmate.model.Payment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Payment> paymentRowMapper = (rs, rowNum) -> {
        Payment p = new Payment();
        p.setPaymentId(rs.getLong("payment_id"));
        p.setBookingId(rs.getLong("booking_id"));
        p.setTransactionReference(rs.getString("transaction_reference"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setPaymentGateway(rs.getString("payment_gateway"));
        p.setGatewayOrderId(rs.getString("gateway_order_id"));
        p.setGatewayPaymentId(rs.getString("gateway_payment_id"));
        p.setGatewaySignature(rs.getString("gateway_signature"));
        p.setAmount(rs.getBigDecimal("amount"));
        p.setCurrency(rs.getString("currency"));
        p.setStatus(rs.getString("status"));
        p.setPaidAt(rs.getTimestamp("paid_at") != null ? rs.getTimestamp("paid_at").toLocalDateTime() : null);
        p.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return p;
    };

    public Optional<Payment> findByBookingId(Long bookingId) {
        String sql = "SELECT * FROM payments WHERE booking_id = ? ORDER BY created_at DESC LIMIT 1";
        try {
            Payment payment = jdbcTemplate.queryForObject(sql, paymentRowMapper, bookingId);
            return Optional.ofNullable(payment);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long save(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, transaction_reference, payment_method, payment_gateway, " +
                     "gateway_order_id, gateway_payment_id, gateway_signature, amount, currency, status, paid_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, payment.getBookingId());
            ps.setString(2, payment.getTransactionReference());
            ps.setString(3, payment.getPaymentMethod());
            ps.setString(4, payment.getPaymentGateway() != null ? payment.getPaymentGateway() : "MOCK");
            ps.setString(5, payment.getGatewayOrderId());
            ps.setString(6, payment.getGatewayPaymentId());
            ps.setString(7, payment.getGatewaySignature());
            ps.setBigDecimal(8, payment.getAmount());
            ps.setString(9, payment.getCurrency() != null ? payment.getCurrency() : "INR");
            ps.setString(10, payment.getStatus() != null ? payment.getStatus() : "PENDING");
            ps.setTimestamp(11, payment.getPaidAt() != null ? Timestamp.valueOf(payment.getPaidAt()) : null);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to save payment record");
    }

    public void updateStatus(Long paymentId, String status, LocalDateTime paidAt) {
        String sql = "UPDATE payments SET status = ?, paid_at = ? WHERE payment_id = ?";
        jdbcTemplate.update(sql, status, paidAt != null ? Timestamp.valueOf(paidAt) : null, paymentId);
    }

    public BigDecimal sumRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0.00) FROM payments WHERE status = 'SUCCESS'";
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class);
        return sum != null ? sum : BigDecimal.ZERO;
    }
}
