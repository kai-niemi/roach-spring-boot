# CockroachDB Spring Boot :: Connection Pooling

A standalone spring boot app demonstrating connection pooling 
with CockroachDB.

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

## Running

Start the server using JPA and Hibernate (default):

    cd spring-boot-pooling
    target/spring-boot-pooling.jar

Alternatively, start the server with custom parameters:

    target/spring-boot-pooling.jar \
    --server.port=8090 \
    --spring.profiles.active=verbose \
    --spring.datasource.url=jdbc:postgresql://localhost:26257/spring_boot?sslmode=disable \
    --spring.datasource.hikari.maximum-pool-size=45 \
    --spring.datasource.hikari.minimum-idle=25 \
    --spring.datasource.hikari.max-lifetime=1800005 \
    "$@"

The REST API index is available at (explorable):

    http://localhost:8090/

To create one product:

    curl -d '{"balance": 50.0,"currency": "USD","name": "some name","description": "some description"}' -H "Content-Type:application/json" -X POST http://localhost:8090/product

To create many products:

    curl -d '{"numProducts":10000,"batchSize":128}' -H "Content-Type:application/json" -X POST http://localhost:8090/catalog 

To list products (first page):

    curl -X GET http://localhost:8090/product

To inspect connection pool status:

    curl -X GET http://localhost:8090/admin/pool-size

To inspect connection pool config:

    curl -X GET http://localhost:8090/admin/pool-config

## Useful Tools

- json-viewer chrome plugin - https://goo.gl/fmphc7
- Postman HTTP client - https://www.postman.com/downloads/
- cURL