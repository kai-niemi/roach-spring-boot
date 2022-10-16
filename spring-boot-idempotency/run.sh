#!/bin/bash

APP_NAME=spring-boot-idempotency
FILE=target/spring-boot-idempotency.jar
URL=jdbc:postgresql://localhost:26257/spring_boot?sslmode=disable
#URL=jdbc:postgresql://192.168.1.99:26257/spring_boot?sslmode=disable
ARGS="--server.port=8090 \
 --spring.profiles.active=verbose \
 --spring.datasource.url=$URL \
 --spring.datasource.hikari.maximum-pool-size=20 \
 --spring.datasource.hikari.minimum-idle=15 \
 --spring.datasource.hikari.max-lifetime=15000"

if [ ! -f "$FILE" ]; then
    chmod +x mvnw
    ../mvnw clean install
fi

fn_run(){
  java -jar target/spring-boot-idempotency.jar $ARGS "$@"
}

fn_start(){
  nohup java -jar target/spring-boot-idempotency.jar $ARGS "$@" > idempotency-stdout.log 2>&1 &
  echo "$APP_NAME is running"
}

fn_stop(){
  pid=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')

  if [ -z "${pid}" ]; then
     echo "$APP_NAME is not running"
  else
      echo "Killing $pid"
      kill -9 "$pid"
  fi
}

########################################
########################################
########################################

getopt=$1
shift

case "${getopt}" in
    r|run)
        fn_run "$*"
        ;;
    s|start)
        fn_start "$*"
        ;;
    p|stop)
        fn_stop "$*"
        ;;
    *)
    if [ -n "${getopt}" ]; then
        echo -e "Unknown command: $0 ${getopt}"
    fi
    echo -e "Usage: $0 [command]"
    echo -e ""
    echo -e "Commands"
    {
        echo -e "r|run\t run server"
        echo -e "s|start\t start server"
        echo -e "p|stop\t stop server"
    } | column -s $'\t' -t
esac

