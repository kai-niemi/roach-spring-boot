package io.roach.spring.order;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.roach.spring.order.util.ExcludeFromTest;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        DataSourceAutoConfiguration.class
})
@EntityScan(basePackageClasses = OrderApplication.class)
@ComponentScan(basePackages = "io.roach.spring.order",
        excludeFilters = {@ComponentScan.Filter(classes = ExcludeFromTest.class)})
public class TestApplication implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
    }
}
