package io.roach.spring.idempotency.domain.transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.idempotency.domain.account.AccountEntity;
import io.roach.spring.idempotency.domain.account.AccountRepository;
import io.roach.spring.idempotency.domain.transfer.IllegalTransferException;
import io.roach.spring.idempotency.domain.transfer.NegativeBalanceException;
import io.roach.spring.idempotency.domain.transfer.TransferRequest;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Page<TransactionEntity> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public TransactionEntity findById(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new NoSuchTransactionException(
                "No transaction with ID: " + id));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TransactionEntity> createTransactions(TransferRequest request) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Transaction expected!");

        List<TransactionEntity> transactions = new ArrayList<>();

        BigDecimal checksum = BigDecimal.ZERO;

        for (TransferRequest.AccountLeg leg : request.getLegs()) {
            AccountEntity account = accountRepository.getReferenceById(leg.getId());

            TransactionEntity transaction = new TransactionEntity();
            transaction.setAccount(account);
            transaction.setAmount(leg.getAmount());
            transaction.setStatus(TransactionStatus.placed);
            transaction.setType("gen");
            transactions.add(transaction);

            updateBalance(account, leg.getAmount());

            checksum = checksum.add(BigDecimal.valueOf(leg.getAmount()));

            transactionRepository.save(transaction);
        }

        if (!BigDecimal.ZERO.setScale(1, RoundingMode.UNNECESSARY).equals(checksum)) {
            throw new IllegalTransferException("Sum of legs does not equal 0 but: " + checksum);
        }

        return transactions;
    }

    private void updateBalance(AccountEntity account, Double amount) {
        double balance = accountRepository.addAmount(account.getId(), amount);
        if (balance < 0) {
            throw new NegativeBalanceException(
                    "Insufficient funds for " + account);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionEntity update(TransactionEntity entity) {
//        transactionRepository.getReferenceById(entity.getId());
        throw new UnsupportedOperationException("Not implemented");
    }
}
