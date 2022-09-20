#!/bin/bash

url=jdbc:postgresql://localhost:26257/spring_boot?sslmode=disable
#url=jdbc:postgresql://192.168.1.2:26257/spring_boot?sslmode=disable

target/spring-boot-pooling.jar \
--server.port=8090 \
--spring.profiles.active=verbose \
--spring.datasource.url="$url" \
--spring.datasource.hikari.maximum-pool-size=45 \
--spring.datasource.hikari.minimum-idle=25 \
--spring.datasource.hikari.max-lifetime=1800005 \
"$@"