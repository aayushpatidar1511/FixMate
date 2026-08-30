package com.fixmate.repository;

import com.fixmate.model.ServiceEntity;
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
public class ServiceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ServiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ServiceEntity> serviceRowMapper = (rs, rowNum) -> {
        ServiceEntity s = new ServiceEntity();
        s.setServiceId(rs.getLong("service_id"));
        s.setCategoryId(rs.getLong("category_id"));
        s.setServiceName(rs.getString("service_name"));
        s.setSlug(rs.getString("slug"));
        s.setDescription(rs.getString("description"));
        s.setBasePrice(rs.getBigDecimal("base_price"));
        s.setDurationMinutes(rs.getInt("duration_minutes"));
        s.setIsActive(rs.getBoolean("is_active"));
        s.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);

        try {
            s.setCategoryName(rs.getString("category_name"));
        } catch (Exception ignored) {}

        return s;
    };

    public List<ServiceEntity> findAllActive() {
        String sql = "SELECT s.*, c.name AS category_name FROM services s " +
                     "JOIN categories c ON s.category_id = c.category_id " +
                     "WHERE s.is_active = TRUE ORDER BY c.display_order ASC, s.service_name ASC";
        return jdbcTemplate.query(sql, serviceRowMapper);
    }

    public List<ServiceEntity> findByCategoryId(Long categoryId) {
        String sql = "SELECT s.*, c.name AS category_name FROM services s " +
                     "JOIN categories c ON s.category_id = c.category_id " +
                     "WHERE s.category_id = ? AND s.is_active = TRUE ORDER BY s.service_name ASC";
        return jdbcTemplate.query(sql, serviceRowMapper, categoryId);
    }

    public Optional<ServiceEntity> findById(Long serviceId) {
        String sql = "SELECT s.*, c.name AS category_name FROM services s " +
                     "JOIN categories c ON s.category_id = c.category_id " +
                     "WHERE s.service_id = ?";
        try {
            ServiceEntity entity = jdbcTemplate.queryForObject(sql, serviceRowMapper, serviceId);
            return Optional.ofNullable(entity);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long save(ServiceEntity service) {
        String sql = "INSERT INTO services (category_id, service_name, slug, description, base_price, duration_minutes, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, service.getCategoryId());
            ps.setString(2, service.getServiceName());
            ps.setString(3, service.getSlug());
            ps.setString(4, service.getDescription());
            ps.setBigDecimal(5, service.getBasePrice());
            ps.setInt(6, service.getDurationMinutes() != null ? service.getDurationMinutes() : 60);
            ps.setBoolean(7, service.getIsActive() != null ? service.getIsActive() : true);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to save service");
    }

    public void update(ServiceEntity service) {
        String sql = "UPDATE services SET category_id = ?, service_name = ?, slug = ?, description = ?, base_price = ?, duration_minutes = ?, is_active = ? WHERE service_id = ?";
        jdbcTemplate.update(sql, service.getCategoryId(), service.getServiceName(), service.getSlug(),
                service.getDescription(), service.getBasePrice(), service.getDurationMinutes(),
                service.getIsActive(), service.getServiceId());
    }

    public List<ServiceEntity> searchByName(String keyword) {
        String sql = "SELECT s.*, c.name AS category_name FROM services s " +
                     "JOIN categories c ON s.category_id = c.category_id " +
                     "WHERE s.is_active = TRUE AND (LOWER(s.service_name) LIKE ? OR LOWER(s.description) LIKE ?) " +
                     "ORDER BY s.service_name ASC";
        String pattern = "%" + keyword.toLowerCase() + "%";
        return jdbcTemplate.query(sql, serviceRowMapper, pattern, pattern);
    }
}
