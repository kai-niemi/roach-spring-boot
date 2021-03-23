package io.roach.spring.annotations;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.roach.spring.annotations.aspect.AdvisorOrder;

@Configuration
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class
})
//@EnableConfigurationProperties
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableJpaRepositories
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableSpringDataWebSupport
@EnableTransactionManagement(order = AdvisorOrder.INNER_BOUNDARY)
@ComponentScan(basePackageClasses = Application.class)
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) {
        logger.info("Bootstrapping roach-spring-annotations CockroachDB schema");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/create.sql"));
        populator.setCommentPrefix("--");
        populator.setIgnoreFailedDrops(true);

        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}