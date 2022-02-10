package io.roach.spring.multitenancy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.roach.spring.annotations.aspect.TransactionHintsAspect;
import io.roach.spring.multitenancy.aspect.TenantContextAspect;

@Configuration
@SpringJUnitConfig(TestConfig.class)
public class TestConfig {
    @Bean
    public TransactionHintsAspect transactionHintsAspect() {
        return new TransactionHintsAspect();
    }

    @Bean
    public TenantContextAspect tenantContextAspect() {
        return new TenantContextAspect();
    }
}
