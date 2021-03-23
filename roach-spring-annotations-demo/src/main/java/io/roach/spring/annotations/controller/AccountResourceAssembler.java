package io.roach.spring.annotations.controller;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import io.roach.spring.annotations.domain.Account;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountResourceAssembler
        extends RepresentationModelAssemblerSupport<Account, AccountModel> {

    public AccountResourceAssembler() {
        super(RootController.class, AccountModel.class);
    }

    @Override
    public AccountModel toModel(Account entity) {
        AccountModel resource = new AccountModel();

        resource.setName(entity.getName());
        resource.setType(entity.getType());
        resource.setBalance(entity.getBalance());

        resource.add(linkTo(methodOn(AccountController.class)
                .getAccount(entity.getId())
        ).withRel(IanaLinkRelations.SELF));

        return resource;
    }
}
