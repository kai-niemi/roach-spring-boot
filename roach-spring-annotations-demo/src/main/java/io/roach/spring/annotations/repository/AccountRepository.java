package io.roach.spring.annotations.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.roach.spring.annotations.domain.Account;
import io.roach.spring.annotations.domain.AccountType;

public interface AccountRepository {
    Page<Account> findAll(Pageable page);

    Account getOne(Long id);

    BigDecimal getBalance(String name);

    void updateBalance(String name, AccountType type, BigDecimal balance);

    void resetAllBalances(BigDecimal balance);
}
