#!/bin/bash

# Script to run all the parameters for the given query on Neo4j using terminal
# Arguments:
# $1 : Query number for which the parameters are to be run.
# $2 : Parameter for which the date type conversion is to be made.
# $3 : Dataset size for current execution
# $4 : Whether ghost index or lucene index is to be used
# $5 : Fullpath to the neo4j/bin/cypher-shell binary
# $6 : Fullpath to the Janusgraph/GhostIndex folder
# $7 : Fullpath to the cypher/queries subfolder in ldbc_snb_implementations folder
# $8 : Username for the cypher shell
# $9 : Password for the cypher shell

if [ -e './params/bi_'$1'_neo_param.txt' ]
then
  echo "Parameter file already exists"
else
  echo "Creating Neo4j parameter file"
  python convert_params.py $1 $2 $6
fi

# Run python file to fire queries on the cypher shell (with respective parameters)
echo "Starting all params execution"
python run_all_params.py $1 $2 $3 $4 $5 $7 $8 $9
