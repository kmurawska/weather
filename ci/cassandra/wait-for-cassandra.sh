#!/bin/bash

set -e

while [ "$(nodetool netstats | grep -oP '(?<=Mode: ).*')" != "NORMAL" ]; do
    sleep 1
done

echo "READY" > /state