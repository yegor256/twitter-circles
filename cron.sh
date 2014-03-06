#!/bin/bash
set -e
set -o pipefail

source /etc/profile

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
    "github OR w3c"
    "aws OR dynamodb OR ec2 OR cloudfront OR cloudwatch"
    "html OR json OR javascript"
)
for keyword in "${KEYWORDS[@]}"
do
    for city in "${CITIES[@]}"
    do
        java -jar target/circles.jar --city=${city} \
            --tag=${keyword} --jdbc=jdbc:sqlite:${DB} --key=${OAUTH}
    done
done
