package io.roach.spring.multitenancy;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.roach.spring.annotations.aspect.AdvisorOrder;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableTransactionManagement(proxyTargetClass = true, order = AdvisorOrder.HIGH)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackageClasses = MultiTenancyApplication.class)
public class MultiTenancyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MultiTenancyApplication.class)
                .logStartupInfo(true)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.CONSOLE)
                .run(args);
    }
}

