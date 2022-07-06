package io.roach.spring.transactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.transactions.domain.TransferService;

@ActiveProfiles({"test","verbose"})
public class JdbcTransferTest extends AbstractTransferTest {
    @Autowired
    @Qualifier("jdbcTransferService")
    private TransferService transferService;

    @Override
    protected TransferService getTransferService() {
        return transferService;
    }
}
