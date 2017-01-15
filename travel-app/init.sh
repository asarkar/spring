#!/bin/bash

WAIT_SCRIPT_URL=https://raw.githubusercontent.com/abhijitsarkar/docker/master/docker-util/wait.sh

curl -sSL -o ./wait.sh $WAIT_SCRIPT_URL
chmod +x ./wait.sh

COUCHBASE_ENDPOINT="$COUCHBASE_PORT_8091_TCP_ADDR:$COUCHBASE_PORT_8091_TCP_PORT"
COUCHBASE_STATUS=$(./wait.sh $COUCHBASE_ENDPOINT)

rm -f ./wait.sh

if [ "$COUCHBASE_STATUS" -gt 0 ]; then
    printf "[ERROR] Could not connect to Couchbase (%s, %s).\n\n" $COUCHBASE_ENDPOINT

    exit 1
fi

supervisord -c /etc/supervisord.conf