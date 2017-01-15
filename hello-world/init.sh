#!/bin/bash

# https://docs.docker.com/v1.8/userguide/dockerlinks/
# https://docs.docker.com/compose/env/

apt-get update && \
	apt-get -y install netcat

nc -w5 -z -n $DB_PORT_3306_TCP_ADDR $DB_PORT_3306_TCP_PORT

WAIT_SCRIPT_URL=https://raw.githubusercontent.com/abhijitsarkar/docker/master/docker-util/wait.sh

curl -sSL -o ./wait.sh $WAIT_SCRIPT_URL
chmod +x ./wait.sh

MYSQL_ENDPOINT="$MYSQL_ENDPOINT_PORT_3306_TCP_ADDR:$MYSQL_ENDPOINT_PORT_3306_TCP_PORT"
MYSQL_STATUS=$(./wait.sh $MYSQL_ENDPOINT)

rm -f ./wait.sh

if [ "$MYSQL_STATUS" -gt 0 ]; then
    printf "[ERROR] Could not connect to MySQL (%s, %s).\n\n" $MYSQL_ENDPOINT

    exit 1
fi

supervisord -c /etc/supervisord.conf