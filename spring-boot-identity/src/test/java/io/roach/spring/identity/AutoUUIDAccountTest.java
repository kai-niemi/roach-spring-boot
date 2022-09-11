package io.roach.spring.identity;

import java.util.UUID;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.identity.model.AutoUUIDAccountEntity;

@ActiveProfiles({"test", "verbose"})
public class AutoUUIDAccountTest extends AbstractAccountTest<AutoUUIDAccountEntity, UUID> {
    @Override
    protected AutoUUIDAccountEntity newInstance() {
        return new AutoUUIDAccountEntity();
    }
}
