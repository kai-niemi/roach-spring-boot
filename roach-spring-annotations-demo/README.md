# CockroachDB Spring Boot :: Annotations Demo

Standalone server app demonstrating CockroachDB Spring Boot annotations.
   
# Project Setup

## Prerequisites

- JDK8+ with 1.8 language level (OpenJDK compatible)
- Maven 3.1+ (optional)

## Database Setup

Create the database to run test against:

    CREATE database roach_spring;
    
## Building and running from codebase

The application is built with [Maven 3.1+](https://maven.apache.org/download.cgi).
Tanuki's Maven wrapper is included (mvnw). All 3rd party dependencies are available in public Maven repos.

Clone the project:

    git clone git@github.com:kai-niemi/roach-spring-boot.git

To build and deploy to your local Maven repo, execute:
                                                            
    cd roach-spring-boot
    ./mvnw clean install

## Stress Test

Start the server using JPA and Hibernate (default):    
    
    cd roach-spring-annotations-demo
    target/roach-spring-annotations-demo.jar

Alternatively, start the server using JDBC and savepoints for retries:

    java -jar target/roach-spring-annotations-demo.jar 
    --spring.profiles.active=jdbc 
    --roach.datasource.url=jdbc:postgresql://192.168.1.99:26257/roach_spring?sslmode=disable    

Run a stress test by sending concurrent HTTP requests to `localhost:8090`:

    mvn -DskipTests=false -Dtest=io.roach.spring.annotations.StressTest -Dthreads=20 test
 