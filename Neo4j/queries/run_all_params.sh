#!/bin/bash

# Script to run all the parameters for the given query on Neo4j using terminal
# Arguments:
# $1 : Query number for which the parameters are to be run.
# $2 : Parameter for which the date type conversion is to be made.
# $3 : Fullpath to the neo4j/bin/cypher-shell binary
# $4 : Fullpath to the Janusgraph/GhostIndex folder
# $5 : Fullpath to the cypher/queries subfolder in ldbc_snb_implementations folder
# $6 : Username for the cypher shell
# $7 : Password for the cypher shell

if [ -e 'bi_'$1'_neo_param.txt' ]
then
  echo "Parameter file already exists"
else
  echo "Creating Neo4j parameter file"
  python convert_params.py $1 $2 $4
fi

# Run python file to fire queries on the cypher shell (with respective parameters)
python run_all_params.py $1 $3 $5 $6 $7
