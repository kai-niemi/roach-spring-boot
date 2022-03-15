# CockroachDB Spring Boot :: Batch Demo

A standalone spring boot app demonstrating batch inserts and updates
using [Spring Data JPA](https://spring.io/projects/spring-data-jpa) 
and Hibernate.

Key takeaways:

- How to use and enable batching
- How to verify batching actually happens

## Relevant Articles

- [Multitenancy Applications with Spring Boot and CockroachDB](https://blog.cloudneutral.se/multitenancy-applications-with-spring-boot-and-cockroachdb)
- [Spring Annotations for CockroachDB](https://blog.cloudneutral.se/spring-annotations-for-cockroachdb)

## Using

### Prerequisites

- JDK8+ with 1.8 language level (OpenJDK compatible)
- Maven 3+ (wrapper provided)
- CockroachDB with a database named `spring_boot_batch`

### Building

    mvn clean install
    
### Running the tests

    mvn -DskipTests=false -Dtest=io.roach.spring.batch.integrationtests.BatchStatementsTest -Dspring.profiles.active=local test
    mvn -DskipTests=false -Dtest=io.roach.spring.batch.integrationtests.BatchStatementsVerboseTest -Dspring.profiles.active=local test
    mvn -DskipTests=false -Dtest=io.roach.spring.batch.integrationtests.DisabledBatchStatementsTest -Dspring.profiles.active=local test
    mvn -DskipTests=false -Dtest=io.roach.spring.batch.integrationtests.DisabledMultiValueInsertsTest -Dspring.profiles.active=local test

Available spring profiles (set with -Dspring.profiles.active=...) include:

- psql
- psql-dev
- crdb
- crdb-dev
- crdb-cloud
- verbose
- disable-batch
- disable-multi-value

See `src/main/resources/application-*.yml` for connection details.
    
### Misc

When using JDK 11+ and connecting against a secure CockroachDB cluster set the following VM option:

    -Djdk.tls.client.protocols=TLSv1.2