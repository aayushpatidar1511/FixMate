package com.fixmate.repository;

import com.fixmate.model.Provider;
import com.fixmate.model.ProviderServiceItem;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProviderRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProviderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Provider> providerRowMapper = (rs, rowNum) -> {
        Provider p = new Provider();
        p.setProviderId(rs.getLong("provider_id"));
        p.setUserId(rs.getLong("user_id"));
        p.setBio(rs.getString("bio"));
        p.setExperienceYears(rs.getInt("experience_years"));
        p.setAddress(rs.getString("address"));
        p.setCity(rs.getString("city"));
        p.setState(rs.getString("state"));
        p.setPincode(rs.getString("pincode"));
        p.setLatitude(rs.getBigDecimal("latitude"));
        p.setLongitude(rs.getBigDecimal("longitude"));
        p.setVerificationStatus(rs.getString("verification_status"));
        p.setIdProofType(rs.getString("id_proof_type"));
        p.setIdProofNumber(rs.getString("id_proof_number"));
        p.setRatingAvg(rs.getBigDecimal("rating_avg"));
        p.setRatingCount(rs.getInt("rating_count"));
        p.setTotalCompletedJobs(rs.getInt("total_completed_jobs"));
        p.setWalletBalance(rs.getBigDecimal("wallet_balance"));
        p.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        p.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);

        try {
            p.setFullName(rs.getString("full_name"));
            p.setEmail(rs.getString("email"));
            p.setPhone(rs.getString("phone"));
        } catch (Exception ignored) {}

        try {
            p.setDistanceKm(rs.getDouble("distance_km"));
        } catch (Exception ignored) {}

        return p;
    };

    private final RowMapper<ProviderServiceItem> providerServiceItemRowMapper = (rs, rowNum) -> {
        ProviderServiceItem item = new ProviderServiceItem();
        item.setProviderId(rs.getLong("provider_id"));
        item.setServiceId(rs.getLong("service_id"));
        item.setCustomPrice(rs.getBigDecimal("custom_price"));
        item.setIsAvailable(rs.getBoolean("is_available"));
        try {
            item.setServiceName(rs.getString("service_name"));
            item.setCategoryName(rs.getString("category_name"));
            item.setBasePrice(rs.getBigDecimal("base_price"));
            item.setDurationMinutes(rs.getInt("duration_minutes"));
        } catch (Exception ignored) {}
        return item;
    };

    public Optional<Provider> findById(Long providerId) {
        String sql = "SELECT p.*, u.full_name, u.email, u.phone FROM service_providers p " +
                     "JOIN users u ON p.user_id = u.user_id WHERE p.provider_id = ?";
        try {
            Provider provider = jdbcTemplate.queryForObject(sql, providerRowMapper, providerId);
            return Optional.ofNullable(provider);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Provider> findByUserId(Long userId) {
        String sql = "SELECT p.*, u.full_name, u.email, u.phone FROM service_providers p " +
                     "JOIN users u ON p.user_id = u.user_id WHERE p.user_id = ?";
        try {
            Provider provider = jdbcTemplate.queryForObject(sql, providerRowMapper, userId);
            return Optional.ofNullable(provider);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Provider> findAllActive(String city, Long categoryId, Long serviceId) {
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT p.*, u.full_name, u.email, u.phone FROM service_providers p " +
            "JOIN users u ON p.user_id = u.user_id " +
            "JOIN provider_services ps ON p.provider_id = ps.provider_id " +
            "JOIN services s ON ps.service_id = s.service_id " +
            "WHERE p.verification_status = 'ACTIVE' "
        );

        List<Object> params = new ArrayList<>();

        if (city != null && !city.trim().isEmpty()) {
            sql.append("AND LOWER(p.city) = LOWER(?) ");
            params.add(city.trim());
        }
        if (serviceId != null) {
            sql.append("AND s.service_id = ? ");
            params.add(serviceId);
        } else if (categoryId != null) {
            sql.append("AND s.category_id = ? ");
            params.add(categoryId);
        }

        sql.append("ORDER BY p.rating_avg DESC, p.total_completed_jobs DESC");
        return jdbcTemplate.query(sql.toString(), providerRowMapper, params.toArray());
    }

    public List<Provider> findNearby(Double lat, Double lon, Double radiusKm, Long serviceId, String city) {
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT p.*, u.full_name, u.email, u.phone, " +
            "fn_haversine_distance_km(?, ?, p.latitude, p.longitude) AS distance_km " +
            "FROM service_providers p " +
            "JOIN users u ON p.user_id = u.user_id " +
            "LEFT JOIN provider_services ps ON p.provider_id = ps.provider_id " +
            "WHERE p.verification_status = 'ACTIVE' "
        );

        List<Object> params = new ArrayList<>();
        params.add(lat);
        params.add(lon);

        if (serviceId != null) {
            sql.append("AND ps.service_id = ? ");
            params.add(serviceId);
        }
        if (city != null && !city.trim().isEmpty()) {
            sql.append("AND LOWER(p.city) = LOWER(?) ");
            params.add(city.trim());
        }

        if (radiusKm != null && radiusKm > 0) {
            sql.append("HAVING distance_km <= ? ");
            params.add(radiusKm);
        }

        sql.append("ORDER BY distance_km ASC, p.rating_avg DESC");
        return jdbcTemplate.query(sql.toString(), providerRowMapper, params.toArray());
    }

    public Long save(Provider provider) {
        String sql = "INSERT INTO service_providers (user_id, bio, experience_years, address, city, state, pincode, " +
                     "latitude, longitude, verification_status, id_proof_type, id_proof_number, rating_avg, " +
                     "rating_count, total_completed_jobs, wallet_balance) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, provider.getUserId());
            ps.setString(2, provider.getBio());
            ps.setInt(3, provider.getExperienceYears() != null ? provider.getExperienceYears() : 1);
            ps.setString(4, provider.getAddress());
            ps.setString(5, provider.getCity());
            ps.setString(6, provider.getState());
            ps.setString(7, provider.getPincode());
            ps.setBigDecimal(8, provider.getLatitude() != null ? provider.getLatitude() : BigDecimal.ZERO);
            ps.setBigDecimal(9, provider.getLongitude() != null ? provider.getLongitude() : BigDecimal.ZERO);
            ps.setString(10, provider.getVerificationStatus() != null ? provider.getVerificationStatus() : "PENDING_VERIFICATION");
            ps.setString(11, provider.getIdProofType() != null ? provider.getIdProofType() : "AADHAAR");
            ps.setString(12, provider.getIdProofNumber());
            ps.setBigDecimal(13, BigDecimal.ZERO);
            ps.setInt(14, 0);
            ps.setInt(15, 0);
            ps.setBigDecimal(16, BigDecimal.ZERO);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to retrieve generated provider_id");
    }

    public void updateVerificationStatus(Long providerId, String status) {
        String sql = "UPDATE service_providers SET verification_status = ? WHERE provider_id = ?";
        jdbcTemplate.update(sql, status, providerId);
    }

    public List<ProviderServiceItem> findServicesByProviderId(Long providerId) {
        String sql = "SELECT ps.*, s.service_name, s.base_price, s.duration_minutes, c.name AS category_name " +
                     "FROM provider_services ps " +
                     "JOIN services s ON ps.service_id = s.service_id " +
                     "JOIN categories c ON s.category_id = c.category_id " +
                     "WHERE ps.provider_id = ?";
        return jdbcTemplate.query(sql, providerServiceItemRowMapper, providerId);
    }

    public void addOrUpdateService(Long providerId, Long serviceId, BigDecimal customPrice) {
        String sql = "INSERT INTO provider_services (provider_id, service_id, custom_price, is_available) " +
                     "VALUES (?, ?, ?, TRUE) " +
                     "ON DUPLICATE KEY UPDATE custom_price = VALUES(custom_price), is_available = TRUE";
        jdbcTemplate.update(sql, providerId, serviceId, customPrice);
    }

    public void removeService(Long providerId, Long serviceId) {
        String sql = "DELETE FROM provider_services WHERE provider_id = ? AND service_id = ?";
        jdbcTemplate.update(sql, providerId, serviceId);
    }

    public List<Provider> findAll() {
        String sql = "SELECT p.*, u.full_name, u.email, u.phone FROM service_providers p " +
                     "JOIN users u ON p.user_id = u.user_id ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, providerRowMapper);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM service_providers";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public long countByVerificationStatus(String status) {
        String sql = "SELECT COUNT(*) FROM service_providers WHERE verification_status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status);
        return count != null ? count : 0L;
    }
}
