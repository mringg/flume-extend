#!/bin/bash

FLUME_HOME=$1

if [ -z "$FLUME_HOME" ] ;then
    echo "Param FLUME_HOME Missing"
    exit 1
else
    echo "FLUME_HOME : " $FLUME_HOME 
fi

ps -ef|grep "supervise $FLUME_HOME"|grep -v grep|awk '{print $2}'|xargs kill -9
ps -ef|grep $FLUME_HOME|grep -v grep|grep -v kill.sh|awk '{print $2}'|xargs kill -15

