package io.roach.spring.identity;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.identity.model.CustomIDAccountEntity;

@ActiveProfiles({"test", "verbose"})
public class CustomIDAccountTest extends AbstractAccountTest<CustomIDAccountEntity, Long> {
    @Override
    protected CustomIDAccountEntity newInstance() {
        return new CustomIDAccountEntity();
    }
}
