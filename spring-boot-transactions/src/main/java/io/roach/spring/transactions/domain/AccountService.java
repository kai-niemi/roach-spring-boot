package io.roach.spring.transactions.domain;

import java.util.List;

public interface AccountService extends Pingable {
    void create(List<AccountEntity> accounts);

    Iterable<AccountEntity> findAll();

    void clearAll();
}
