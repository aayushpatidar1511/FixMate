package com.fixmate.repository;

import com.fixmate.model.Review;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Review> reviewRowMapper = (rs, rowNum) -> {
        Review r = new Review();
        r.setReviewId(rs.getLong("review_id"));
        r.setBookingId(rs.getLong("booking_id"));
        r.setCustomerId(rs.getLong("customer_id"));
        r.setProviderId(rs.getLong("provider_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        try {
            r.setCustomerName(rs.getString("customer_name"));
            r.setServiceName(rs.getString("service_name"));
        } catch (Exception ignored) {}
        return r;
    };

    public Optional<Review> findByBookingId(Long bookingId) {
        String sql = "SELECT r.*, u.full_name AS customer_name, s.service_name FROM reviews r " +
                     "JOIN customers c ON r.customer_id = c.customer_id " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "JOIN bookings b ON r.booking_id = b.booking_id " +
                     "JOIN services s ON b.service_id = s.service_id " +
                     "WHERE r.booking_id = ?";
        try {
            Review review = jdbcTemplate.queryForObject(sql, reviewRowMapper, bookingId);
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Review> findByProviderId(Long providerId) {
        String sql = "SELECT r.*, u.full_name AS customer_name, s.service_name FROM reviews r " +
                     "JOIN customers c ON r.customer_id = c.customer_id " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "JOIN bookings b ON r.booking_id = b.booking_id " +
                     "JOIN services s ON b.service_id = s.service_id " +
                     "WHERE r.provider_id = ? ORDER BY r.created_at DESC";
        return jdbcTemplate.query(sql, reviewRowMapper, providerId);
    }

    public Long save(Review review) {
        String sql = "INSERT INTO reviews (booking_id, customer_id, provider_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, review.getBookingId());
            ps.setLong(2, review.getCustomerId());
            ps.setLong(3, review.getProviderId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to save review");
    }

    public boolean existsByBookingId(Long bookingId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE booking_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, bookingId);
        return count != null && count > 0;
    }
}
