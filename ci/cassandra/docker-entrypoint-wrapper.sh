#!/bin/bash

set -e

/state-service.sh &
/wait-for-cassandra.sh &

if  [ "x" != "x$WAIT_FOR" ]; then
	for WAITING in $WAIT_FOR; do
		WAITING_VALUES=(${WAITING//;/ })
		HOST=${WAITING_VALUES[0]}
		PORT=${WAITING_VALUES[1]}
		EXPECTED=${WAITING_VALUES[2]}

		until nc $HOST $PORT | grep $EXPECTED; do
			echo "Waiting for $HOST:$PORT to respond with $EXPECTED"
			sleep 1
		done
	done
fi

/docker-entrypoint.sh cassandra -f

exec "$@"