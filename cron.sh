#!/bin/bash
set -e
set -o pipefail

source /etc/profile

cd $(dirname $0)
DIR=$(pwd)

git pull
DB=${DIR}/twitter.db
OAUTH=$(cat ${DIR}/twitter.key)
mvn --batch-mode --strict-checksums --errors --quiet \
    -Pliquibase -Dsqlite.file=${DB} clean package
# http://software.clapper.org/daemonize/
daemonize -a -v -l ${DIR}/front.pid -p ${DIR}/front.pid -c ${DIR}/front \
    -o ${DIR}/stdout.log -e ${DIR}/stderr.log \
    python front.py ${DIR}/twitter.db

CITIES=(
    "52.3740300,4.8896900,30km"
    "37.4419444,-122.1419444,50mi"
)
KEYWORDS=(
    "github"
    "aws OR dynamodb OR cloudfront OR cloudwatch"
    "json OR javascript OR xhtml OR html5"
)
for keyword in "${KEYWORDS[@]}"
do
    for city in "${CITIES[@]}"
    do
        java -jar ${DIR}/target/circles.jar "--city=${city}" \
            "--tag=${keyword}" "--jdbc=jdbc:sqlite:${DB}" "--key=${OAUTH}"
    done
done
