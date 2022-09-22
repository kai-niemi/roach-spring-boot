package io.roach.spring.pooling.product;

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
public class ProductResourceAssembler
        implements SimpleRepresentationModelAssembler<ProductEntity> {

    @Override
    public void addLinks(EntityModel<ProductEntity> resource) {
        ProductEntity entity = resource.getContent();
        UUID id = entity.getId();
        if (entity.isForSale()) {
            resource.add(linkTo(methodOn(ProductController.class)
                    .closeProduct(id)
            ).withRel("close"));
        } else {
            resource.add(linkTo(methodOn(ProductController.class)
                    .openProduct(id)
            ).withRel("open"));
        }
        resource.add(
                linkTo(methodOn(ProductController.class).findProduct(id)).withSelfRel()
                        .andAffordance(afford(methodOn(ProductController.class).updateProduct(id, null)))
                        .andAffordance(afford(methodOn(ProductController.class).deleteProduct(id))),
                linkTo(methodOn(ProductController.class).findAll(PageRequest.of(0, 16)))
                        .withRel("products"));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<ProductEntity>> resources) {
    }
}
