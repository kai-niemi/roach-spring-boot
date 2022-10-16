package io.roach.spring.idempotency.domain.account;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountResourceAssembler implements SimpleRepresentationModelAssembler<AccountEntity> {

    @Override
    public void addLinks(EntityModel<AccountEntity> resource) {
        AccountEntity entity = resource.getContent();
        Long id = entity.getId();
        resource.add(linkTo(methodOn(AccountController.class).findAccount(id)).withSelfRel()
                .andAffordance(afford(methodOn(AccountController.class).updateAccount(id, null)))
                .andAffordance(afford(methodOn(AccountController.class).deleteAccount(id))));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<AccountEntity>> resources) {
    }
}
