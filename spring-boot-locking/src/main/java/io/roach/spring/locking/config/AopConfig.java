package io.roach.spring.locking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.roach.spring.annotations.aspect.RetryableAspect;
import io.roach.spring.annotations.aspect.TransactionHintsAspect;
import io.roach.spring.locking.ProfileNames;

@Configuration
public class AopConfig {
    @Bean
    @Profile({ProfileNames.CRDB, ProfileNames.CRDB_DEV, ProfileNames.CRDB_CLOUD})
    public TransactionHintsAspect transactionHintsAspect() {
        return new TransactionHintsAspect();
    }

    @Bean
    public RetryableAspect retryableTransactionalAspect() {
        return new RetryableAspect();
    }
}
