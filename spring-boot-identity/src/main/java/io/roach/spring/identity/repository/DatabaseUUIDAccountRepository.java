package io.roach.spring.identity.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.roach.spring.identity.model.DatabaseUUIDAccountEntity;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface DatabaseUUIDAccountRepository extends JpaRepository<DatabaseUUIDAccountEntity, UUID> {
}

