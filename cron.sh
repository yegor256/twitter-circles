#!/bin/bash
set -e
set -o pipefail

git pull
DB=${HOME}/twitter.db
OAUTH=$(cat ${HOME}/twitter.key)
mvn clean package -Pliquibase -Dsqlite.file=${DB}

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
        java -jar target/circles.jar --city=${city} \
            --tag=${KEYWORD} --jdbc=jdbc:sqlite:${DB} --key=${OAUTH}
    done
done
