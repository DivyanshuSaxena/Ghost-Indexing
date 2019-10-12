#!/bin/bash
if [ "$JANUS_HOME" = "" ]; then
    echo "JANUS_HOME not set"
    exit 1
fi

PWD_ORIG=$(pwd)

cd $JANUS_HOME
echo ":load ${PWD_ORIG}/indexCreationScripts/mgmtIndex.gremlin" | bin/gremlin.sh

