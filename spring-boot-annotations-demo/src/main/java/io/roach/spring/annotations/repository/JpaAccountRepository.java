package io.roach.spring.annotations.repository;

import java.math.BigDecimal;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import io.roach.spring.annotations.domain.Account;
import io.roach.spring.annotations.domain.AccountType;

@Profile("jpa")
public interface JpaAccountRepository extends JpaRepository<Account, Long> {
    @Query(value = "select sum(a.balance) from Account a where a.name=?1")
    BigDecimal getBalance(String name);

    @Modifying
    @Query("update Account a set a.balance = a.balance + ?3 where a.name = ?1 and a.type=?2")
    void updateBalance(String name, AccountType type, BigDecimal balance);

    @Modifying
    @Query("update Account a set a.balance = ?1")
    void resetAllBalances(BigDecimal balance);
}
