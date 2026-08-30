package com.fixmate.repository;

import com.fixmate.model.WalletLedger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class WalletLedgerRepository {

    private final JdbcTemplate jdbcTemplate;

    public WalletLedgerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<WalletLedger> ledgerRowMapper = (rs, rowNum) -> {
        WalletLedger l = new WalletLedger();
        l.setLedgerId(rs.getLong("ledger_id"));
        l.setProviderId(rs.getLong("provider_id"));
        l.setBookingId(rs.getObject("booking_id") != null ? rs.getLong("booking_id") : null);
        l.setTransactionType(rs.getString("transaction_type"));
        l.setAmount(rs.getBigDecimal("amount"));
        l.setRunningBalance(rs.getBigDecimal("running_balance"));
        l.setDescription(rs.getString("description"));
        l.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return l;
    };

    public List<WalletLedger> findByProviderId(Long providerId) {
        String sql = "SELECT * FROM provider_wallet_ledger WHERE provider_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, ledgerRowMapper, providerId);
    }

    public Long save(WalletLedger ledger) {
        String sql = "INSERT INTO provider_wallet_ledger (provider_id, booking_id, transaction_type, amount, running_balance, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, ledger.getProviderId());
            if (ledger.getBookingId() != null) {
                ps.setLong(2, ledger.getBookingId());
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
            ps.setString(3, ledger.getTransactionType());
            ps.setBigDecimal(4, ledger.getAmount());
            ps.setBigDecimal(5, ledger.getRunningBalance());
            ps.setString(6, ledger.getDescription());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("Failed to record wallet ledger entry");
    }
}
