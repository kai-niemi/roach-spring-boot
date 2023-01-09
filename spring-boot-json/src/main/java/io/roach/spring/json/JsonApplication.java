package io.roach.spring.json;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJdbcRepositories
@EnableAspectJAutoProxy
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 1) // Bump up one level to enable extra advisors
@SpringBootApplication
public class JsonApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(JsonApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
