package com.fixmate.repository;

import com.fixmate.dto.response.BookingSummaryResponse;
import com.fixmate.model.Booking;
import com.fixmate.model.BookingStatusHistory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Booking> bookingRowMapper = (rs, rowNum) -> {
        Booking b = new Booking();
        b.setBookingId(rs.getLong("booking_id"));
        b.setBookingNumber(rs.getString("booking_number"));
        b.setCustomerId(rs.getLong("customer_id"));
        b.setProviderId(rs.getLong("provider_id"));
        b.setServiceId(rs.getLong("service_id"));
        b.setAddressId(rs.getLong("address_id"));
        b.setBookingDate(rs.getDate("booking_date").toLocalDate());
        b.setSlotId(rs.getLong("slot_id"));
        b.setProblemDescription(rs.getString("problem_description"));
        b.setBaseAmount(rs.getBigDecimal("base_amount"));
        b.setPlatformFee(rs.getBigDecimal("platform_fee"));
        b.setTaxAmount(rs.getBigDecimal("tax_amount"));
        b.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setProviderEarnings(rs.getBigDecimal("provider_earnings"));
        b.setBookingStatus(rs.getString("booking_status"));
        b.setPaymentStatus(rs.getString("payment_status"));
        b.setCancellationReason(rs.getString("cancellation_reason"));
        b.setCancelledBy(rs.getString("cancelled_by"));
        b.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        b.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return b;
    };

    private final RowMapper<BookingSummaryResponse> summaryRowMapper = (rs, rowNum) -> {
        BookingSummaryResponse b = new BookingSummaryResponse();
        b.setBookingId(rs.getLong("booking_id"));
        b.setBookingNumber(rs.getString("booking_number"));
        b.setBookingDate(rs.getDate("booking_date").toLocalDate());
        b.setSlotName(rs.getString("slot_name"));
        b.setBookingStatus(rs.getString("booking_status"));
        b.setPaymentStatus(rs.getString("payment_status"));
        b.setBaseAmount(rs.getBigDecimal("base_amount"));
        b.setPlatformFee(rs.getBigDecimal("platform_fee"));
        b.setTaxAmount(rs.getBigDecimal("tax_amount"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setProblemDescription(rs.getString("problem_description"));
        b.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);

        b.setServiceId(rs.getLong("service_id"));
        b.setServiceName(rs.getString("service_name"));
        b.setCategoryName(rs.getString("category_name"));

        b.setCustomerId(rs.getLong("customer_id"));
        b.setCustomerName(rs.getString("customer_name"));
        b.setCustomerPhone(rs.getString("customer_phone"));

        b.setProviderId(rs.getLong("provider_id"));
        b.setProviderName(rs.getString("provider_name"));
        b.setProviderPhone(rs.getString("provider_phone"));
        b.setProviderRating(rs.getBigDecimal("provider_rating"));

        b.setAddressLabel(rs.getString("address_label"));
        b.setStreetAddress(rs.getString("street_address"));
        b.setLandmark(rs.getString("landmark"));
        b.setCity(rs.getString("city"));
        b.setPincode(rs.getString("pincode"));

        try {
            b.setReviewRating(rs.getObject("review_rating") != null ? rs.getInt("review_rating") : null);
            b.setReviewComment(rs.getString("review_comment"));
        } catch (Exception ignored) {}

        return b;
    };

    private final RowMapper<BookingStatusHistory> historyRowMapper = (rs, rowNum) -> {
        BookingStatusHistory h = new BookingStatusHistory();
        h.setHistoryId(rs.getLong("history_id"));
        h.setBookingId(rs.getLong("booking_id"));
        h.setPreviousStatus(rs.getString("previous_status"));
        h.setNewStatus(rs.getString("new_status"));
        h.setChangedByUserId(rs.getLong("changed_by_user_id"));
        h.setRemarks(rs.getString("remarks"));
        h.setChangedAt(rs.getTimestamp("changed_at").toLocalDateTime());
        try {
            h.setChangedByUserName(rs.getString("user_name"));
        } catch (Exception ignored) {}
        return h;
    };

    private static final String BASE_SUMMARY_SQL = 
        "SELECT b.booking_id, b.booking_number, b.booking_date, b.booking_status, b.payment_status, " +
        "b.base_amount, b.platform_fee, b.tax_amount, b.total_amount, b.problem_description, b.created_at, " +
        "s.service_id, s.service_name, cat.name AS category_name, " +
        "c.customer_id, cu.full_name AS customer_name, cu.phone AS customer_phone, " +
        "p.provider_id, pu.full_name AS provider_name, pu.phone AS provider_phone, p.rating_avg AS provider_rating, " +
        "a.label AS address_label, a.street_address, a.landmark, a.city, a.pincode, " +
        "sl.slot_name, " +
        "r.rating AS review_rating, r.comment AS review_comment " +
        "FROM bookings b " +
        "JOIN customers c ON b.customer_id = c.customer_id " +
        "JOIN users cu ON c.user_id = cu.user_id " +
        "JOIN service_providers p ON b.provider_id = p.provider_id " +
        "JOIN users pu ON p.user_id = pu.user_id " +
        "JOIN services s ON b.service_id = s.service_id " +
        "JOIN categories cat ON s.category_id = cat.category_id " +
        "JOIN addresses a ON b.address_id = a.address_id " +
        "JOIN slots sl ON b.slot_id = sl.slot_id " +
        "LEFT JOIN reviews r ON b.booking_id = r.booking_id ";

    public Optional<Booking> findById(Long bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try {
            Booking booking = jdbcTemplate.queryForObject(sql, bookingRowMapper, bookingId);
            return Optional.ofNullable(booking);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<BookingSummaryResponse> findSummaryById(Long bookingId) {
        String sql = BASE_SUMMARY_SQL + "WHERE b.booking_id = ?";
        try {
            BookingSummaryResponse summary = jdbcTemplate.queryForObject(sql, summaryRowMapper, bookingId);
            return Optional.ofNullable(summary);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<BookingSummaryResponse> findByCustomerId(Long customerId) {
        String sql = BASE_SUMMARY_SQL + "WHERE b.customer_id = ? ORDER BY b.created_at DESC";
        return jdbcTemplate.query(sql, summaryRowMapper, customerId);
    }

    public List<BookingSummaryResponse> findByProviderId(Long providerId) {
        String sql = BASE_SUMMARY_SQL + "WHERE b.provider_id = ? ORDER BY b.booking_date DESC, b.slot_id ASC";
        return jdbcTemplate.query(sql, summaryRowMapper, providerId);
    }

    public List<BookingSummaryResponse> findAll(String status) {
        StringBuilder sql = new StringBuilder(BASE_SUMMARY_SQL);
        List<Object> params = new ArrayList<>();
        if (status != null && !status.trim().isEmpty()) {
            sql.append("WHERE b.booking_status = ? ");
            params.add(status.trim().toUpperCase());
        }
        sql.append("ORDER BY b.created_at DESC");
        return jdbcTemplate.query(sql.toString(), summaryRowMapper, params.toArray());
    }

    public boolean isSlotBooked(Long providerId, LocalDate date, Long slotId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE provider_id = ? AND booking_date = ? AND slot_id = ? AND booking_status NOT IN ('CANCELLED', 'REJECTED')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, providerId, Date.valueOf(date), slotId);
        return count != null && count > 0;
    }

    public Long save(Booking booking) {
        String sql = "INSERT INTO bookings (booking_number, customer_id, provider_id, service_id, address_id, " +
                     "booking_date, slot_id, problem_description, base_amount, platform_fee, tax_amount, " +
                     "discount_amount, total_amount, provider_earnings, booking_status, payment_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, booking.getBookingNumber());
            ps.setLong(2, booking.getCustomerId());
            ps.setLong(3, booking.getProviderId());
            ps.setLong(4, booking.getServiceId());
            ps.setLong(5, booking.getAddressId());
            ps.setDate(6, Date.valueOf(booking.getBookingDate()));
            ps.setLong(7, booking.getSlotId());
            ps.setString(8, booking.getProblemDescription());
            ps.setBigDecimal(9, booking.getBaseAmount());
            ps.setBigDecimal(10, booking.getPlatformFee());
            ps.setBigDecimal(11, booking.getTaxAmount());
            ps.setBigDecimal(12, booking.getDiscountAmount() != null ? booking.getDiscountAmount() : java.math.BigDecimal.ZERO);
            ps.setBigDecimal(13, booking.getTotalAmount());
            ps.setBigDecimal(14, booking.getProviderEarnings());
            ps.setString(15, booking.getBookingStatus() != null ? booking.getBookingStatus() : "PENDING");
            ps.setString(16, booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "PENDING");
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to insert booking");
    }

    public void updateStatus(Long bookingId, String newStatus, String cancellationReason, String cancelledBy) {
        String sql = "UPDATE bookings SET booking_status = ?, cancellation_reason = ?, cancelled_by = ? WHERE booking_id = ?";
        jdbcTemplate.update(sql, newStatus, cancellationReason, cancelledBy, bookingId);
    }

    public void updatePaymentStatus(Long bookingId, String paymentStatus) {
        String sql = "UPDATE bookings SET payment_status = ? WHERE booking_id = ?";
        jdbcTemplate.update(sql, paymentStatus, bookingId);
    }

    public void saveStatusHistory(Long bookingId, String previousStatus, String newStatus, Long userId, String remarks) {
        String sql = "INSERT INTO booking_status_history (booking_id, previous_status, new_status, changed_by_user_id, remarks) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, bookingId, previousStatus, newStatus, userId, remarks);
    }

    public List<BookingStatusHistory> findHistory(Long bookingId) {
        String sql = "SELECT h.*, u.full_name AS user_name FROM booking_status_history h " +
                     "LEFT JOIN users u ON h.changed_by_user_id = u.user_id " +
                     "WHERE h.booking_id = ? ORDER BY h.changed_at ASC";
        return jdbcTemplate.query(sql, historyRowMapper, bookingId);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM bookings";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE booking_status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status);
        return count != null ? count : 0L;
    }
}
