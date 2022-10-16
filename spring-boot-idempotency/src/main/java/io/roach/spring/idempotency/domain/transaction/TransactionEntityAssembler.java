package io.roach.spring.idempotency.domain.transaction;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.roach.spring.idempotency.domain.account.AccountController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TransactionEntityAssembler implements SimpleRepresentationModelAssembler<TransactionEntity> {
    @Override
    public void addLinks(EntityModel<TransactionEntity> resource) {
        TransactionEntity entity = resource.getContent();
        resource.add(
                linkTo(methodOn(TransactionController.class).findTransaction(entity.getId()))
                        .withSelfRel(),
                linkTo(methodOn(AccountController.class).findAccount(entity.getAccount().getId()))
                        .withRel("account")
                        .andAffordance(afford(methodOn(TransactionController.class)
                                .updateTransaction(entity.getId(), null))));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<TransactionEntity>> resources) {
    }
}
