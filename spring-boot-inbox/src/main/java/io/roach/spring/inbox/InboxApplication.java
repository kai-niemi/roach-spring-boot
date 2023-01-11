package io.roach.spring.inbox;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication
@EnableHypermediaSupport(type = {
        EnableHypermediaSupport.HypermediaType.HAL_FORMS,
        EnableHypermediaSupport.HypermediaType.HAL})
@EnableSpringDataWebSupport
@EnableJpaRepositories(basePackages = {"io.roach.spring"})
@EnableTransactionManagement(proxyTargetClass = true,
        order = Ordered.LOWEST_PRECEDENCE - 1)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class InboxApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(InboxApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    @Bean
    public CurieProvider defaultCurieProvider() {
        return new DefaultCurieProvider("roach-spring", UriTemplate.of("/rels/{rel}"));
    }

    @Bean
    public HalFormsConfiguration halFormsConfiguration() {
        return new HalFormsConfiguration();
    }
}
