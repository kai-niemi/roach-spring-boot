package io.roach.spring.idempotency.domain.account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/account")
public class AccountController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountResourceAssembler accountResourceAssembler;

    @Autowired
    private PagedResourcesAssembler<AccountEntity> pagedResourcesAssembler;

    @GetMapping
    public HttpEntity<PagedModel<EntityModel<AccountEntity>>> findAll(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page) {
        PagedModel<EntityModel<AccountEntity>> model = pagedResourcesAssembler
                .toModel(accountService.findAll(page), accountResourceAssembler);

        model.add(linkTo(methodOn(AccountController.class).findAll(page)).withRel(IanaLinkRelations.FIRST)
                .andAffordance(afford(methodOn(AccountController.class)
                        .createAccount(null)))
                .andAffordance(afford(methodOn(AccountController.class)
                        .createAccounts(500, 16)))
        );

        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/{id}")
    public HttpEntity<EntityModel<AccountEntity>> findAccount(@PathVariable("id") Long id) {
        AccountEntity account = accountService.findById(id);
        return new ResponseEntity<>(accountResourceAssembler
                .toModel(account), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EntityModel<AccountEntity>> createAccount(@RequestBody AccountEntity account) {
        account = accountService.create(account);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountResourceAssembler.toModel(account));
    }

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @PostMapping("/batch")
    public ResponseEntity<EntityModel<AccountEntity>> createAccounts(
            @RequestParam(value = "numAccounts", defaultValue = "500") int numAccounts,
            @RequestParam(value = "batchSize", defaultValue = "16") int batchSize) {

        int n = numAccounts;
        while (n > 0) {
            List<AccountEntity> batch = new ArrayList<>();
            LongStream.rangeClosed(1, batchSize).forEach(value -> {
                AccountEntity instance = new AccountEntity();
                instance.setBalance(Math.floor(RANDOM.nextDouble(100, 1500)));
                instance.setCreatedAt(LocalDateTime.now());
                batch.add(instance);
            });
            accountService.create(batch);
            n -= batchSize;
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable("id") Long id,
                                           @RequestBody AccountEntity account) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Tx active");

        account.setId(id);
        accountService.update(account);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id) {
        accountService.delete(id);
        return ResponseEntity.ok().build();
    }
}
