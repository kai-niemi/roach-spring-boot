# CockroachDB Spring Boot :: Pagination

A standalone e-commerce spring boot app demonstrating 
offset+limit pagination using JPA and Hibernate.

## Using

### Prerequisites

- JDK8+ with 1.8 language level (OpenJDK compatible)
- Maven 3+ (wrapper provided)
- CockroachDB with a database named `spring_boot`

### Running the app
                                     
Build:

    mvn clean install

(alt: build from parent level):

    cd..    
    ./mvnw clean install
    cd -

Start the app with a custom JDBC URL:
                   
    java -jar target/spring-boot-pagination.jar --medium --spring.datasource.url=jdbc:postgresql://192.168.1.2:26257/spring_boot?sslmode=disable

or

    target/spring-boot-pagination.jar --small --spring.datasource.url=jdbc:postgresql://192.168.1.2:26257/spring_boot?sslmode=disable

Open REST API index:

    open http://localhost:8080/
    
### Misc

When using JDK 11+ and connecting against a secure CockroachDB cluster set the following VM option:

    -Djdk.tls.client.protocols=TLSv1.2