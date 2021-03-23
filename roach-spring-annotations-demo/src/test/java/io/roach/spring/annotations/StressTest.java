package io.roach.spring.annotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import io.roach.spring.annotations.domain.AccountType;

public class StressTest {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void whenMovingFundsConcurrently_thenObserveSerializationErrorsAndFunStuff_withCorrectOutcome() {
        final LinkedList<Future<HttpStatus>> futures = new LinkedList<>();

        final DemoHttpClient httpClient = new DemoHttpClient("http://localhost:8090");
        httpClient.reset();

        // Use concurrent threads (>10) to cause transient serialization errors
        ExecutorService pool = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 200; i++) {
            String name = (i % 2 == 0) ? "alice" : "bob";
            AccountType type = (i % 2 == 0) ? AccountType.expense : AccountType.asset;
            BigDecimal amount = BigDecimal.valueOf(Math.random() * 50).setScale(2, RoundingMode.HALF_EVEN).negate();

            futures.offer(pool.submit(() -> {

                HttpStatus status = httpClient.transfer(name, type, amount).getStatusCode();
                logger.info("Withdraw ${} from {} ({}) - {}", amount, name, type, status);
                return status;
            }));
        }

        while (!futures.isEmpty()) {
            try {
                HttpStatus status = futures.poll().get();
                Assertions.assertTrue(status.is2xxSuccessful(), "Unexpected: " + status.value());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("", e);
            } catch (ExecutionException e) {
//                logger.error("", e);
                if (e.getCause() instanceof HttpClientErrorException) {
                    HttpClientErrorException ex = (HttpClientErrorException) e.getCause();
                    Assertions.assertEquals(HttpStatus.EXPECTATION_FAILED, ex.getStatusCode()); // Negative balance
                } else {
                    Assertions.fail("Not good!");
                }
            }
        }

        pool.shutdown();

        BigDecimal aliceBalance = new BigDecimal(httpClient.balanceTotal("alice").getBody());
        BigDecimal bobBalance = new BigDecimal(httpClient.balanceTotal("bob").getBody());

        logger.info("Final balance for Alice: {}", aliceBalance);
        logger.info("Final balance for Bob: {}", bobBalance);

        // Expected to fail in PSQL unless using SSI
        Assertions.assertFalse(aliceBalance.compareTo(BigDecimal.ZERO) < 0,
                "Negative balance for Alice: " + aliceBalance);
        Assertions.assertFalse(aliceBalance.compareTo(BigDecimal.ZERO) < 0,
                "Negative balance for Bob: " + bobBalance);
    }
}
