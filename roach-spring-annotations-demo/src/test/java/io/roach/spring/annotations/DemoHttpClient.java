package io.roach.spring.annotations;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.roach.spring.annotations.controller.TransferRequest;
import io.roach.spring.annotations.domain.AccountType;

public class DemoHttpClient {
    private final Link transferLink;

    private final Link balanceTotalLink;

    private final Link resetLink;

    private final RestTemplate restTemplate = new RestTemplate();

    public DemoHttpClient(String baseUri) {
        Traverson traverson = new Traverson(URI.create(baseUri),
                MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
        this.transferLink = traverson.follow("transfer").asLink();
        this.balanceTotalLink = traverson.follow("balance-total").asLink();
        this.resetLink = traverson.follow("reset").asLink();
    }

    public ResponseEntity<String> balanceTotal(String name) {
        return restTemplate.getForEntity(balanceTotalLink.toUri().toString(), String.class, name);
    }

    public ResponseEntity<String> transfer(String name, AccountType type, BigDecimal amount) {
        return restTemplate
                .postForEntity(transferLink.toUri(),
                        TransferRequest.builder()
                                .setName(name)
                                .setAccountType(type)
                                .setAmount(amount)
                                .build(),
                        String.class);
    }

    public void reset() {
        restTemplate.postForEntity(resetLink.toUri(), null, Void.class);
    }
}
