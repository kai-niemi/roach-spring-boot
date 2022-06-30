package io.roach.spring.transaction;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
public class JdbcTransferService implements TransferService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ping() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Explicit transaction required");
        logger.info("Pong");
    }

    @Autowired
    private JdbcTransferService selfRef;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void createTransfer_WithPreCondition(TransactionEntity singleton) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Transaction not expected!");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity("https://status.sunet.se/", String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            selfRef.createTransfer(singleton);
        } else {
            throw new IllegalStateException("Disturbance");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTransfer(TransactionEntity singleton) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Transaction expected!");

        jdbcTemplate.update(
                "INSERT INTO t_transaction (id, account_id, amount, transaction_type, transaction_status) "
                        + "VALUES (unique_rowid(), ?, ?, ?, ?) RETURNING NOTHING", ps -> {
                    int i = 1;
                    ps.setLong(i++, singleton.getAccountId());
                    ps.setDouble(i++, singleton.getAmount());
                    ps.setString(i++, singleton.getTransactionType());
                    ps.setString(i, singleton.getTransactionStatus());
                });

        jdbcTemplate.update("UPDATE t_account SET balance=balance+? WHERE id=?", ps -> {
            ps.setDouble(1, singleton.getAmount());
            ps.setLong(2, singleton.getAccountId());
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTransferCollection(List<TransactionEntity> entities) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO t_transaction (id, account_id, amount, transaction_type, transaction_status) "
                        + "VALUES (unique_rowid(), ?, ?, ?, ?) RETURNING NOTHING",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        TransactionEntity item = entities.get(i);
                        int idx = 1;
                        ps.setLong(idx++, item.getAccountId());
                        ps.setDouble(idx++, item.getAmount());
                        ps.setString(idx++, item.getTransactionType());
                        ps.setString(idx, item.getTransactionStatus());
                    }

                    @Override
                    public int getBatchSize() {
                        return entities.size();
                    }
                });

        jdbcTemplate.batchUpdate("UPDATE t_account SET balance=balance+? WHERE id=?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        TransactionEntity item = entities.get(i);
                        ps.setDouble(1, item.getAmount());
                        ps.setLong(2, item.getAccountId());
                    }

                    @Override
                    public int getBatchSize() {
                        return entities.size();
                    }
                });
    }
}
