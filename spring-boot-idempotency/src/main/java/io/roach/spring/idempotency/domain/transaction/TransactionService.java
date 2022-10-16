package io.roach.spring.idempotency.domain.transaction;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.roach.spring.idempotency.domain.transfer.TransferRequest;

public interface TransactionService {
    Page<TransactionEntity> findAll(Pageable pageable);

    List<TransactionEntity> createTransactions(TransferRequest request);

    TransactionEntity findById(Long id);

    TransactionEntity update(TransactionEntity entity);
}
