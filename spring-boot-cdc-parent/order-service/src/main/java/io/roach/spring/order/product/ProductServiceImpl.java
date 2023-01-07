package io.roach.spring.order.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

import io.roach.spring.order.changefeed.Envelope;
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
    public void onProductChangeEvent(Envelope<ProductPayload, UUID> envelope) {
        ProductPayload afterPayload = envelope.getAfterPayload();
        if (afterPayload == null) {
            logger.warn("Empty payload: {}", envelope);
            return;
        }

        switch (envelope.getOperation()) {
            case insert:
            case update:
                Product proxy = productRepository.findById(afterPayload.getId()).orElseGet(Product::new);
                if (proxy.isNew()) {
                    logger.debug("Create product with ID [{}]: {}", afterPayload.getId(), proxy);
                    proxy.setId(afterPayload.getId());
                } else {
                    logger.debug("Update product with ID [{}]: {}", afterPayload.getId(), proxy);
                }

                Money m = Money.of(afterPayload.getPrice());
                proxy.setPrice(m.getAmount());
                proxy.setCurrency(m.getCurrency().getCurrencyCode());
                proxy.setSku(afterPayload.getSku());
                proxy.setName(afterPayload.getName());
                proxy.setDescription(afterPayload.getDescription());
                proxy.setInventory(afterPayload.getInventory());

                productRepository.save(proxy);
                break;
            case delete: {
                logger.debug("Delete product with ID [{}]", afterPayload.getId());
                productRepository.deleteById(afterPayload.getId());
                break;
            }
            default:
                throw new IllegalStateException("Unknown operation: " + envelope.getOperation());
        }
    }
}
