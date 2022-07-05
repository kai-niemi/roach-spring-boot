package io.roach.spring.outbox;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"io.roach.spring.outbox"})
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableTransactionManagement(proxyTargetClass = true, order = Ordered.LOWEST_PRECEDENCE - 2)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackageClasses = OutboxApplication.class)
public class OutboxApplication {
}

