package io.roach.spring.batch.integrationtests;

import org.springframework.test.context.ActiveProfiles;

import io.roach.spring.batch.ProfileNames;

@ActiveProfiles(value = {ProfileNames.CRDB_DEV},inheritProfiles = false)
public class BatchStatementsTest extends AbstractBatchStatementsTest {
}
