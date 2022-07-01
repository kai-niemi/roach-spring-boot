package io.roach.spring.transactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class JdbcTransferTest extends AbstractTransactionTest {
    @Autowired
    @Qualifier("jdbcTransferService")
    private TransferService transferService;

    @Override
    protected TransferService getTransferService() {
        return transferService;
    }
}
