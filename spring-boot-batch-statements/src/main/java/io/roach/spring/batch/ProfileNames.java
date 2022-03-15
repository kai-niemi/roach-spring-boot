package io.roach.spring.batch;

public class ProfileNames {
    // Databases
    public static final String CRDB = "crdb";

    public static final String CRDB_DEV = "crdb-dev";

    public static final String CRDB_CLOUD = "crdb-cloud";

    public static final String PSQL = "psql";

    public static final String PSQL_DEV = "psql-dev";

    // More verbose trace logging
    public static final String VERBOSE = "verbose";

    // Disable batch inserts and updates
    public static final String DISABLE_BATCH = "disable-batch";

    // Disable rewriting of inserts to multi-value
    public static final String DISABLE_MULTI_VALUE = "disable-multi-value";

    private ProfileNames() {
    }
}

