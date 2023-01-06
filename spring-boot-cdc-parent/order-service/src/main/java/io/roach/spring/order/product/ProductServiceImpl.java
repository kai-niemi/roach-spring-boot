package io.roach.spring.order.product;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        Product proxy = productRepository.findById(afterPayload.getId()).orElseGet(Product::new);

        logger.debug("Find product with ID [{}] for [{}]: {}", afterPayload.getId(), envelope.getOperation(),  proxy);

        switch (envelope.getOperation()) {
            case insert:
            case update:
                Money m = Money.of(afterPayload.getPrice());
                if (proxy.isNew()) {
                    proxy.setId(afterPayload.getId());
                    proxy.setCreatedAt(afterPayload.getCreatedAt());
                    proxy.setCreatedBy(afterPayload.getCreatedBy());
                } else {
                    proxy.setLastModifiedBy(afterPayload.getLastModifiedBy());
                    proxy.setLastModifiedAt(afterPayload.getLastModifiedAt());
                }

                proxy.setPrice(m.getAmount());
                proxy.setCurrency(m.getCurrency().getCurrencyCode());
                proxy.setSku(afterPayload.getSku());
                proxy.setName(afterPayload.getName());
                proxy.setDescription(afterPayload.getDescription());
                proxy.setInventory(afterPayload.getInventory());

                productRepository.save(proxy);
                break;
            case delete: {
                productRepository.deleteById(afterPayload.getId());
                break;
            }
            default:
                throw new IllegalStateException("Unknown operation: " + envelope.getOperation());
        }
    }
}
