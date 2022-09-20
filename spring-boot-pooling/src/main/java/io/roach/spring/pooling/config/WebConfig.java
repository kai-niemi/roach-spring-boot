package io.roach.spring.pooling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;

@EnableHypermediaSupport(type = {
        EnableHypermediaSupport.HypermediaType.HAL_FORMS,
        EnableHypermediaSupport.HypermediaType.HAL})
@EnableSpringDataWebSupport
@Configuration
public class WebConfig {
    @Bean
    public CurieProvider defaultCurieProvider() {
        return new DefaultCurieProvider("roach-spring", UriTemplate.of("/rels/{rel}"));
    }

    @Bean
    public HalFormsConfiguration halFormsConfiguration() {
        HalFormsConfiguration configuration = new HalFormsConfiguration();
        return configuration;
    }
}
