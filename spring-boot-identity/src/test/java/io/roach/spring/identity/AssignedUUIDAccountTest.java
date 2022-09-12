package io.roach.spring.identity;


import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.identity.model.AssignedUUIDAccountEntity;

@ActiveProfiles({"test"})
public class AssignedUUIDAccountTest extends AbstractAccountTest<AssignedUUIDAccountEntity, UUID> {
    @Override
    protected AssignedUUIDAccountEntity newInstance() {
        return new AssignedUUIDAccountEntity();
    }
}
