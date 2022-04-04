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
import io.roach.spring.pagination.domain.Product;
import io.roach.spring.pagination.repository.ProductRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/product")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PagedResourcesAssembler<Product> pagedResourcesAssembler;

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> index() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(getClass())
                .listProducts(PageRequest.of(0, 5)))
                .withRel(LinkRels.PRODUCTS_REL));

        return ResponseEntity.ok(index);
    }

    @GetMapping("/")
    @TransactionBoundary(followerRead = true)
    public HttpEntity<PagedModel<ProductModel>> listProducts(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page) {
        return ResponseEntity
                .ok(pagedResourcesAssembler.toModel(
                        productRepository.findAll(page), productModelAssembler(page)));
    }

    @GetMapping(value = "/{id}")
    @TransactionBoundary(readOnly = true)
    public HttpEntity<ProductModel> getProduct(@PathVariable("id") UUID accountId) {
        return ResponseEntity.ok(productModelAssembler(PageRequest.ofSize(5))
                .toModel(productRepository.getById(accountId)));
    }

    private RepresentationModelAssembler<Product, ProductModel> productModelAssembler(Pageable page) {
        return (entity) -> {
            ProductModel model = new ProductModel();
            model.setName(entity.getName());
            model.setPrice(entity.getPrice());
            model.setSku(entity.getSku());
            model.setInventory(entity.getInventory());

            model.add(linkTo(methodOn(ProductController.class)
                    .getProduct(entity.getId())
            ).withRel(IanaLinkRelations.SELF));

            model.add(linkTo(methodOn(OrderController.class)
                    .listOrdersByProduct(page, entity.getId())
            ).withRel(LinkRels.ORDERS_REL));

            return model;
        };
    }
}
