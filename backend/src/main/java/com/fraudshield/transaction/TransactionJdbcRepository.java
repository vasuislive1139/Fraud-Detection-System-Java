package com.fraudshield.transaction;

import com.fraudshield.core.entity.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class TransactionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchInsert(List<Transaction> transactions) {
        String sql = "INSERT INTO transactions (id, step, type, amount, name_orig, old_balance_orig, new_balance_orig, name_dest, old_balance_dest, new_balance_dest, is_fraud, is_flagged_fraud, event_time, import_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, transactions, 1000, (PreparedStatement ps, Transaction tx) -> {
            ps.setObject(1, tx.getId());
            ps.setInt(2, tx.getStep());
            ps.setString(3, tx.getType());
            ps.setBigDecimal(4, tx.getAmount());
            ps.setString(5, tx.getNameOrig());
            ps.setBigDecimal(6, tx.getOldBalanceOrig());
            ps.setBigDecimal(7, tx.getNewBalanceOrig());
            ps.setString(8, tx.getNameDest());
            ps.setBigDecimal(9, tx.getOldBalanceDest());
            ps.setBigDecimal(10, tx.getNewBalanceDest());
            ps.setBoolean(11, tx.getIsFraud());
            ps.setBoolean(12, tx.getIsFlaggedFraud());
            ps.setTimestamp(13, Timestamp.valueOf(tx.getEventTime()));
            ps.setObject(14, tx.getDatasetImport().getId());
        });
    }
}
