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
if [ -f ${DIR}/front.pid ]; then
    kill -9 $(cat ${DIR}/front.pid)
    rm -rf ${DIR}/front.pid
fi
daemonize -a -v -l ${DIR}/front.pid -p ${DIR}/front.pid -c ${DIR}/front \
    -o ${DIR}/stdout.log -e ${DIR}/stderr.log \
    $(which python) front.py ${DIR}/twitter.db

while read city; do
    while read keyword; do
        java -jar ${DIR}/target/circles.jar "--city=${city}" \
            "--tag=${keyword}" "--jdbc=jdbc:sqlite:${DB}" "--key=${OAUTH}"
    done < keywords.txt
done < cities.txt
