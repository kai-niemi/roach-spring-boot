package io.roach.spring.batch.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.roach.spring.batch.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
