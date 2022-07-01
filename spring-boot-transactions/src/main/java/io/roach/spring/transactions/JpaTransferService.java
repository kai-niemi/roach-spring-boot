package io.roach.spring.transactions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
public class JpaTransferService implements TransferService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JpaTransferService selfRef;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void createTransfer_WithPreCondition(TransactionEntity singleton) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Transaction not expected!");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity("https://status.sunet.se/", String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            selfRef.createTransfer(singleton);
        } else {
            throw new IllegalStateException("Disturbance");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ping() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Transaction expected!");
        logger.info("Pong");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTransfer(TransactionEntity singleton) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Transaction expected!");
        transactionRepository.save(singleton);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTransferCollection(List<TransactionEntity> collection) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Transaction expected!");
        transactionRepository.saveAll(collection);
    }
}
