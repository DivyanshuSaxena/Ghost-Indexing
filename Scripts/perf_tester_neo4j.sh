#!/bin/bash

# Script to run the Performance Tester class in GhostIndex/Utilities project
# Arguments:
# $1 : Query number for which the test is to be run
# $2 : Label of vertex on which index is to be made
# $3 : Attribute on which index is to be made
# $4 : Whether the index is (date (D)/string (S)/integer (I)) type
# $5: Column in csv on which index is to be made
# $6 : Index Type (B/BP)
# $7 : Branch size of B/BP Tree 
# $8 : Fullpath to Neo4J Installation directory
# $9 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_neo_<number>]
# $10 : Fullpath to ldbc_snb_implementations folder
# $11 : Fullpath to Ghost-Indexing/Neo4j folder
# $12 : Fullpath to the input csv

datasets=( 1000 2500 4000 7500 )
cwd=$(pwd)
export NEO4J_HOME=$8
export NEO4J_DB_DIR=$NEO4J_HOME/data/databases/graph.ghostdb  
export POSTFIX=_0_0.csv

for dataset in ${datasets[*]}; do
	echo "=================================================="
	echo "Starting for dataset: "$dataset
	echo "--------------------------------------------------"

  export NEO4J_DATA_DIR=$9/social_network_neo_$dataset/
  cd ${10}/cypher/load_scripts
  # Deletes previous data in Neo4J and rewrite the new dataset
  ./load-in-one-step.sh

  # Run the Neo4J queries over Lucene Indexes
  cd ${11}/queries
  ./run_all_params.sh $1 ${11}/../Janusgraph/GhostIndex ${10}/cypher/queries

  # Generate the ghost indexes
  cd ${11}/IndexHandler
  time ./unifyIndex.sh $2 $3_index_$6_$7 $7 $5 ${12} $4 $6 $3 $8 neo4j ghost2019

  # Run the Neo4J queries over custom-made ghost indexes
  
