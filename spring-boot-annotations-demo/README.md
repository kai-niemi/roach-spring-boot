# CockroachDB Spring Boot :: Annotations Demo

A standalone spring boot app demonstrating CockroachDB related annotations.
   
# Project Setup

## Prerequisites

- JDK8+ with 1.8 language level (OpenJDK compatible)
- Maven 3.1+ (optional)

## Database Setup

Create the database to run test against:

    CREATE database spring_boot;
    
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
    
    cd spring-boot-annotations-demo
    target/spring-boot-annotations-demo.jar

Alternatively, start the server using JDBC, a custom URL and savepoints for retries:

    java -jar target/spring-boot-annotations-demo.jar \ 
    --spring.profiles.active=jdbc,savepoints \ 
    --roach.datasource.url=jdbc:postgresql://192.168.1.2:26257/spring_boot?sslmode=disable    

Run a stress test by sending concurrent HTTP requests to `localhost:8090`:

    mvn -DskipTests=false -Dtest=io.roach.spring.annotations.IntegrationTest -Dthreads=20 test
 