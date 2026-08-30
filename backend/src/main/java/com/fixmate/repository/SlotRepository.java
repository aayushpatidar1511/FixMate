package com.fixmate.repository;

import com.fixmate.model.ProviderSlot;
import com.fixmate.model.Slot;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class SlotRepository {

    private final JdbcTemplate jdbcTemplate;

    public SlotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Slot> slotRowMapper = (rs, rowNum) -> {
        Slot s = new Slot();
        s.setSlotId(rs.getLong("slot_id"));
        s.setSlotName(rs.getString("slot_name"));
        s.setStartTime(rs.getTime("start_time").toLocalTime());
        s.setEndTime(rs.getTime("end_time").toLocalTime());
        return s;
    };

    private final RowMapper<ProviderSlot> providerSlotRowMapper = (rs, rowNum) -> {
        ProviderSlot ps = new ProviderSlot();
        ps.setProviderSlotId(rs.getLong("provider_slot_id"));
        ps.setProviderId(rs.getLong("provider_id"));
        ps.setDayOfWeek(rs.getInt("day_of_week"));
        ps.setSlotId(rs.getLong("slot_id"));
        ps.setIsActive(rs.getBoolean("is_active"));
        try {
            ps.setSlotName(rs.getString("slot_name"));
            ps.setStartTime(rs.getTime("start_time").toLocalTime());
            ps.setEndTime(rs.getTime("end_time").toLocalTime());
        } catch (Exception ignored) {}
        return ps;
    };

    public List<Slot> findAll() {
        String sql = "SELECT * FROM slots ORDER BY start_time ASC";
        return jdbcTemplate.query(sql, slotRowMapper);
    }

    public Optional<Slot> findById(Long slotId) {
        String sql = "SELECT * FROM slots WHERE slot_id = ?";
        try {
            Slot slot = jdbcTemplate.queryForObject(sql, slotRowMapper, slotId);
            return Optional.ofNullable(slot);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ProviderSlot> findByProviderId(Long providerId) {
        String sql = "SELECT ps.*, s.slot_name, s.start_time, s.end_time FROM provider_slots ps " +
                     "JOIN slots s ON ps.slot_id = s.slot_id " +
                     "WHERE ps.provider_id = ? ORDER BY ps.day_of_week ASC, s.start_time ASC";
        return jdbcTemplate.query(sql, providerSlotRowMapper, providerId);
    }

    public List<Slot> findAvailableSlotsForProviderOnDate(Long providerId, LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Mon, 7=Sun

        // Slot must be active in provider's schedule AND NOT booked in bookings table on that date
        String sql = "SELECT s.* FROM slots s " +
                     "JOIN provider_slots ps ON s.slot_id = ps.slot_id " +
                     "WHERE ps.provider_id = ? " +
                     "  AND ps.day_of_week = ? " +
                     "  AND ps.is_active = TRUE " +
                     "  AND s.slot_id NOT IN (" +
                     "      SELECT b.slot_id FROM bookings b " +
                     "      WHERE b.provider_id = ? " +
                     "        AND b.booking_date = ? " +
                     "        AND b.booking_status NOT IN ('CANCELLED', 'REJECTED')" +
                     "  ) " +
                     "ORDER BY s.start_time ASC";

        return jdbcTemplate.query(sql, slotRowMapper, providerId, dayOfWeek, providerId, java.sql.Date.valueOf(date));
    }

    public void updateProviderSlotsForDay(Long providerId, int dayOfWeek, List<Long> activeSlotIds) {
        // Reset all for that day
        String deactivateSql = "UPDATE provider_slots SET is_active = FALSE WHERE provider_id = ? AND day_of_week = ?";
        jdbcTemplate.update(deactivateSql, providerId, dayOfWeek);

        if (activeSlotIds != null && !activeSlotIds.isEmpty()) {
            for (Long slotId : activeSlotIds) {
                String upsertSql = "INSERT INTO provider_slots (provider_id, day_of_week, slot_id, is_active) " +
                                   "VALUES (?, ?, ?, TRUE) " +
                                   "ON DUPLICATE KEY UPDATE is_active = TRUE";
                jdbcTemplate.update(upsertSql, providerId, dayOfWeek, slotId);
            }
        }
    }
}
