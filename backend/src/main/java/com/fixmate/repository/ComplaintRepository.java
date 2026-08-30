package com.fixmate.repository;

import com.fixmate.model.Complaint;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ComplaintRepository {

    private final JdbcTemplate jdbcTemplate;

    public ComplaintRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Complaint> complaintRowMapper = (rs, rowNum) -> {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getLong("complaint_id"));
        c.setComplaintNumber(rs.getString("complaint_number"));
        c.setBookingId(rs.getLong("booking_id"));
        c.setCustomerId(rs.getLong("customer_id"));
        c.setSubject(rs.getString("subject"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        c.setAdminRemarks(rs.getString("admin_remarks"));
        c.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        c.setResolvedAt(rs.getTimestamp("resolved_at") != null ? rs.getTimestamp("resolved_at").toLocalDateTime() : null);
        try {
            c.setCustomerName(rs.getString("customer_name"));
            c.setCustomerPhone(rs.getString("customer_phone"));
            c.setBookingNumber(rs.getString("booking_number"));
        } catch (Exception ignored) {}
        return c;
    };

    public Optional<Complaint> findById(Long complaintId) {
        String sql = "SELECT cp.*, u.full_name AS customer_name, u.phone AS customer_phone, b.booking_number " +
                     "FROM complaints cp " +
                     "JOIN customers c ON cp.customer_id = c.customer_id " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "JOIN bookings b ON cp.booking_id = b.booking_id " +
                     "WHERE cp.complaint_id = ?";
        try {
            Complaint complaint = jdbcTemplate.queryForObject(sql, complaintRowMapper, complaintId);
            return Optional.ofNullable(complaint);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Complaint> findByCustomerId(Long customerId) {
        String sql = "SELECT cp.*, u.full_name AS customer_name, u.phone AS customer_phone, b.booking_number " +
                     "FROM complaints cp " +
                     "JOIN customers c ON cp.customer_id = c.customer_id " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "JOIN bookings b ON cp.booking_id = b.booking_id " +
                     "WHERE cp.customer_id = ? ORDER BY cp.created_at DESC";
        return jdbcTemplate.query(sql, complaintRowMapper, customerId);
    }

    public List<Complaint> findAll() {
        String sql = "SELECT cp.*, u.full_name AS customer_name, u.phone AS customer_phone, b.booking_number " +
                     "FROM complaints cp " +
                     "JOIN customers c ON cp.customer_id = c.customer_id " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "JOIN bookings b ON cp.booking_id = b.booking_id " +
                     "ORDER BY cp.created_at DESC";
        return jdbcTemplate.query(sql, complaintRowMapper);
    }

    public Long save(Complaint complaint) {
        String sql = "INSERT INTO complaints (complaint_number, booking_id, customer_id, subject, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, complaint.getComplaintNumber());
            ps.setLong(2, complaint.getBookingId());
            ps.setLong(3, complaint.getCustomerId());
            ps.setString(4, complaint.getSubject());
            ps.setString(5, complaint.getDescription());
            ps.setString(6, complaint.getStatus() != null ? complaint.getStatus() : "OPEN");
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to save complaint");
    }

    public void updateStatus(Long complaintId, String status, String adminRemarks) {
        String sql = "UPDATE complaints SET status = ?, admin_remarks = ?, resolved_at = ? WHERE complaint_id = ?";
        LocalDateTime now = "RESOLVED".equalsIgnoreCase(status) ? LocalDateTime.now() : null;
        jdbcTemplate.update(sql, status, adminRemarks, now != null ? Timestamp.valueOf(now) : null, complaintId);
    }

    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status);
        return count != null ? count : 0L;
    }
}
