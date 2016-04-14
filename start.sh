#!/bin/sh

#Copyright 2015, Comcast Corporation. This software and its contents are
#Comcast confidential and proprietary. It cannot be used, disclosed, or
#distributed without Comcast's prior written permission. Modification of this
#software is only allowed at the direction of Comcast Corporation. All allowed
#modifications must be provided to Comcast Corporation.

JARFILE=/opt/e2e-monitoring/e2e-monitoring.jar
PIDFILE=/var/run/e2e-monitoring.pid

CONFDIR=/etc/e2e-monitoring/
CONFFILE=e2e-monitoring.conf
CONFPATH=${CONFDIR}${CONFFILE}
CONFLOG=log4j2.xml
CONFLOGPATH=${CONFDIR}${CONFLOG}

JMXREMOTE=true
JMXPORT=7711
JMXAUTH=false
JMXSSL=false

JAVAARGS=""
MAXMEM=7G

SYSCFG=/etc/sysconfig/e2e-monitoring

if [ -f $SYSCFG ]; then
. $SYSCFG
fi

JMXARGS=""

if [ "$JMXREMOTE" = "true" ]; then
JMXARGS="-Dcom.sun.management.jmxremote=$JMXREMOTE -Dcom.sun.management.jmxremote.port=$JMXPORT -Dcom.sun.management.jmxremote.authenticate=$JMXAUTH -Dcom.sun.management.jmxremote.ssl=$JMXSSL"
fi

java -Xmx${MAXMEM} -Dlog4j.configurationFile=file:${CONFLOGPATH}\
${JMXARGS}\
${JAVAARGS}\
-jar ${JARFILE} ${CONFPATH} 2>&1 &

if [ $? -eq 0 ]; then
if echo -n $! > ${PIDFILE}; then
sleep 1
else
echo "FAILED TO WRITE PID"
exit 1
fi
else
echo "SERVER DID NOT START"
exit 1
fi