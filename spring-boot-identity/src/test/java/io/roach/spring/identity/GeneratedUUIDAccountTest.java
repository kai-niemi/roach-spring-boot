package io.roach.spring.identity;

import java.util.UUID;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.identity.model.GeneratedUUIDAccountEntity;

@ActiveProfiles({"test"})
public class GeneratedUUIDAccountTest extends AbstractAccountTest<GeneratedUUIDAccountEntity, UUID> {
    @Override
    protected GeneratedUUIDAccountEntity newInstance() {
        return new GeneratedUUIDAccountEntity();
    }
}
