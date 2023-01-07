package io.roach.spring.catalog.scheduler;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.roach.spring.catalog.LinkRelations;
import io.roach.spring.catalog.product.Product;
import io.roach.spring.catalog.product.ProductService;
import io.roach.spring.catalog.util.RandomUtils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/catalog-service/scheduler")
public class SchedulingController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private static boolean enableInserts;

    private static boolean enableUpdates;

    private static boolean enableDeletes;

    @Autowired
    private ProductService productService;

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter productsCreated;

    private Counter productsUpdated;

    private Counter productsDeleted;

    @PostConstruct
    public void init() {
        this.productsCreated = meterRegistry.counter("roach.products.created");
        this.productsUpdated = meterRegistry.counter("roach.products.updated");
        this.productsDeleted = meterRegistry.counter("roach.products.deleted");
    }

    @GetMapping
    public ResponseEntity<SchedulingModel> getShedulingStatus() {
        return ResponseEntity.ok().body(toModel());
    }

    @GetMapping("/toggle-insert") // Anti-pattern!
    @PostMapping("/toggle-insert")
    public ResponseEntity<SchedulingModel> toggleInserts() {
        SchedulingController.enableInserts = !SchedulingController.enableInserts;
        return ResponseEntity.ok().body(toModel());
    }

    @GetMapping("/toggle-update") // Anti-pattern!
    @PostMapping("/toggle-update")
    public ResponseEntity<SchedulingModel> toggleUpdates() {
        SchedulingController.enableUpdates = !SchedulingController.enableUpdates;
        return ResponseEntity.ok().body(toModel());
    }

    @GetMapping("/toggle-delete") // Anti-pattern!
    @PostMapping("/toggle-delete")
    public ResponseEntity<SchedulingModel> toggleDeletes() {
        SchedulingController.enableDeletes = !SchedulingController.enableDeletes;
        return ResponseEntity.ok().body(toModel());
    }

    private SchedulingModel toModel() {
        SchedulingModel model = new SchedulingModel();
        model.setInsertsEnabled(SchedulingController.enableInserts);
        model.setUpdatesEnabled(SchedulingController.enableUpdates);
        model.setDeletesEnabled(SchedulingController.enableDeletes);
        model.setProductsCreated((int) productsCreated.count());
        model.setProductsUpdated((int) productsUpdated.count());
        model.setProductsDeleted((int) productsDeleted.count());
        model.add(linkTo(methodOn(getClass())
                .toggleInserts())
                .withRel(LinkRelations.TOGGLE_REL)
                .andAffordance(afford(methodOn(getClass()).toggleInserts()))
                .withTitle("Toggle periodic product INSERTs"));
        model.add(linkTo(methodOn(getClass())
                .toggleUpdates())
                .withRel(LinkRelations.TOGGLE_REL)
                .andAffordance(afford(methodOn(getClass()).toggleUpdates()))
                .withTitle("Toggle periodic product UPDATEs"));
        model.add(linkTo(methodOn(getClass())
                .toggleDeletes())
                .withRel(LinkRelations.TOGGLE_REL)
                .andAffordance(afford(methodOn(getClass()).toggleDeletes()))
                .withTitle("Toggle periodic product DELETEs"));
        return model;
    }

    @Scheduled(cron = "*/15 * * * * ?")
    public void createProducts() {
        if (!enableInserts) {
            return;
        }

        int n = random.nextInt(8, 32);

        productService.createProductBatch(n, () -> Product.builder()
                .withInventory(RandomUtils.random.nextInt(100, 500))
                .withSku(UUID.randomUUID().toString())
                .withPrice(RandomUtils.randomBigDecimal(15, 550))
                .withCurrency("USD")
                .withName(RandomUtils.randomString(16))
                .build());

        productsCreated.increment(n);

        logger.info("Created {} products", (int) productsCreated.count());
    }

    @Scheduled(cron = "0/15 * * * * ?")
    public void updateProducts() {
        if (!enableInserts) {
            return;
        }

        Page<Product> products = productService.findProductsPage(PageRequest.ofSize(32));
        while (products.hasContent()) {
            products.forEach(product -> {
                if (random.nextDouble() > .8) {
                    product.setPrice(RandomUtils.randomBigDecimal(10, 150));
                    product.incInventory(RandomUtils.random.nextInt(1, 5));
                    productService.update(product);
                    productsUpdated.increment();
                }
            });
            if (products.hasNext()) {
                products = productService.findProductsPage(products.nextPageable());
            } else {
                break;
            }
        }

        logger.info("Updated {} products", (int) productsUpdated.count());
    }

    @Scheduled(cron = "2 * * * * ?")
    public void deleteProducts() {
        if (!enableInserts) {
            return;
        }
        Page<Product> products = productService.findProductsPage(PageRequest.ofSize(32));
        while (products.hasContent()) {
            products.forEach(product -> {
                if (random.nextDouble() > .95) {
                    productService.delete(product.getId());
                    productsDeleted.increment();
                }
            });
            if (products.hasNext()) {
                products = productService.findProductsPage(products.nextPageable());
            } else {
                break;
            }
        }

        logger.info("Deleted {} products", (int) productsDeleted.count());
    }
}
