package io.roach.spring.order.changefeed;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.roach.spring.order.product.ChangeEventListener;
import io.roach.spring.order.product.ProductEnvelope;

@RestController
@RequestMapping(value = "/order-service/cdc")
public class ChangeFeedController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("prettyObjectMapper")
    private ObjectMapper prettyObjectMapper;

    @Autowired
    private ChangeEventListener domainEventListener;

    @PostMapping(consumes = {MediaType.ALL_VALUE})
    public ResponseEntity<?> onChangeFeedEvent(@RequestBody String body) {
        try {
            String prettyJson = prettyObjectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(prettyObjectMapper.readTree(body));
            logger.debug("onChangeFeedEvent ({}) body:\n{}", counter.incrementAndGet(), prettyJson);

            // We could use the 'event_table' field to map against change event types, here we only have one type
            ProductEnvelope envelope = objectMapper.readerFor(ProductEnvelope.class).readValue(body);
            AbstractEnvelope.Metadata metadata = envelope.getMetadata();
            if (metadata != null) {
                metadata.getResolvedTimestamp().ifPresent(logicalTimestamp -> {
                    logger.debug("Resolved timestamp: {}", logicalTimestamp);
                });
            }
            envelope.getPayloads().forEach(e -> domainEventListener.onProductChangeEvent(e));
        } catch (JsonProcessingException e) {
            logger.error("", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.toString());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok().body("CDC webhook endpoint only supports POST");
    }
}
