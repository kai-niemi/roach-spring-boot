package io.roach.spring.pagination.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.roach.spring.pagination.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
}
