package io.roach.spring.pooling;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackageClasses = PoolingApplication.class)
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = {"io.roach.spring.pooling"},
        enableDefaultTransactions = false)
@EnableJpaAuditing(modifyOnCreate = false,
        auditorAwareRef = "auditorProvider")
@EnableTransactionManagement(proxyTargetClass = true,
        order = Ordered.LOWEST_PRECEDENCE - 1)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class PoolingApplication implements CommandLineRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PoolingApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    @Override
    public void run(String... args) {
    }
}


