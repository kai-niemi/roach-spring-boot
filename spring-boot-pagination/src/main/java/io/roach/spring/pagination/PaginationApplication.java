package io.roach.spring.pagination;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.roach.spring.annotations.aspect.AdvisorOrder;
import io.roach.spring.pagination.repository.Doubles;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableTransactionManagement(proxyTargetClass = true, order = AdvisorOrder.HIGH)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackageClasses = PaginationApplication.class)
public class PaginationApplication implements CommandLineRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PaginationApplication.class)
                .logStartupInfo(true)
                .web(WebApplicationType.SERVLET)
                .bannerMode(Banner.Mode.CONSOLE)
                .run(args);
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Doubles doubles;

    @Override
    public void run(String... args) {
        List<String> argsList = Arrays.asList(args);

        if (argsList.isEmpty()) {
            logger.info("Usage: ./spring-boot-pagination.jar [options]");
            logger.info("Options:");
            logger.info("--delete   - delete all test data");
            logger.info("--small    - load with 1K rows per entity");
            logger.info("--medium   - load with 10K rows per entity");
            logger.info("--large    - load with 100K rows per entity");
            logger.info("--xl       - load with 1M rows per entity");
            logger.info("--xxl      - load with 10M rows per entity");
        }

        if (argsList.contains("--delete")) {
            logger.info("Deleting all test data..");
            doubles.deleteTestData();
        }

        int size = 0;
        if (argsList.contains("--small")) {
            size = 1000;
        } else if (argsList.contains("--medium")) {
            size = 10_000;
        } else if (argsList.contains("--large")) {
            size = 100_000;
        } else if (argsList.contains("--xl")) {
            size = 1000_000;
        } else if (argsList.contains("--xxl")) {
            size = 10_000_000;
        }

        if (size > 0) {
            logger.info("Populating with test data (size: {})", size);
            doubles.createCustomers(size);
            doubles.createProducts(size);
            doubles.createOrders(size);
        }
    }
}

