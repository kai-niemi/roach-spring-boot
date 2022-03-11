package io.roach.spring.multitenancy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.roach.spring.annotations.aspect.RetryableAspect;

@Configuration
public class AopConfig {
    @Bean
    public RetryableAspect retryableTransactionalAspect() {
        return new RetryableAspect();
    }
}
