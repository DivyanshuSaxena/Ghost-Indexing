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
# $8 : Name of the input csv
# $9 : Fullpath to Neo4J Installation directory
# $10 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_neo_<number>]
# $11 : Fullpath to ldbc_snb_implementations folder
# $12 : Fullpath to Ghost-Indexing/Neo4j folder

if [ "$#" -ne 12 ]; then
  echo "Usage: <script.sh> <query-no> <label-of-vertex-in-index>
  <attribute-name-to-index> <index-key-datatype(Date(D)/String(S)/Int(I))>
  <column-no.-in-csv-to-index-on> <index-type(B-Tree(B)/B+Tree(BP))>
  <min-no-of-child-in-bTree> <input-csv> <Neo4j-Installation-directory> 
  <ldbc-snb-data-path> <ldbc-snb-implementations-path>
  <Ghost-Indexing-Neo4j-folder-path>" >&2
  exit 1
fi

datasets=( 2500 )
cwd=$(pwd)
export NEO4J_HOME=$9
export NEO4J_DB_DIR=$NEO4J_HOME/data/databases/graph.ghostdb  
export POSTFIX=_0_0.csv

for dataset in ${datasets[*]}; do
	echo "=================================================="
	echo "Starting for dataset: "$dataset
	echo "--------------------------------------------------"

  export NEO4J_DATA_DIR=${10}/social_network_neo_$dataset/
  cd ${11}/cypher/load-scripts
  # Deletes previous data in Neo4J, rewrites the new dataset and start Neo4j
  ./load-in-one-step.sh

  # Sleep and wait for Neo4j to start
  echo "Wait for Neo4j to start"
  sleep 5s
  echo "Waited for 5s. Hopefully, Neo4j started"

  # Run the Neo4J queries over Lucene Indexes
  cd ${12}/queries
  ./run_all_params.sh $1 $dataset 0 $9/bin/cypher-shell ${12}/../JanusGraph/GhostIndex ${11}/cypher/queries neo4j ghost2019

  # Generate the ghost indexes
  cd ${12}/IndexHandler
  echo "time ./unifyIndex.sh $2 $3_index_$6_$7 $7 $5 ${10}/social_network_neo_$dataset/$8 $4 $6 $3 $9 neo4j ghost2019"
  time ./unifyIndex.sh $2 $3_index_$6_$7 $7 $5 ${10}/social_network_neo_$dataset/$8 $4 $6 $3 $9 neo4j ghost2019

  # Run the Neo4J queries over custom-made ghost indexes
  cd ${12}/queries
  ./run_all_params.sh $1 $dataset 1 $9/bin/cypher-shell ${12}/../JanusGraph/GhostIndex ${12}/ghost-queries neo4j ghost2019
done

echo "Done"