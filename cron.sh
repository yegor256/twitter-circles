#!/bin/bash
set -e
set -o pipefail

cd $(dirname $0)
source /etc/profile
if [ ! -f /etc/init.d/functions ]; then
    echo "You can run this script only in Linux"
    exit -1;
fi
source /etc/init.d/functions

git pull
DB=${HOME}/twitter.db
OAUTH=$(cat ${HOME}/twitter.key)
mvn --batch-mode --strict-checksums --errors --quiet \
    -Pliquibase -Dsqlite.file=${DB} clean package
daemon --restart --pidfile=${HOME}/front.pid --chroot=${HOME}/front \
    --output=user.info --stderr=user.error \
    python front.py ${HOME}/twitter.db

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
        java -jar target/circles.jar "--city=${city}" \
            "--tag=${keyword}" "--jdbc=jdbc:sqlite:${DB}" "--key=${OAUTH}"
    done
done
