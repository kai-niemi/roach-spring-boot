package io.roach.spring.catalog.product;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.roach.spring.catalog.util.RandomUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/catalog-service/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductResourceAssembler productResourceAssembler;

    @Autowired
    private PagedResourcesAssembler<Product> pagedResourcesAssembler;

    @GetMapping
    public HttpEntity<PagedModel<EntityModel<Product>>> findAll(@PageableDefault(size = 5) Pageable page) {
        Page<Product> entities = productService.findProductsPage(page);
        return ResponseEntity.ok(pagedResourcesAssembler
                .toModel(entities, productResourceAssembler));
    }

    @GetMapping("/form")
    public ResponseEntity<EntityModel<Product>> getFormTemplate(@RequestParam Map<String, String> requestParams) {
        Product template = Product.builder()
                .withInventory(RandomUtils.random.nextInt(10, 100))
                .withSku(UUID.randomUUID().toString())
                .withPrice(RandomUtils.randomBigDecimal(5, 50))
                .withCurrency(requestParams.getOrDefault("currency", "USD"))
                .withName(RandomUtils.randomString(16))
                .build();

        EntityModel<Product> form = EntityModel.of(template);
        form.add(linkTo(methodOn(getClass()).getFormTemplate(Collections.emptyMap()))
                .withSelfRel());

        return ResponseEntity.ok(form);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Product>> createProduct(@RequestBody Product product) {
        product = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productResourceAssembler.toModel(product));
    }

    @GetMapping(value = "/{id}")
    public HttpEntity<EntityModel<Product>> getProduct(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(productResourceAssembler
                .toModel(productService.getProductById(id)));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") UUID id,
                                           @RequestBody Product product) {
        product.setId(id);
        productService.update(product);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") UUID id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
