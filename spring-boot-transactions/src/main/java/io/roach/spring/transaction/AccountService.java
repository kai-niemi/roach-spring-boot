package io.roach.spring.transaction;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import static java.sql.Statement.SUCCESS_NO_INFO;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class AccountService implements Pingable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccountRepository accountRepository;

    private SimpleJdbcInsert jdbcInsert;

    @PostConstruct
    public void afterInit() {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("t_account")
                .usingColumns("balance")
                .usingGeneratedKeyColumns("id");
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void create(List<AccountEntity> accounts) {
        MapSqlParameterSource[] parameters = accounts
                .stream()
                .map(project -> new MapSqlParameterSource()
                        .addValue("balance", project.getBalance()))
                .toArray(MapSqlParameterSource[]::new);

        int[] rowsAffected = this.jdbcInsert.executeBatch(parameters);

        Arrays.stream(rowsAffected).sequential().forEach(value -> {
            if (value != SUCCESS_NO_INFO) {
                throw new IncorrectResultSizeDataAccessException(1, value);
            }
        });
    }

    @Transactional(propagation = REQUIRES_NEW)
    public Iterable<AccountEntity> findAll() {
        return accountRepository.findAll();
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void persist(AccountEntity account) {
        accountRepository.save(account);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void clearAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("delete from t_transaction where 1=1");
        jdbcTemplate.execute("delete from t_account where 1=1");
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void ping() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Explicit transaction required");
        logger.info("Pong");
    }
}
