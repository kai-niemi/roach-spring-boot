package io.roach.spring.identity;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.identity.model.SequenceAccountEntity;

@ActiveProfiles({"test"})
public class SequenceAccountTest extends AbstractAccountTest<SequenceAccountEntity, Long> {
    @Override
    protected SequenceAccountEntity newInstance() {
        return new SequenceAccountEntity();
    }
}
