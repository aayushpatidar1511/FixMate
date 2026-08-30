package com.fixmate.repository;

import com.fixmate.model.Address;
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
public class AddressRepository {

    private final JdbcTemplate jdbcTemplate;

    public AddressRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Address> addressRowMapper = (rs, rowNum) -> {
        Address a = new Address();
        a.setAddressId(rs.getLong("address_id"));
        a.setCustomerId(rs.getLong("customer_id"));
        a.setLabel(rs.getString("label"));
        a.setStreetAddress(rs.getString("street_address"));
        a.setLandmark(rs.getString("landmark"));
        a.setCity(rs.getString("city"));
        a.setState(rs.getString("state"));
        a.setPincode(rs.getString("pincode"));
        a.setLatitude(rs.getBigDecimal("latitude"));
        a.setLongitude(rs.getBigDecimal("longitude"));
        a.setIsDefault(rs.getBoolean("is_default"));
        a.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return a;
    };

    public Optional<Address> findById(Long addressId) {
        String sql = "SELECT * FROM addresses WHERE address_id = ?";
        try {
            Address address = jdbcTemplate.queryForObject(sql, addressRowMapper, addressId);
            return Optional.ofNullable(address);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Address> findByCustomerId(Long customerId) {
        String sql = "SELECT * FROM addresses WHERE customer_id = ? ORDER BY is_default DESC, created_at DESC";
        return jdbcTemplate.query(sql, addressRowMapper, customerId);
    }

    public Long save(Address address) {
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            jdbcTemplate.update("UPDATE addresses SET is_default = FALSE WHERE customer_id = ?", address.getCustomerId());
        }

        String sql = "INSERT INTO addresses (customer_id, label, street_address, landmark, city, state, pincode, latitude, longitude, is_default) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, address.getCustomerId());
            ps.setString(2, address.getLabel() != null ? address.getLabel() : "Home");
            ps.setString(3, address.getStreetAddress());
            ps.setString(4, address.getLandmark());
            ps.setString(5, address.getCity());
            ps.setString(6, address.getState());
            ps.setString(7, address.getPincode());
            ps.setBigDecimal(8, address.getLatitude());
            ps.setBigDecimal(9, address.getLongitude());
            ps.setBoolean(10, address.getIsDefault() != null && address.getIsDefault());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to retrieve generated address_id");
    }

    public void delete(Long addressId, Long customerId) {
        String sql = "DELETE FROM addresses WHERE address_id = ? AND customer_id = ?";
        jdbcTemplate.update(sql, addressId, customerId);
    }
}
