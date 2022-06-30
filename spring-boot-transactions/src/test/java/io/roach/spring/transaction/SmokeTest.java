package io.roach.spring.transaction;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class SmokeTest extends AbstractTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    @Qualifier("jdbcTransferService")
    private TransferService transactionServiceExplicit;

    @Autowired
    @Qualifier("CommonTableExpressionTransferService")
    private TransferService transactionServiceImplicit;

    @Test
    @Order(1)
    public void whenStarting_thenOnePingOnly() {
        accountService.ping();
        transactionServiceExplicit.ping();
        transactionServiceImplicit.ping();
    }
}
