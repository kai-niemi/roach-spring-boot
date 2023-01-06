package io.roach.spring.order.product;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/order-service/product")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductModelResourceAssembler productModelResourceAssembler;

    @Autowired
    private PagedResourcesAssembler<Product> pagedResourcesAssembler;

    @GetMapping
    public HttpEntity<PagedModel<EntityModel<Product>>> findAll(@PageableDefault(size = 5) Pageable page) {
        PagedModel<EntityModel<Product>> model = pagedResourcesAssembler
                .toModel(productService.findProductsPage(page), productModelResourceAssembler);
        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/{id}")
    public HttpEntity<EntityModel<Product>> getProduct(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(productModelResourceAssembler
                .toModel(productService.getProductById(id)));
    }
}
