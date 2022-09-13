package io.roach.spring.outbox.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    void create(List<AccountEntity> accounts);

    Page<AccountEntity> findAll(Pageable pageable);

    void clearAll();
}
