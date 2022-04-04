# CockroachDB Spring Boot :: Locking

## Using

### Prerequisites

- JDK8+ with 1.8 language level (OpenJDK compatible)
- Maven 3+ (wrapper provided)
- CockroachDB with a database named `spring_boot`

### Building

    mvn clean install

### Running the tests

    mvn -DskipTests=false

### Misc

When using JDK 11+ and connecting against a secure CockroachDB cluster set the following VM option:

    -Djdk.tls.client.protocols=TLSv1.2