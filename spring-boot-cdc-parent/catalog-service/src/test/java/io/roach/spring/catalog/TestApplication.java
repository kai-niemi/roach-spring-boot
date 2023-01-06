package io.roach.spring.catalog;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.roach.spring.catalog.util.ExcludeFromTest;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        DataSourceAutoConfiguration.class
})
@EntityScan(basePackageClasses = CatalogApplication.class)
@ComponentScan(basePackages = "io.roach.spring.catalog",
        excludeFilters = {@ComponentScan.Filter(classes = ExcludeFromTest.class)})
public class TestApplication implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
    }
}
