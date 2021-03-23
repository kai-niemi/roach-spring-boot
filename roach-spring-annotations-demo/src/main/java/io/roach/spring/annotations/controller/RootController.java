package io.roach.spring.annotations.controller;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.annotations.TransactionBoundary;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootController {
    @GetMapping
    public ResponseEntity<IndexModel> index() {
        IndexModel index = new IndexModel("Welcome to Roach Spring Boot Annotations Demo");

        index.add(linkTo(methodOn(AccountController.class)
                .listAccounts(PageRequest.of(0, 5)))
                .withRel("accounts"));

        index.add(linkTo(methodOn(AccountController.class)
                .getBalance("alice"))
                .withRel("balance-total"));

        index.add(linkTo(methodOn(AccountController.class)
                .getBalance("bob"))
                .withRel("balance-total"));

        index.add(linkTo(methodOn(AccountController.class)
                .transfer(null))
                .withRel("transfer"));

        index.add(linkTo(methodOn(AccountController.class)
                .reset())
                .withRel("reset"));

        index.add(linkTo(methodOn(RootController.class)
                .databaseVersion())
                .withRel("version"));

        return new ResponseEntity<>(index, HttpStatus.OK);
    }

    @Autowired
    private DataSource dataSource;

    @GetMapping(value = "/db-version")
    @TransactionBoundary
    public HttpEntity<String> databaseVersion() {
        String version;
        try {
            version = new JdbcTemplate(dataSource).queryForObject("select version()", String.class);
        } catch (DataAccessException e) {
            version = "[" + e.getMessage() + "]";
        }
        return ResponseEntity.ok(version);
    }


}
