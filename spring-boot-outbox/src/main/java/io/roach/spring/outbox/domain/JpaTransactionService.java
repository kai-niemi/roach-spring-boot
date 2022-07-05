package io.roach.spring.outbox.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Service
public class JpaTransactionService implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public int foo(int bar) {
        return 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionEntity createTransaction(TransactionEntity entity) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Transaction expected!");
        transactionRepository.save(entity);
        return entity;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TransactionEntity> createTransactionCollection(List<TransactionEntity> transactionEntities) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Transaction expected!");
        transactionRepository.saveAll(transactionEntities);
        return transactionEntities;
    }
}
