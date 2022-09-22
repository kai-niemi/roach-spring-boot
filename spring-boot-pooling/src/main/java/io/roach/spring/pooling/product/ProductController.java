package io.roach.spring.pooling.product;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/product")
public class ProductController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductResourceAssembler productResourceAssembler;

    @Autowired
    private PagedResourcesAssembler<ProductEntity> pagedResourcesAssembler;

    @GetMapping
    public HttpEntity<PagedModel<EntityModel<ProductEntity>>> findAll(
            @PageableDefault(size = 5, direction = Sort.Direction.ASC) Pageable page) {
        PagedModel<EntityModel<ProductEntity>> model = pagedResourcesAssembler
                .toModel(productService.findPage(page), productResourceAssembler);

        model.add(linkTo(methodOn(ProductController.class).findAll(page)).withRel(IanaLinkRelations.FIRST)
                .andAffordance(afford(methodOn(ProductController.class).createProduct(null))));

        return ResponseEntity.ok(model);
    }

    @GetMapping(value = "/{id}")
    public HttpEntity<EntityModel<ProductEntity>> findProduct(@PathVariable("id") UUID id) {
        ProductEntity product = productService.findById(id);
        return new ResponseEntity<>(productResourceAssembler
                .toModel(product), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EntityModel<ProductEntity>> createProduct(@RequestBody ProductEntity product) {
        product = productService.createOne(product);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productResourceAssembler.toModel(product));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") UUID id,
                                           @RequestBody ProductEntity product) {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Tx active");

        product.setId(id);
        productService.update(product);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/open")
    public ResponseEntity<?> openProduct(@PathVariable("id") UUID id) {
        productService.updateStatus(id, true);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/close")
    public ResponseEntity<?> closeProduct(@PathVariable("id") UUID id) {
        productService.updateStatus(id, false);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") UUID id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/poll")
    public ResponseEntity<Void> longPoll(@RequestParam(name = "delay", defaultValue = "60") int delaySeconds) {
        logger.info("Entering wait for {} sec while holding connection", delaySeconds);
        productService.simulateProcessingDelay(delaySeconds);
        logger.info("Exited wait for {} sec", delaySeconds);
        return ResponseEntity.ok().build();
    }
}
