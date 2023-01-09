package io.roach.spring.order.config;

import java.util.Currency;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.roach.spring.order.LinkRelations;
import io.roach.spring.order.util.Money;

@Configuration
@EnableHypermediaSupport(type = {
        EnableHypermediaSupport.HypermediaType.HAL_FORMS, EnableHypermediaSupport.HypermediaType.HAL})
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, Currency.class, Currency::getInstance);
        registry.addConverter(String.class, Money.class, Money::of);
        registry.addFormatter(new Formatter<Money>() {
            @Override
            public String print(Money object, Locale locale) {
                return object.toString();
            }

            @Override
            public Money parse(String text, Locale locale) {
                return Money.of(text);
            }
        });
    }

    @Bean
    public HalFormsConfiguration halFormsConfiguration() {
        return new HalFormsConfiguration();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .defaultContentType(
                        MediaTypes.HAL_FORMS_JSON,
                        MediaTypes.HAL_JSON,
                        MediaTypes.VND_ERROR_JSON,
                        MediaType.APPLICATION_JSON,
                        MediaType.ALL);
    }

    @Bean
    public CurieProvider defaultCurieProvider() {
        String uri = ServletUriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .pathSegment("order-service")
                .pathSegment("rels")
                .pathSegment("{rel}.html")
                .build().toUriString();
        return new DefaultCurieProvider(LinkRelations.CURIE_NAMESPACE, UriTemplate.of(uri));
    }
}
