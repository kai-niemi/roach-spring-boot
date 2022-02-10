package io.roach.spring.annotations.repository;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.roach.spring.annotations.TransactionService;
import io.roach.spring.annotations.domain.Account;
import io.roach.spring.annotations.domain.AccountType;

@Repository
@TransactionService
@Profile("jpa")
public class JpaAccountRepositoryDelegate implements AccountRepository {
    @Autowired
    private JpaAccountRepository accountJpaRepository;

    @Override
    public Page<Account> findAll(Pageable page) {
        return accountJpaRepository.findAll(page);
    }

    @Override
    public Account getOne(Long id) {
        return accountJpaRepository.getOne(id);
    }

    @Override
    public BigDecimal getBalance(String name) {
        return accountJpaRepository.getBalance(name);
    }

    @Override
    public void updateBalance(String name, AccountType type, BigDecimal balance) {
        accountJpaRepository.updateBalance(name, type, balance);
    }

    @Override
    public void resetAllBalances(BigDecimal balance) {
        accountJpaRepository.resetAllBalances(balance);
    }
}
