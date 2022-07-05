package io.roach.spring.outbox.domain;

import java.util.List;

public interface TransactionService {
    TransactionEntity createTransaction(TransactionEntity entity);

    List<TransactionEntity> createTransactionCollection(List<TransactionEntity> entities);
}
