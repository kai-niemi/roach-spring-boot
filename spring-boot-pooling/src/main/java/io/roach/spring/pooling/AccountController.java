package io.roach.spring.pooling;

import java.util.UUID;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                .toModel(accountService.findPage(page), accountResourceAssembler);

        model.add(linkTo(methodOn(AccountController.class).findAll(page)).withRel(IanaLinkRelations.FIRST)
                .andAffordance(afford(methodOn(AccountController.class).createAccount(null))));

        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/{id}")
    public HttpEntity<EntityModel<AccountEntity>> findAccount(@PathVariable("id") UUID id) {
        AccountEntity account = accountService.findById(id);
        return new ResponseEntity<>(accountResourceAssembler
                .toModel(account), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EntityModel<AccountEntity>> createAccount(@RequestBody AccountEntity account) {
        account = accountService.createOne(account);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountResourceAssembler.toModel(account));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable("id") UUID id,
                                           @RequestBody AccountEntity account) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Tx active");

        account.setId(id);
        accountService.update(account);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/open")
    public ResponseEntity<?> openAccount(@PathVariable("id") UUID id) {
        accountService.updateStatus(id, false);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/close")
    public ResponseEntity<?> closeAccount(@PathVariable("id") UUID id) {
        accountService.updateStatus(id, true);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") UUID id) {
        accountService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/poll")
    public ResponseEntity<Void> longPoll(@RequestParam(name = "delay", defaultValue = "60") int delaySeconds) {
        logger.info("Entering wait for {} sec while holding connection", delaySeconds);
        accountService.simulateProcessingDelay(delaySeconds);
        logger.info("Exited wait for {} sec", delaySeconds);
        return ResponseEntity.ok().build();
    }
}
