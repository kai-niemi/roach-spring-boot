# CockroachDB Spring Boot :: Idempotency

A standalone spring boot app demonstrating techniques for 
idempotent POST methods in REST APIs.

See this blog [post](https://blog.cloudneutral.se/) for an article 
on this topic.

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

    cd spring-boot-idempotency
    ./mvnw clean install

## Running

Start the server using JPA and Hibernate (default):

    target/spring-boot-idempotency.jar

Alternatively, start the server with custom parameters:

    target/spring-boot-idempotency.jar \
    --server.port=8090 \
    --spring.profiles.active=verbose \
    --spring.datasource.url=jdbc:postgresql://localhost:26257/spring_boot?sslmode=disable \
    "$@"

The REST API index is available at (explorable):

    curl http://localhost:8090/ | jq

## Useful Tools

- json-viewer chrome plugin - https://goo.gl/fmphc7
- Postman HTTP client - https://www.postman.com/downloads/
- cURL - https://github.com/curl/curl
- jq - https://github.com/stedolan/jq
