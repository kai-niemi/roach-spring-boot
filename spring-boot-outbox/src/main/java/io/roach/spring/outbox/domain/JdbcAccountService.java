package io.roach.spring.outbox.domain;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.sql.Statement.SUCCESS_NO_INFO;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class JdbcAccountService implements AccountService {
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

    @Override
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

    @Override
    @Transactional(propagation = REQUIRES_NEW, readOnly = true)
    public Page<AccountEntity> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void clearAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("delete from t_transaction where 1=1");
        jdbcTemplate.execute("delete from t_account where 1=1");
        jdbcTemplate.execute("delete from t_outbox where 1=1");
    }
}
