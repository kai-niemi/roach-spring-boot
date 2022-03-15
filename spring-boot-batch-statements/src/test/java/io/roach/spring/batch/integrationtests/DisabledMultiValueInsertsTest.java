package io.roach.spring.batch.integrationtests;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.batch.ProfileNames;

@ActiveProfiles({ProfileNames.CRDB_DEV, ProfileNames.DISABLE_MULTI_VALUE})
public class DisabledMultiValueInsertsTest extends AbstractBatchStatementsTest {
}
