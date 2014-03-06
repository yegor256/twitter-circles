#!/bin/bash
set -x

CITIES=(
    "52.3740300,4.8896900,30km",
    "37.4419444,-122.1419444,50mi"
)
for city in "${CITIES[@]}"
do
    mvn test -DskipTests -Plive -Pcuriost \
        -Dcity=${city} -Dtag=java -Dsqlite.file=${HOME}/twitter.db
done
