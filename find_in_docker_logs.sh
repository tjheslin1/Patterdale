#!/bin/bash
set -e

# https://flyingtophat.co.uk/blog/2017/06/07/testing-docker-images.html

CONTAINER_NAME=$1
TIMEOUT=$2
SUBSTRING=$3

COMMAND="docker logs -f $CONTAINER_NAME"

expect -c "log_user 0; set timeout $TIMEOUT; spawn $COMMAND; expect \"$SUBSTRING\" { exit 0 } timeout { exit 1 }"
