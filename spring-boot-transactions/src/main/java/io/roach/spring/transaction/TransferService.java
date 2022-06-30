package io.roach.spring.transaction;

import java.util.List;

public interface TransferService extends Pingable {
    void createTransfer_WithPreCondition(TransactionEntity singleton);

    void createTransfer(TransactionEntity singleton);

    void createTransferCollection(List<TransactionEntity> entities);
}
