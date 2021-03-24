package io.roach.spring.annotations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.roach.spring.annotations.aspect.FollowerReadAspect;
import io.roach.spring.annotations.aspect.RetryableAspect;
import io.roach.spring.annotations.aspect.RetryableSavepointAspect;
import io.roach.spring.annotations.aspect.TimeTravelAspect;
import io.roach.spring.annotations.aspect.TransactionHintsAspect;

@Configuration
public class AopConfig {
    @Profile("!savepoints")
    @Bean
    public RetryableAspect retryableTransactionalAspect() {
        return new RetryableAspect();
    }

    // Savepoints only works with JDBC
    @Profile("savepoints")
    @Bean
    public RetryableSavepointAspect savepointTransactionAspect() {
        return new RetryableSavepointAspect("cockroach_restart");
    }

    @Bean
    public TransactionHintsAspect transactionHintsAspect() {
        return new TransactionHintsAspect();
    }

    @Bean
    public FollowerReadAspect followerReadAspect() {
        return new FollowerReadAspect();
    }

    @Bean
    public TimeTravelAspect timeTravelAspect() {
        return new TimeTravelAspect();
    }
}
