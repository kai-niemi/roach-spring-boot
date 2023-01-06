package io.roach.spring.order.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.roach.spring.order.LinkRelations;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelResourceAssembler implements SimpleRepresentationModelAssembler<Product> {
    @Override
    public void addLinks(EntityModel<Product> resource) {
        Product product = resource.getContent();
        resource.add(linkTo(methodOn(ProductController.class)
                .getProduct(product.getId())).withSelfRel()
        );

        resource.add(
                Link.of(ServletUriComponentsBuilder.fromHttpUrl("http://localhost:8090/catalog-service")
                                .pathSegment("product")
                                .pathSegment("{id}")
                                .buildAndExpand(product.getId())
                                .toUriString())
                        .withRel(LinkRelations.FOREIGN_PRODUCT_REL)
                        .withTitle("Foreign product (SoR)"));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<Product>> resources) {
        resources.add(linkTo(methodOn(ProductController.class)
                .findAll(PageRequest.of(0, 5)))
                .withRel(LinkRelations.PRODUCTS_REL));
    }

}
