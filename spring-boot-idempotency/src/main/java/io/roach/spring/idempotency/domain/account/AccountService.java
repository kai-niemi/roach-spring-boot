package io.roach.spring.idempotency.domain.account;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    Page<AccountEntity> findAll(Pageable pageable);

    AccountEntity findById(Long id);

    AccountEntity create(AccountEntity account);

    void update(AccountEntity account);

    void delete(Long id);

    void create(List<AccountEntity> accounts);

    void clearAll();
}
