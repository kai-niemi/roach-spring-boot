package io.roach.spring.catalog.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.roach.spring.catalog.LinkRelations;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductResourceAssembler implements SimpleRepresentationModelAssembler<Product> {
    @Override
    public void addLinks(EntityModel<Product> resource) {
        Product product = resource.getContent();
        resource.add(linkTo(methodOn(ProductController.class)
                .getProduct(product.getId())).withSelfRel()
                .andAffordance(afford(methodOn(ProductController.class)
                        .updateProduct(product.getId(), product)))
                .andAffordance(afford(methodOn(ProductController.class)
                        .deleteProduct(product.getId())))
        );
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<Product>> resources) {
        resources.add(linkTo(methodOn(ProductController.class)
                .findAll(PageRequest.of(0, 5)))
                .withRel(LinkRelations.PRODUCTS_REL));
    }

}
