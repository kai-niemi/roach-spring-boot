package io.roach.spring.pagination.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class IndexController {
    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(ProductController.class)
                .listProducts(PageRequest.of(0, 5)))
                .withRel(LinkRels.PRODUCTS_REL)
                .withTitle("Product resource details")
        );

        index.add(linkTo(methodOn(CustomerController.class)
                .listCustomers(PageRequest.of(0, 5)))
                .withRel(LinkRels.CUSTOMERS_REL)
                .withTitle("Customer resource details")
        );

        index.add(linkTo(methodOn(OrderController.class)
                .listOrders(PageRequest.of(0, 5)))
                .withRel(LinkRels.ORDERS_REL)
                .withTitle("Order resource details")
        );

        return ResponseEntity.ok(index);
    }
}
