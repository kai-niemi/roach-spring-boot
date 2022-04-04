package io.roach.spring.pagination;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.roach.spring.annotations.aspect.RetryableAspect;
import io.roach.spring.annotations.aspect.TransactionHintsAspect;

@Configuration
@SpringJUnitConfig(TestConfig.class)
public class TestConfig {
    @Bean
    public TransactionHintsAspect transactionHintsAspect() {
        return new TransactionHintsAspect();
    }

    @Bean
    public RetryableAspect transactionRetryAspect() {
        return new RetryableAspect();
    }
}
