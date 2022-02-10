package io.roach.spring.annotations.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.roach.spring.annotations.aspect.FollowerReadAspect;
import io.roach.spring.annotations.aspect.TimeTravelAspect;
import io.roach.spring.annotations.aspect.TransactionHintsAspect;

@Configuration
public class AopConfig {
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
