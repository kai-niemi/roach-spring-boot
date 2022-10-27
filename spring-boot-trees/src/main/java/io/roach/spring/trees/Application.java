package io.roach.spring.trees;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackageClasses = Application.class)
@EnableAutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(basePackageClasses = Application.class, enableDefaultTransactions = false)
@EntityScan(basePackageClasses = Application.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}