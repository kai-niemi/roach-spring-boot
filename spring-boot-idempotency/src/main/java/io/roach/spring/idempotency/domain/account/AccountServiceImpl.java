package io.roach.spring.idempotency.domain.account;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.roach.spring.idempotency.domain.transaction.TransactionRepository;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void create(List<AccountEntity> accounts) {
        accountRepository.saveAll(accounts);
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public AccountEntity create(AccountEntity account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW, readOnly = true)
    public AccountEntity findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchAccountException("No such account: " + id));
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void update(AccountEntity account) {
        AccountEntity productProxy = accountRepository.getReferenceById(account.getId());
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void delete(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW, readOnly = true)
    public Page<AccountEntity> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void clearAll() {
        transactionRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("delete from outbox where 1=1");
    }
}
