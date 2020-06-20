#!/bin/bash

# Script to run the Performance Tester class in GhostIndex/Utilities project
# Arguments:
# $1 : Query number for which the test is to be run
# $2 : Label of vertex on which index is to be made
# $3 : Label of vertex on which index edge is to be made
# $4 : Attribute on which index is to be made
# $5 : Whether the index is (date (D)/string (S)/integer (I)) type
# $6 : Column in csv on which index is to be made (Count starting from 1)
# $7 : Index Type (B/BP)
# $8 : Branch size of B/BP Tree 
# $9 : Batch size before clearing Neo4j Cache
# $10 : Name of the input csv
# $11 : Fullpath to Neo4J Installation directory
# $12 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_neo_<number>]
# $13 : Fullpath to ldbc_snb_implementations folder
# $14 : Fullpath to Ghost-Indexing/Neo4j folder
# $15 : Cache size for which Neo4J is running

if [ "$#" -ne 15 ]; then
  echo "Usage: <script.sh> <query-no> <label-of-vertex-in-index> <label-of-vertex-in-edge>
  <attribute-name-to-index> <index-key-datatype(Date(D)/String(S)/Int(I))>
  <column-no.-in-csv-to-index-on> <index-type(B-Tree(B)/B+Tree(BP))>
  <min-no-of-child-in-bTree> <batch-size-for-caching> <input-csv> 
  <Neo4j-Installation-directory> <ldbc-snb-data-path> 
  <ldbc-snb-implementations-path> <Ghost-Indexing-Neo4j-folder-path> <neo4j-cache-size>" >&2
  exit 1
fi

datasets=( 1000 2500 4000 7500 )
#datasets=( 1000 )
cwd=$(pwd)
export NEO4J_HOME=${11}
export NEO4J_DB_DIR=$NEO4J_HOME/data/databases/graph.ghostdb  
export POSTFIX=_0_0.csv

for dataset in ${datasets[*]}; do
	echo "=================================================="
	echo "Starting for dataset: "$dataset
	echo "--------------------------------------------------"

  export NEO4J_DATA_DIR=${12}/social_network_neo_$dataset/
  cd ${13}/cypher/load-scripts
  # Deletes previous data in Neo4J, rewrites the new dataset and start Neo4j
  ./load-in-one-step.sh

  # Sleep and wait for Neo4j to start
  echo "Wait for Neo4j to start"
  sleep 5s
  echo "Waited for 5s. Hopefully, Neo4j started"

  # Run the Neo4J queries over Lucene Indexes
  cd ${14}/queries
  ./run_all_params.sh $1 $dataset 0 $9 ${11} ${14}/../JanusGraph/GhostIndex ${13}/cypher/queries ${15} neo4j ghost2019

  # Generate the ghost indexes
  cd ${14}/IndexHandler
  echo "time ./unifyIndex.sh $2 $3 $3_$4_$7_$8 $8 $6 ${12}/social_network_neo_$dataset/${10} $5 $7 $4 ${11} neo4j ghost2019"
  time ./unifyIndex.sh $2 $3 $3_$4_$7_$8 $8 $6 ${12}/social_network_neo_$dataset/${10} $5 $7 $4 ${11} neo4j ghost2019

  # Run the Neo4J queries over custom-made ghost indexes
  cd ${14}/queries
  ./run_all_params.sh $1 $dataset 1 $9 ${11} ${14}/../JanusGraph/GhostIndex ${14}/ghost-queries ${15} neo4j ghost2019
done

echo "Done"
