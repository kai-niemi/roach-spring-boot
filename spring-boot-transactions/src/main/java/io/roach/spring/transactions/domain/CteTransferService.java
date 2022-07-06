package io.roach.spring.transactions.domain;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Service
public class CteTransferService implements TransferService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void createTransfer_WithPreCondition(TransactionEntity singleton) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void ping() {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(),
                "Explicit transaction not expected");
        logger.info("Pong");
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createTransfer(TransactionEntity singleton) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Transaction not expected!");

        jdbcTemplate.update(
                "WITH x AS ( "
                        + "INSERT INTO t_transaction ( "
                        + "  id, "
                        + "  account_id, "
                        + "  amount, "
                        + "  transaction_type, "
                        + "  transaction_status) "
                        + "VALUES (unique_rowid(), ?, ?, ?, ?) RETURNING account_id) "
                        + "  UPDATE t_account SET balance=balance+? WHERE id=? RETURNING NOTHING",
                ps -> {
                    int i = 1;
                    ps.setLong(i++, singleton.getAccountId());
                    ps.setDouble(i++, singleton.getAmount());
                    ps.setString(i++, singleton.getTransactionType());
                    ps.setString(i++, singleton.getTransactionStatus());
                    ps.setDouble(i++, singleton.getAmount());
                    ps.setLong(i++, singleton.getAccountId());
                });
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createTransferCollection(List<TransactionEntity> collection) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Transaction not expected!");

        jdbcTemplate.batchUpdate(
                "WITH x AS ( "
                        + "INSERT INTO t_transaction ( "
                        + "  id, "
                        + "  account_id, "
                        + "  amount, "
                        + "  transaction_type, "
                        + "  transaction_status) "
                        + "VALUES (unique_rowid(), ?, ?, ?, ?) RETURNING account_id) "
                        + "  UPDATE t_account SET balance=balance+? WHERE id=? RETURNING NOTHING",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        TransactionEntity item = collection.get(i);
                        int idx = 1;
                        ps.setLong(idx++, item.getAccountId());
                        ps.setDouble(idx++, item.getAmount());
                        ps.setString(idx++, item.getTransactionType());
                        ps.setString(idx++, item.getTransactionStatus());
                        ps.setDouble(idx++, item.getAmount());
                        ps.setLong(idx++, item.getAccountId());
                    }

                    @Override
                    public int getBatchSize() {
                        return collection.size();
                    }
                });
    }
}
