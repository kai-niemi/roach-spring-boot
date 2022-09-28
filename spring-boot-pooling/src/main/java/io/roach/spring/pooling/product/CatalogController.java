package io.roach.spring.pooling.product;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static io.roach.spring.pooling.util.TimeUtils.millisecondsToDisplayString;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/catalog")
public class CatalogController {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> home() {
        RepresentationModel<?> index = new RepresentationModel<>();

        index.add(linkTo(methodOn(getClass())
                .getTotalInventory())
                .withRel("catalog-inventory")
                .withTitle("Total product inventory"));

        index.add(linkTo(methodOn(getClass())
                .submitForm(null))
                .withRel("catalog-builder")
                .withTitle("Product catalog builder"));

        return ResponseEntity.ok(index);
    }

    @GetMapping("/inventory")
    public ResponseEntity<Long> getTotalInventory() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl
                        .maxAge(30, TimeUnit.SECONDS))
                .body(productService.sumTotalInventory());
    }

    @GetMapping("/form")
    public ResponseEntity<CatalogForm> getFormTemplate(@RequestParam Map<String, String> requestParams) {
        CatalogForm form = new CatalogForm();
        form.setBatchSize(128);
        form.setNumProducts(5_000);
        form.setDescription("Create product catalog");
        return ResponseEntity.ok(form);
    }

    @PostMapping("/form")
    public ResponseEntity<StreamingResponseBody> submitForm(@RequestBody CatalogForm form) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(outputStream -> createCatalog(form, outputStream));
    }

    private void createCatalog(CatalogForm form, OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream, true);
        pw.printf("Creating %,d products with batch size %d\n",
                form.getNumProducts(), form.getBatchSize());

        final long startTime = System.nanoTime();

        try {
            int batchSize = form.getBatchSize();
            int numProducts = 0;
            while (numProducts < form.getNumProducts()) {
                batchSize = Math.min(Math.abs(form.getNumProducts() - numProducts), batchSize);
                numProducts += batchSize;

                productService.createAll(createBatch(batchSize));

                pw.println(numProducts);
            }
        } finally {
            pw.printf("[Done] %,d products with batch size %d in %s\n",
                    form.getNumProducts(),
                    form.getBatchSize(),
                    millisecondsToDisplayString(Duration.ofNanos(System.nanoTime() - startTime).toMillis()));
        }
    }

    private List<ProductEntity> createBatch(int size) {
        List<ProductEntity> batch = new ArrayList<>();
        LongStream.rangeClosed(1, size).forEach(value -> batch.add(createInstance()));
        return batch;
    }

    private ProductEntity createInstance() {
        ProductEntity instance = new ProductEntity();
        instance.setPrice(BigDecimal.valueOf(RANDOM.nextDouble(100.00, 5000.00)));
        instance.setCurrency("USD");
        instance.setName(randomName(32));
        instance.setDescription(randomName(64));
        instance.setInventory(RANDOM.nextInt(10, 500));
        instance.setSku(UUID.randomUUID().toString());
        instance.setForSale(true);
        return instance;
    }

    private static String randomName(int min) {
        byte[] buffer = new byte[min];
        RANDOM.nextBytes(buffer);
        return ENCODER.encodeToString(buffer);
    }
}
