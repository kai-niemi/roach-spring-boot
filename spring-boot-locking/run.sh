#!/bin/bash

APP_NAME=spring-boot-locking
FILE=target/spring-boot-locking.jar
ARGS="--spring.profiles.active=verbose,crdb --spring.datasource.url=jdbc:postgresql://192.168.1.99:26257/spring_boot?sslmode=disable"

if [ ! -f "$FILE" ]; then
    ../mvnw clean install
fi

fn_start_1(){
  nohup java -jar $FILE $ARGS --server.port=8090 "$@" > locking-1-stdout.log 2>&1 &
  echo "$APP_NAME is running. Type Ctrl+C to exit log tail"
}

fn_start_2(){
  nohup java -jar $FILE $ARGS --server.port=8091 "$@" > locking-2-stdout.log 2>&1 &
  echo "$APP_NAME is running. Type Ctrl+C to exit log tail"
}

fn_start_3(){
  nohup java -jar $FILE $ARGS --server.port=8092 "$@" > locking-3-stdout.log 2>&1 &
  echo "$APP_NAME is running. Type Ctrl+C to exit log tail"
}

fn_stop_all(){
  killall -9 "$APP_NAME"
}

########################################

getopt=$1
shift

case "${getopt}" in
    start-1)
        fn_start_1 "$*"
        ;;
    start-2)
        fn_start_2 "$*"
        ;;
    start-3)
        fn_start_3 "$*"
        ;;
    stop)
        fn_stop_all "$*"
        ;;
    *)
    if [ -n "${getopt}" ]; then
        echo -e "Unknown command: $0 ${getopt}"
    fi
    echo -e "Usage: $0 [command]"
    echo -e ""
    echo -e "Commands"
    {
        echo -e "start-1\t start server #1"
        echo -e "start-2\t start server #2"
        echo -e "start-3\t start server #3"
        echo -e "stop\t stop all servers"
    } | column -s $'\t' -t
esac

