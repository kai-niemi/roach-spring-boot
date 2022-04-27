package io.roach.spring.cte;

import java.util.List;

public interface TransactionService {
    void createTransaction(TransactionEntity singleton);

    void createTransactions(List<TransactionEntity> batch);
}
