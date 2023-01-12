# CockroachDB Spring Boot :: Inbox Pattern 

A standalone spring boot app demonstrating the inbox pattern
using ActiveMQ and CockroachDB.

# Project Setup

## Prerequisites

- ActiveMQ 5
- CockroachDB v22.1+
- JDK 19+ (OpenJDK compatible)
- Maven 3.1+ (optional)

## ActiveMQ Setup

Although ActiveMQ claims to be JDK 1.8 compatible, its compiled with a class version beyond
that so JDK 19 or higher is needed.
                       
Linux:

```shell      
wget https://downloads.apache.org/activemq/5.17.3/apache-activemq-5.17.3-bin.tar.gz
tar zxvf apache-activemq-5.17.3-bin.tar.gz
cd apache-activemq-5.17.3/bin
./activemq console
```

OSX:

```shell    
brew install apache-activemq
```

The admin UI is available on: http://127.0.0.1:8161/admin/
Default login is `admin/admin`.

## Database Setup

Create the database to run test against:

    CREATE database spring_boot;

## Building and running from codebase

The application is built with [Maven 3.1+](https://maven.apache.org/download.cgi).
Tanuki's Maven wrapper is included (mvnw). All 3rd party dependencies are available in public Maven repos.

Clone the project:

    git clone git@github.com:kai-niemi/roach-spring-boot.git

To build and deploy to your local Maven repo, execute:

    cd roach-spring-inbox
    ./mvnw clean install

## Running

Start the server using JPA and Hibernate (default):

```shell
cd spring-boot-inbox
java -jar target/spring-boot-inbox.jar
```

Alternatively, start the server with custom parameters:

```shell
java -jar target/spring-boot-inbox.jar \
--server.port=8090 \
--spring.profiles.active=verbose \
--spring.datasource.url=jdbc:postgresql://localhost:26257/spring_boot?sslmode=disable \
--active-mq.broker-url=tcp://localhost:61616 \
"$@"
```

The REST API index is available at: http://localhost:8090/

Next, either get and submit a form or inline the payload:
                                                     
Form method:

```shell
curl http://localhost:8090/registration/form > form.json
curl -v -d "@form.json" -H "Content-Type:application/json" -X POST http://localhost:8090/registration/ 
```

Inlined:

```shell
curl -v -d '{"name":"User","email":"user@email.com","jurisdiction":"mga","createdAt":"2023-01-12T09:21:04.571+00:00"}' -H "Content-Type:application/json" -X POST http://localhost:8090/registration/
```

To observe that the events were received and stored in the journal (pipe to `jq` is optional):

```shell
curl http://localhost:8090/journal/registration-events?jurisdiction=mga | jq 
```

