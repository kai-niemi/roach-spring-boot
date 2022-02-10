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
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class
})
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableSpringDataWebSupport
@ComponentScan(basePackageClasses = AnnotationsApplication.class)
public class AnnotationsApplication implements CommandLineRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AnnotationsApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) {
        logger.info("Bootstrapping spring-boot-annotations CockroachDB schema");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/create.sql"));
        populator.setCommentPrefix("--");
        populator.setIgnoreFailedDrops(true);

        DatabasePopulatorUtils.execute(populator, dataSource);

        logger.info("Bootstrapping done!");
    }
}