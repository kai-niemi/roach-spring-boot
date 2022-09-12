package io.roach.spring.identity;

import java.util.UUID;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.identity.model.DatabaseUUIDAccountEntity;

@ActiveProfiles({"test"})
public class DatabaseUUIDAccountTest extends AbstractAccountTest<DatabaseUUIDAccountEntity, UUID> {
    @Override
    protected DatabaseUUIDAccountEntity newInstance() {
        return new DatabaseUUIDAccountEntity();
    }
}
