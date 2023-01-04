# CockroachDB Spring Boot :: Column Families

A standalone spring boot app demonstrating CockroachDB Column Families.

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

    cd spring-boot-column-families
    ./mvnw clean install

## Running

Start the server using JPA and Hibernate (default):

    target/spring-boot-column-families.jar

Alternatively, start the server with custom parameters:

    target/spring-boot-column-families.jar \
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

# Testing Procedure

In this sequence of operations we are updating the same record concurrently on different columns. 
With a single column family, it will fail with a 40001 error and with multiple column families, 
it will succeed.

## Single Column Family Test

Get form to create new order:
```shell
curl "http://localhost:8090/order/v1/template" > o1.json
```

Submit order form:

```shell
curl "http://localhost:8090/order/v1/" -H "Content-Type:application/json" -X POST -d "@o1.json"
```

Take note of generated `id` value and update status which will delay the commit by 5 sec
to allow for the other update to come in between:

```shell
curl "http://localhost:8090/order/v1/1/status?delay=5" -i -X PUT
```

Within 5 sec, update price on same order which will cause a serialization conflict:

```shell
curl "http://localhost:8090/order/v1/1/price?price=5&delay=0" -i -X PUT
```

This request will fail.

## Multiple Column Families Test

Get form to create new order:
```shell
curl "http://localhost:8090/order/v2/template" > o1.json
```

Submit order form:

```shell
curl "http://localhost:8090/order/v2/" -H "Content-Type:application/json" -X POST -d "@o1.json"
```

Take note of generated `id` value and update status which will delay the commit by 5 sec
to allow for the other update to come in between:

```shell
curl "http://localhost:8090/order/v2/1/status?delay=5" -i -X PUT
```

Within 5 sec, update price on same order which will cause a serialization conflict:

```shell
curl "http://localhost:8090/order/v2/1/price?price=5&delay=0" -i -X PUT
```

This request will fail.
