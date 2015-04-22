#!/bin/bash

FLUME_HOME=$1
CONF_NAME=$2

if [ -z "$FLUME_HOME" ] ;then
    echo "Param FLUME_HOME Missing"
    exit 1
else
    echo "FLUME_HOME : " $FLUME_HOME 
fi

if [ -z "$CONF_NAME" ] ;then
    echo "Param CONF_NAME Missing"
    exit 1
else
    echo "CONF_NAME : " $CONF_NAME 
fi

rm -rf $FLUME_HOME/bin/run
rm -rf $FLUME_HOME/bin/supervise
rm -rf $FLUME_HOME/logs/*
rm -rf $FLUME_HOME/data/*
rm -rf $FLUME_HOME/conf/*

touch  $FLUME_HOME"/logs/deploy.log"

cp   $FLUME_HOME/confRepo/$CONF_NAME/*				$FLUME_HOME/conf/
sed  -i "s@\${FLUME_HOME}@${FLUME_HOME}@g"			$FLUME_HOME/conf/flume-conf.properties
echo "flume.log.dir="$FLUME_HOME"/logs" 		>> 	$FLUME_HOME/conf/log4j.properties

echo "`date`" 						>>	$FLUME_HOME/logs/deploy.log
echo '#!/bin/bash' 					>  	$FLUME_HOME/bin/run
echo $FLUME_HOME"/bin/flume-ng agent -n agent -c "$FLUME_HOME"/conf/ -f "$FLUME_HOME"/conf/flume-conf.properties -Dflume.monitoring.type=http --no-reload-conf " >> $FLUME_HOME/bin/run
chmod +x $FLUME_HOME/bin/run

supervise $FLUME_HOME/bin &
