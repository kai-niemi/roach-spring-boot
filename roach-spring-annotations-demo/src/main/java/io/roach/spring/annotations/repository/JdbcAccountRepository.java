package io.roach.spring.annotations.repository;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import io.roach.spring.annotations.TransactionControlService;
import io.roach.spring.annotations.domain.Account;
import io.roach.spring.annotations.domain.AccountType;

@Repository
@TransactionControlService
@Profile("jdbc")
public class JdbcAccountRepository implements AccountRepository {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Page<Account> findAll(Pageable pageable) {
        String sql =
                "SELECT id,name,type,balance "
                        + "FROM account "
                        + "ORDER BY id "
                        + "LIMIT :limit OFFSET :offset ";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        List<Account> accounts = this.namedParameterJdbcTemplate
                .query(sql, parameters, (rs, rowNum) -> new Account(
                        rs.getLong(1),
                        rs.getString(2),
                        AccountType.valueOf(rs.getString(3)),
                        rs.getBigDecimal(4)));

        return new PageImpl<>(accounts, pageable, countAll(parameters));
    }

    private Long countAll(MapSqlParameterSource params) {
        return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT count(id) FROM account", params, Long.class);
    }

    @Override
    public Account getOne(Long id) {
        return this.jdbcTemplate.queryForObject(
                "SELECT id,name,type,balance "
                        + "FROM account "
                        + "WHERE id=?",
                (rs, rowNum) -> new Account(
                        rs.getLong(1),
                        rs.getString(2),
                        AccountType.valueOf(rs.getString(3)),
                        rs.getBigDecimal(4)),
                id
        );
    }

    @Override
    public BigDecimal getBalance(String name) {
        return this.jdbcTemplate.queryForObject(
                "SELECT sum(balance) "
                        + "FROM account "
                        + "WHERE name=?",
                (rs, rowNum) -> rs.getBigDecimal(1),
                name
        );
    }

    @Override
    public void updateBalance(String name, AccountType type, BigDecimal balance) {
        int rowsAffected = jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE account "
                                    + "SET balance=balance+? "
                                    + "WHERE name=? and type=?");
                    ps.setObject(1, balance);
                    ps.setString(2, name);
                    ps.setObject(3, type.name());
                    return ps;
                });
        if (rowsAffected != 1) {
            throw new IncorrectResultSizeDataAccessException(1, rowsAffected);
        }
    }

    @Override
    public void resetAllBalances(BigDecimal balance) {
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE account "
                                    + "SET balance=balance+? "
                    );
                    ps.setObject(1, balance);
                    return ps;
                });
    }
}
