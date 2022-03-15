package io.roach.spring.batch.integrationtests;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.batch.ProfileNames;

@ActiveProfiles({ProfileNames.CRDB_DEV, ProfileNames.VERBOSE})
public class BatchStatementsVerboseTest extends AbstractBatchStatementsTest {
}
