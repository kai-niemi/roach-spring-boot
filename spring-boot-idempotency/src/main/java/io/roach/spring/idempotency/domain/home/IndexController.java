package io.roach.spring.idempotency.domain.home;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.idempotency.domain.account.AccountController;
import io.roach.spring.idempotency.domain.transaction.TransactionController;
import io.roach.spring.idempotency.domain.transfer.TransferController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/")
public class IndexController {
    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(AccountController.class)
                .findAll(PageRequest.ofSize(5)))
                .withRel("accounts")
                .withTitle("Paginated collection of accounts"));

        index.add(linkTo(methodOn(TransactionController.class)
                .findAll(PageRequest.ofSize(5)))
                .withRel("transactions")
                .withTitle("Paginated collection of transactions from transfers"));

        index.add(linkTo(methodOn(TransferController.class)
                .index())
                .withRel("transfer")
                .withTitle("Transfer workflow resource"));

        return ResponseEntity.ok(index);
    }
}
