package io.roach.spring.cte;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Service
public class TransactionServiceExplicit implements TransactionService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTransaction(TransactionEntity singleton) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Transaction expected!");

        jdbcTemplate.update("INSERT INTO t_transaction ("
                        + "id, "
                        + "account_id, "
                        + "amount, "
                        + "transaction_type, "
                        + "transaction_status) "
                        + "VALUES (unique_rowid(), ?, ?, ?, ?) RETURNING NOTHING",
                ps -> {
                    int i = 1;
                    ps.setLong(i++, singleton.getAccountId());
                    ps.setDouble(i++, singleton.getAmount());
                    ps.setString(i++, singleton.getTransactionType());
                    ps.setString(i++, singleton.getTransactionStatus());
                });

        jdbcTemplate.update("UPDATE t_account SET balance=balance+? WHERE id=?",
                ps -> {
                    ps.setDouble(1, singleton.getAmount());
                    ps.setLong(2, singleton.getAccountId());
                });
    }

    @Override
    public void createTransactions(List<TransactionEntity> batch) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO t_transaction ("
                        + "id, "
                        + "account_id, "
                        + "amount, "
                        + "transaction_type, "
                        + "transaction_status) "
                        + "VALUES (unique_rowid(), ?, ?, ?, ?) RETURNING NOTHING",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        TransactionEntity item = batch.get(i);
                        int idx = 1;
                        ps.setLong(idx++, item.getAccountId());
                        ps.setDouble(idx++, item.getAmount());
                        ps.setString(idx++, item.getTransactionType());
                        ps.setString(idx++, item.getTransactionStatus());
                    }

                    @Override
                    public int getBatchSize() {
                        return batch.size();
                    }
                });

        jdbcTemplate.batchUpdate(
                "UPDATE t_account SET balance=balance+? WHERE id=?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        TransactionEntity item = batch.get(i);
                        int idx = 1;
                        ps.setDouble(idx++, item.getAmount());
                        ps.setLong(idx++, item.getAccountId());
                    }

                    @Override
                    public int getBatchSize() {
                        return batch.size();
                    }
                });
    }
}
