package io.roach.spring.pagination.web;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.annotations.TransactionBoundary;
import io.roach.spring.pagination.domain.Customer;
import io.roach.spring.pagination.repository.CustomerRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/customer")
public class CustomerController {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PagedResourcesAssembler<Customer> pagedResourcesAssembler;

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(getClass())
                .listCustomers(PageRequest.of(0, 5)))
                .withRel(LinkRels.CUSTOMERS_REL));

        return ResponseEntity.ok(index);
    }

    @GetMapping("/")
    @TransactionBoundary(followerRead = true)
    public HttpEntity<PagedModel<CustomerModel>> listCustomers(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page) {
        return ResponseEntity
                .ok(pagedResourcesAssembler.toModel(
                        customerRepository.findAll(page), customerModelAssembler(page)));
    }

    @GetMapping(value = "/{id}")
    @TransactionBoundary(readOnly = true)
    public HttpEntity<CustomerModel> getCustomer(@PathVariable("id") UUID accountId) {
        return ResponseEntity.ok(
                customerModelAssembler(PageRequest.ofSize(5))
                        .toModel(customerRepository.getById(accountId)));
    }

    private RepresentationModelAssembler<Customer, CustomerModel> customerModelAssembler(Pageable page) {
        return (entity) -> {
            CustomerModel model = new CustomerModel();
            model.setUserName(entity.getUserName());
            model.setFullName(entity.getFullName());

            model.add(linkTo(methodOn(CustomerController.class)
                    .getCustomer(entity.getId())
            ).withRel(IanaLinkRelations.SELF));

            model.add(linkTo(methodOn(OrderController.class)
                    .listOrdersByCustomer(page, entity.getId())
            ).withRel(LinkRels.ORDERS_REL));

            return model;
        };
    }
}
