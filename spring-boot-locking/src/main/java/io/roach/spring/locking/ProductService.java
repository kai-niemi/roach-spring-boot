package io.roach.spring.locking;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;

import io.roach.spring.annotations.TransactionBoundary;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
                                                                                     
    @TransactionBoundary(maxBackoff = 15000)
    public void updateProductWithOptimisticLock(UUID id, int qty) {
        Optional<Product> product = productRepository.getByIdOptimisticLock(id);
        int newQuantity = product.orElseThrow(() -> new ObjectRetrievalFailureException(Product.class, id))
                .addInventoryQuantity(qty);
        if (newQuantity < 0) {
            throw new BusinessException("Negative inventory for " + id);
        }
    }

    @TransactionBoundary(maxBackoff = 15000)
    public void updateProductWithPessimisticLock(UUID id, int qty) {
        Product product = productRepository.getByIdWithPessimisticLock(id);
        int newQuantity = product.addInventoryQuantity(qty);
        if (newQuantity < 0) {
            throw new BusinessException("Negative inventory for " + id);
        }
    }

    @TransactionBoundary(maxBackoff = 15000)
    public void updateProduct(UUID id, int qty) {
        productRepository.updateInventory(id, qty);
    }

    @TransactionBoundary
    public Page<Product> findAll(Pageable page) {
        return productRepository.findAll(page);
    }

    @TransactionBoundary
    public Product findOneProduct() {
        return productRepository.findAll(
                        PageRequest.of(1, 1))
                .iterator()
                .next();
    }
}
