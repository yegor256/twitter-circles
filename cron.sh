#!/bin/bash

HOME=$(dirname $0)
cd ${HOME}/twitter-circles
CITIES=(
    "52.3740300,4.8896900,30km",
    "37.4419444,-122.1419444,50mi"
)
for city in "${CITIES[@]}"
do
    mvn test -DskipTests -Plive -Pcuriost --settings ${HOME}/settings.xml \
        -Dcity=${city} -Dtag=java -Dsqlite.file=${HOME}/twitter.db
done
