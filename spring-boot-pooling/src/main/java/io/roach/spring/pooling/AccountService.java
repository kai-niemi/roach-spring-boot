package io.roach.spring.pooling;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AccountEntity createOne(AccountEntity account) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        return accountRepository.save(account);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AccountEntity> createAll(Iterable<AccountEntity> accounts) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        return accountRepository.saveAll(accounts);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AccountEntity findById(UUID id) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        return accountRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("No such account: " + id));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Page<AccountEntity> findPage(Pageable pageable) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        return accountRepository.findAll(pageable);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(AccountEntity account) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        AccountEntity persistentAccount = accountRepository.getReferenceById(account.getId());
        persistentAccount.setName(account.getName());
        persistentAccount.setBalance(account.getBalance());
        persistentAccount.setDescription(account.getDescription());
        persistentAccount.setClosed(account.isClosed());
        persistentAccount.setCurrency(account.getCurrency());
        persistentAccount.setUpdatedTime(LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(UUID id, boolean closed) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        AccountEntity persistentAccount = accountRepository.getReferenceById(id);
        persistentAccount.setClosed(closed);
        persistentAccount.setUpdatedTime(LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(UUID id) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        accountRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAll() {
        accountRepository.deleteAllInBatch();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void simulateProcessingDelay(int delay) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");

        accountRepository.findAll(PageRequest.of(0,16)).getContent().forEach(accountEntity -> {
            accountEntity.getName();
        });

        try {
            Thread.sleep(delay*1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
