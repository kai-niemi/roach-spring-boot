package io.roach.spring.pooling.product;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProductEntity createOne(ProductEntity product) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        return productRepository.save(product);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ProductEntity> createAll(Iterable<ProductEntity> products) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        return productRepository.saveAll(products);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProductEntity findById(UUID id) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        return productRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("No such product: " + id));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Page<ProductEntity> findPage(Pageable pageable) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        Assert.isTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly(), "Not rox");
        return productRepository.findAll(pageable);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(ProductEntity product) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
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
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        ProductEntity productProxy = productRepository.getReferenceById(id);
        productProxy.setForSale(forSale);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(UUID id) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");
        productRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAll() {
        productRepository.deleteAllInBatch();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void simulateProcessingDelay(int delay) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx");

        productRepository.findAll(PageRequest.of(0,16)).getContent().forEach(productEntity -> {
            productEntity.getName();
        });

        try {
            Thread.sleep(delay*1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
