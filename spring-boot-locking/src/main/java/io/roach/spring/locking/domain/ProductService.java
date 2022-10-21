package io.roach.spring.locking.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
                                                                                     
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProductWithOptimisticLock(UUID id, int qty) {
        Optional<Product> product = productRepository.getByIdOptimisticLock(id);
        int newQuantity = product.orElseThrow(() -> new ObjectRetrievalFailureException(Product.class, id))
                .addInventoryQuantity(qty);
        if (newQuantity < 0) {
            throw new BusinessException("Negative inventory for " + id);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProductWithPessimisticLock(UUID id, int qty) {
        Product product = productRepository.getByIdWithPessimisticLock(id);
        int newQuantity = product.addInventoryQuantity(qty);
        if (newQuantity < 0) {
            throw new BusinessException("Negative inventory for " + id);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProduct(UUID id, int qty) {
        productRepository.updateInventory(id, qty);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Page<Product> findAll(Pageable page) {
        return productRepository.findAll(page);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product findOneProduct() {
        return productRepository.findAll(
                        PageRequest.of(1, 1))
                .iterator()
                .next();
    }
}
