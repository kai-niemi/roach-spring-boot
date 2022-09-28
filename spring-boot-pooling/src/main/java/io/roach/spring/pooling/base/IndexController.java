package io.roach.spring.pooling.base;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.roach.spring.pooling.product.ProductController;
import io.roach.spring.pooling.product.CatalogController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/")
public class IndexController {

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(ProductController.class)
                .findAll(PageRequest.of(0, 16)))
                .withRel("products")
                .withTitle("Paginated collection of products"));

        index.add(linkTo(methodOn(ProductController.class)
                .longPoll(15))
                .withRel("long-poll")
                .withTitle("Claim and hold connection for a time period"));

        index.add(linkTo(methodOn(CatalogController.class)
                .home())
                .withRel("product-catalog")
                .withTitle("Product catalog"));

        index.add(linkTo(methodOn(AdminController.class)
                .index())
                .withRel("admin")
                .withTitle("Admin and metadata"));


        return ResponseEntity.ok(index);
    }
}


