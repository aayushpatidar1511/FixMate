package com.fixmate.repository;

import com.fixmate.model.Customer;
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
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
        Customer c = new Customer();
        c.setCustomerId(rs.getLong("customer_id"));
        c.setUserId(rs.getLong("user_id"));
        c.setProfileImage(rs.getString("profile_image"));
        c.setTotalBookings(rs.getInt("total_bookings"));
        c.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        
        // Joined columns if available
        try {
            c.setFullName(rs.getString("full_name"));
            c.setEmail(rs.getString("email"));
            c.setPhone(rs.getString("phone"));
        } catch (Exception ignored) {}

        return c;
    };

    public Optional<Customer> findById(Long customerId) {
        String sql = "SELECT c.*, u.full_name, u.email, u.phone FROM customers c JOIN users u ON c.user_id = u.user_id WHERE c.customer_id = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, customerId);
            return Optional.ofNullable(customer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findByUserId(Long userId) {
        String sql = "SELECT c.*, u.full_name, u.email, u.phone FROM customers c JOIN users u ON c.user_id = u.user_id WHERE c.user_id = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, userId);
            return Optional.ofNullable(customer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long save(Customer customer) {
        String sql = "INSERT INTO customers (user_id, profile_image, total_bookings) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, customer.getUserId());
            ps.setString(2, customer.getProfileImage());
            ps.setInt(3, customer.getTotalBookings() != null ? customer.getTotalBookings() : 0);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to retrieve generated customer_id");
    }

    public void incrementTotalBookings(Long customerId) {
        String sql = "UPDATE customers SET total_bookings = total_bookings + 1 WHERE customer_id = ?";
        jdbcTemplate.update(sql, customerId);
    }

    public List<Customer> findAll() {
        String sql = "SELECT c.*, u.full_name, u.email, u.phone FROM customers c JOIN users u ON c.user_id = u.user_id ORDER BY c.created_at DESC";
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM customers";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}
