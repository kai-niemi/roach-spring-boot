package io.roach.spring.idempotency.domain.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PagedResourcesAssembler<TransactionEntity> pagedResourcesAssembler;

    @Autowired
    private TransactionEntityAssembler transactionEntityAssembler;

    @GetMapping
    public HttpEntity<PagedModel<EntityModel<TransactionEntity>>> findAll(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page) {
        PagedModel<EntityModel<TransactionEntity>> model = pagedResourcesAssembler
                .toModel(transactionService.findAll(page), transactionEntityAssembler);
        model.add(linkTo(methodOn(getClass())
                .findAll(page)).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/{id}")
    public HttpEntity<EntityModel<TransactionEntity>> findTransaction(@PathVariable("id") Long id) {
        TransactionEntity transaction = transactionService.findById(id);
        return ResponseEntity.ok(transactionEntityAssembler.toModel(transaction));
    }


    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable("id") Long id,
                                               @RequestBody TransactionEntity transaction) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Tx active");

        transaction.setId(id);
        transactionService.update(transaction);

        return ResponseEntity.noContent().build();
    }
}
