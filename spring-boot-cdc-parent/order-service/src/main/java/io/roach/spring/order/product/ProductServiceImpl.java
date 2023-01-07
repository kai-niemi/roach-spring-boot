package io.roach.spring.order.product;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.roach.spring.order.changefeed.Payload;
import io.roach.spring.order.util.Money;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ProductServiceImpl implements ProductService, ChangeEventListener {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> findProductsPage(Pageable page) {
        return productRepository.findAll(page);
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchProductException(id));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductChangeEvent(Payload<ProductEvent, UUID> payload) {
        ProductEvent beforeEvent = payload.getBefore();
        ProductEvent afterEvent = payload.getAfter();

        // Deletes are a bit special
        if (beforeEvent == null && afterEvent == null) {
            Payload.Metadata metadata = payload.getMetadata();
            metadata.getKey().forEach(key -> {
                UUID id = UUID.fromString(key);
                logger.debug("Delete product with ID [{}]", id);
                productRepository.deleteById(id);
            });
            return;
        }

        switch (payload.getOperation()) {
            case insert:
            case update:
                Product proxy = productRepository.findById(afterEvent.getId()).orElseGet(Product::new);
                if (proxy.isNew()) {
                    logger.debug("Create product with ID [{}]: {}", afterEvent.getId(), proxy);
                    proxy.setId(afterEvent.getId());
                } else {
                    logger.debug("Update product with ID [{}]: {}", afterEvent.getId(), proxy);
                }

                Money m = Money.of(afterEvent.getPrice());
                proxy.setPrice(m.getAmount());
                proxy.setCurrency(m.getCurrency().getCurrencyCode());
                proxy.setSku(afterEvent.getSku());
                proxy.setName(afterEvent.getName());
                proxy.setDescription(afterEvent.getDescription());
                proxy.setInventory(afterEvent.getInventory());

                productRepository.save(proxy);
                break;
            default:
                throw new IllegalStateException("Unknown operation: " + payload.getOperation());
        }
    }
}
