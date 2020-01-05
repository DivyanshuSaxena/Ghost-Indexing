#!/bin/bash

# Script to run the Performance Tester class in GhostIndex/Utilities project
# Arguments:
# $1 : Fullpath to Neo4J Installation directory
# $2 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_neo<number>]
# $3 : Fullpath to ldbc_snb_implementations folder
# $4 : Fullpath to Ghost-Indexing/Neo4j folder
# $5 : Query number for which the test is to be run

datasets=( 1000 2500 4000 7500 )
cwd=$(pwd)
export NEO4J_HOME=$1
export NEO4J_DB_DIR=$NEO4J_HOME/data/databases/graph.ghostdb  
export POSTFIX=_0_0.csv

for dataset in ${datasets[*]}; do
	echo "=================================================="
	echo "Starting for dataset: "$dataset
	echo "--------------------------------------------------"

  export NEO4J_DATA_DIR=$2/social_network_neo_$dataset/
  cd $3/cypher/load_scripts
  ./load-in-one-step.sh

  cd $4/queries
  ./run_all_params.sh $5