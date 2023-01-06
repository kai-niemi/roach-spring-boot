package io.roach.spring.catalog.product;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.roach.spring.catalog.common.NoSuchProductException;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createProductBatch(int batchSize, Supplier<Product> factory) {
        IntStream.rangeClosed(1, batchSize).forEach(v -> {
            String ref = "p-" + productRepository.nextSeqNumber();
            Product product = factory.get();
            product.setSku(ref);
            productRepository.save(product);
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(Product product) {
        Product productProxy = productRepository.getReferenceById(product.getId());
        productProxy.setCurrency(product.getCurrency());
        productProxy.setPrice(product.getPrice());
        productProxy.setInventory(product.getInventory());
        productProxy.setDescription(product.getDescription());
    }

    @Override
    public Page<Product> findProductsPage(Pageable page) {
        return productRepository.findAll(page);
    }

    @Override
    public Product getProductBySku(String sku) {
        return productRepository.getBySku(sku)
                .orElseThrow(() -> new NoSuchProductException(sku));
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchProductException(id));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }
}
