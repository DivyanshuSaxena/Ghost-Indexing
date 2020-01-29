#!/bin/bash
# Arguments:
# $1 : Label of vertex on which index is to be made
# $2 : Index name
# $3 : Branch size of B/BP Tree 
# $4 : Column in csv on which index is to be made
# $5 : Fullpath to the input csv
# $6 : Whether the index is (date (D)/string (S)/integer (I)) type
# $7 : Index Type (B/BP)
# $8 : Attribute on which index is to be made
# $9 : Fullpath to Neo4J Installation directory
# $10 : Username to access Neo4J
# $11 : Password to access Neo4J

if [ "$#" -ne 11 ]; then
    echo "Usage: <script.sh> <label-of-vertex> <index-name> 
    <min-no-of-child-in-bTree> <column-no.-in-csv-to-index-on> 
    <input-csv> <index-key-datatype(Date(D)/String(S)/Int(I))> 
    <index-type(B-Tree(B)/B+Tree(BP))> <attribute-name-to-index>
    <Neo4j Installation directory> <Neo4j username> <Neo4j password>" >&2
    exit 1
fi

g++ bTree/bTreeCreate.cpp -o indexCreate
if [ "$7" = "BP" ]; then
    g++ bPlusTree/bPlusTreeCreate.cpp -o indexCreate
fi

echo "[INFO]: Generating Index files";
./indexCreate $1 $2 $3 $4 $5 $6

echo "[INFO]: csv files generated";

# Generate the required ghost index files
pwdir=$(pwd)
pwdir_str="${pwdir//\//\\\/}"
sed -i 's/{INDEXHANDLER_DIR}/file:\/\/'"${pwdir_str}"'/g' add_ghost_index.cql
sed -i 's/{ATTRIBUTE_NAME}/'$8'/g' add_ghost_index.cql
sed -i 's/{INDEX_NAME}/'$2'/g' add_ghost_index.cql
sed -i 's/{INDEX_TYPE}/'$7'/g' add_ghost_index.cql
sed -i 's/{DATA_NODE}/'$1'/g' add_ghost_index.cql

# Modified cql files. Run ovr neo4j shell
$9/bin/cypher-shell -u ${10} -p ${11} < add_ghost_index.cql
echo "[INFO]: Ghost Indexes added";

# Clean up
sed -i 's/file:\/\/'$pwdir_str'/{INDEXHANDLER_DIR}/g' add_ghost_index.cql
sed -i 's/'$8'/{ATTRIBUTE_NAME}/g' add_ghost_index.cql
sed -i 's/'$7'/{INDEX_TYPE}/g' add_ghost_index.cql
sed -i 's/'$2'/{INDEX_NAME}/g' add_ghost_index.cql
sed -i 's/'$1'/{DATA_NODE}/g' add_ghost_index.cql
