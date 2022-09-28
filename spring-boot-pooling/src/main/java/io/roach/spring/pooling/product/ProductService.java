package io.roach.spring.pooling.product;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    private static void assertTx(boolean readOnly) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        if (readOnly) {
            Assert.isTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Not read-only");
        } else {
            Assert.isTrue(!TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Read-only");
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public Long sumTotalInventory() {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        // Since were using explicit tx
        jdbcTemplate.execute(
                "SET TRANSACTION AS OF SYSTEM TIME follower_read_timestamp()");
        return productRepository.sumTotalInventoryJpql();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProductEntity createOne(ProductEntity product) {
        assertTx(false);
        return productRepository.save(product);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ProductEntity> createAll(Iterable<ProductEntity> products) {
        assertTx(false);
        return productRepository.saveAll(products);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public ProductEntity findById(UUID id) {
        assertTx(true);
        return productRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("No such product: " + id));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Page<ProductEntity> findPage(Pageable pageable) {
        assertTx(true);
        return productRepository.findAll(pageable);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(ProductEntity product) {
        assertTx(false);
        ProductEntity productProxy = productRepository.getReferenceById(product.getId());
        productProxy.setName(product.getName());
        productProxy.setDescription(product.getDescription());
        productProxy.setInventory(product.getInventory());
        productProxy.setPrice(product.getPrice());
        productProxy.setCurrency(product.getCurrency());
        productProxy.setForSale(product.isForSale());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(UUID id, boolean forSale) {
        assertTx(false);
        ProductEntity productProxy = productRepository.getReferenceById(id);
        productProxy.setForSale(forSale);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(UUID id) {
        assertTx(false);
        productRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAll() {
        assertTx(false);
        productRepository.deleteAllInBatch();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void simulateProcessingDelay(int delay) {
        assertTx(true);

        productRepository.findAll(PageRequest.of(0, 16)).getContent().forEach(productEntity -> {
            productEntity.getName();
        });

        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
