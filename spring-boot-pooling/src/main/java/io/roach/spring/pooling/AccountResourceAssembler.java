package io.roach.spring.pooling;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountResourceAssembler
        implements SimpleRepresentationModelAssembler<AccountEntity> {

    @Override
    public void addLinks(EntityModel<AccountEntity> resource) {
        AccountEntity account = resource.getContent();
        UUID id = account.getId();
        if (account.isClosed()) {
            resource.add(linkTo(methodOn(AccountController.class)
                    .openAccount(id)
            ).withRel("open"));
        } else {
            resource.add(linkTo(methodOn(AccountController.class)
                    .closeAccount(id)
            ).withRel("close"));
        }
        resource.add(
                linkTo(methodOn(AccountController.class).findAccount(id)).withSelfRel()
                        .andAffordance(afford(methodOn(AccountController.class).updateAccount(id, null)))
                        .andAffordance(afford(methodOn(AccountController.class).deleteAccount(id))),
                linkTo(methodOn(AccountController.class).findAll(PageRequest.of(0, 16)))
                        .withRel("accounts"));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<AccountEntity>> resources) {
    }
}
