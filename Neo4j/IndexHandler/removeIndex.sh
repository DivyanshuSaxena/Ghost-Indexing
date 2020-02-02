#!/bin/bash
# Script to remove Ghost Indexes created over Neo4j
# 
# Arguments:
# $1 : Fullpath to Neo4J Installation directory
# $2 : Username to access Neo4J
# $3 : Password to access Neo4J

$1/bin/cypher-shell -u $2 -p $3 < remove_ghost_index.cql
echo "[INFO]: Ghost Indexes deleted";
