package io.roach.spring.annotations.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.annotations.FollowerRead;
import io.roach.spring.annotations.TimeTravel;
import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.annotations.TransactionHint;
import io.roach.spring.annotations.TransactionHints;
import io.roach.spring.annotations.domain.Account;
import io.roach.spring.annotations.domain.AccountType;
import io.roach.spring.annotations.domain.NegativeBalanceException;
import io.roach.spring.annotations.repository.AccountRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/account")
@TransactionBoundary(priority = TransactionBoundary.Priority.normal)
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountResourceAssembler accountResourceAssembler;

    @Autowired
    private PagedResourcesAssembler<Account> pagedResourcesAssembler;

    @GetMapping
    // Overrides type level annotation
    @TransactionBoundary(
            readOnly = true,
            vectorize = TransactionBoundary.Vectorize.off,
            priority = TransactionBoundary.Priority.low)
    @TimeTravel
    public HttpEntity<PagedModel<AccountModel>> listAccounts(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page) {
        return ResponseEntity.ok(pagedResourcesAssembler
                .toModel(accountRepository.findAll(page), accountResourceAssembler));
    }

    @GetMapping(value = "/{id}")
    @TransactionBoundary(priority = TransactionBoundary.Priority.low)
    @FollowerRead
    public HttpEntity<AccountModel> getAccount(@PathVariable("id") Long accountId) {
        return new ResponseEntity<>(accountResourceAssembler
                .toModel(accountRepository.getOne(accountId)), HttpStatus.OK);
    }

    @GetMapping(value = "/{name}/balance")
    @FollowerRead
    public HttpEntity<String> getBalance(@PathVariable("name") String name) {
        return new ResponseEntity<>(accountRepository.getBalance(name).toPlainString(), HttpStatus.OK);
    }

    @GetMapping(value = "/transfer")
    public HttpEntity<TransferRequest> getTransferRequestForm() {
        TransferRequest form = TransferRequest.builder()
                .setName("alice")
                .setAccountType(AccountType.expense)
                .setAmount(new BigDecimal("100.00").negate())
                .build();
        form.add(linkTo(methodOn(AccountController.class)
                .transfer(form))
                .withRel("transfer"));
        return new ResponseEntity<>(form, HttpStatus.OK);
    }

    @PostMapping(value = "/transfer")
    @TransactionBoundary(retryAttempts = 20, maxBackoff = 45000)
    public HttpEntity<Void> transfer(@RequestBody TransferRequest request) {
        BigDecimal totalBalance = accountRepository.getBalance(request.getName());

        if (totalBalance.add(request.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeBalanceException(
                    "Insufficient funds " + request.getAmount() + " for user " + request.getName());
        }

        accountRepository.updateBalance(request.getName(), request.getAccountType(), request.getAmount());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Horrible, but lets ignore sanity for sake of simplicity in this demo
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, path = "/reset")
    @TransactionHints({
            @TransactionHint(name = "application_name", value = "test")
    })
    public HttpEntity<Void> reset() {
        accountRepository.resetAllBalances(new BigDecimal(500.00));
        return ResponseEntity.ok().build();
    }
}
