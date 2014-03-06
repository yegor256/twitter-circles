#!/bin/bash

git pull

CITIES=(
    "52.3740300,4.8896900,30km"
    "37.4419444,-122.1419444,50mi"
)
KEYWORDS=(
    "java"
    "github"
    "aws"
    "dynamo"
)
for keyword in "${KEYWORDS[@]}"
    for city in "${CITIES[@]}"
    do
        mvn test -DskipTests -Plive -Pcuriost \
            -Dcity=${city} -Dtag=${KEYWORD} -Dsqlite.file=${HOME}/twitter.db
    done
done
