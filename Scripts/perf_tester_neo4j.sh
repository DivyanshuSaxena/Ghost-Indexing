#!/bin/bash

# Script to run the Performance Tester class in GhostIndex/Utilities project
# Arguments:
# $1 : Query number for which the test is to be run
# $2 : Label of vertex on which index is to be made
# $3 : Attribute on which index is to be made
# $4 : Whether the index is (date (D)/string (S)/integer (I)) type
# $5 : Column in csv on which index is to be made (Count starting from 1)
# $6 : Index Type (B/BP)
# $7 : Branch size of B/BP Tree 
# $8 : Parameter for which date conversion is to be done (-1 if no date param conversion needed)
# $9 : Name of the input csv
# $10 : Fullpath to Neo4J Installation directory
# $11 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_neo_<number>]
# $12 : Fullpath to ldbc_snb_implementations folder
# $13 : Fullpath to Ghost-Indexing/Neo4j folder

datasets=( 1000 2500 4000 7500 )
cwd=$(pwd)
export NEO4J_HOME=${10}
export NEO4J_DB_DIR=$NEO4J_HOME/data/databases/graph.ghostdb  
export POSTFIX=_0_0.csv

for dataset in ${datasets[*]}; do
	echo "=================================================="
	echo "Starting for dataset: "$dataset
	echo "--------------------------------------------------"

  export NEO4J_DATA_DIR=${11}/social_network_neo_$dataset/
  cd ${12}/cypher/load-scripts
  # Deletes previous data in Neo4J, rewrites the new dataset and start Neo4j
  ./load-in-one-step.sh

  # Run the Neo4J queries over Lucene Indexes
  cd ${13}/queries
  ./run_all_params.sh $1 $8 $dataset 0 ${10}/bin/cypher-shell ${13}/../JanusGraph/GhostIndex ${12}/cypher/queries neo4j ghost2019

  # Generate the ghost indexes
  cd ${13}/IndexHandler
  echo "time ./unifyIndex.sh $2 $3_index_$6_$7 $7 $5 ${11}/social_network_neo_$dataset/$9 $4 $6 $3 ${10} neo4j ghost2019"
  time ./unifyIndex.sh $2 $3_index_$6_$7 $7 $5 ${11}/social_network_neo_$dataset/$9 $4 $6 $3 ${10} neo4j ghost2019

  # Run the Neo4J queries over custom-made ghost indexes
  cd ${13}/queries
  ./run_all_params.sh $1 $8 $dataset 1 ${10}/bin/cypher-shell ${13}/../JanusGraph/GhostIndex ${13}/ghost-queries neo4j ghost2019
done

echo "Done"