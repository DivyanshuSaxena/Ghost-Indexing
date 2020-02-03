#!/bin/bash

# Script to run all the parameters for the given query on Neo4j using terminal
# Arguments:
# $1 : Query number for which the parameters are to be run.
# $2 : Dataset size for current execution
# $3 : Whether ghost index or lucene index is to be used
# $4 : Fullpath to the neo4j/bin/cypher-shell binary
# $5 : Fullpath to the Janusgraph/GhostIndex folder
# $6 : Fullpath to the cypher/queries subfolder in ldbc_snb_implementations folder
# $7 : Username for the cypher shell
# $8 : Password for the cypher shell

if [ -e './params/bi_'$1'_neo_param.txt' ]
then
  echo "Parameter file already exists"
else
  echo "Creating Neo4j parameter file"
  python convert_params.py $1 $5
fi

# Run python file to fire queries on the cypher shell (with respective parameters)
echo "Starting all params execution"
python run_all_params.py $1 $2 $3 $4 $6 $7 $8
