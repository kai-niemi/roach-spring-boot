package io.roach.spring.identity;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.identity.model.NumericIDAccountEntity;

@ActiveProfiles({"numid", "test"})
public class NumericIDAccountTest extends AbstractAccountTest<NumericIDAccountEntity, Long> {
    @Override
    protected NumericIDAccountEntity newInstance() {
        return new NumericIDAccountEntity();
    }
}

