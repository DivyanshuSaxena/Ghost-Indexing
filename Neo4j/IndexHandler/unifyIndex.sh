#!/bin/bash
# Arguments:
# $1 : Label of vertex on which index is to be made
# $2 : Label of vertex on which index edge is to be made
# $3 : Index name
# $4 : Branch size of B/BP Tree 
# $5 : Column in csv on which index is to be made
# $6 : Fullpath to the input csv
# $7 : Whether the index is (date (D)/string (S)/integer (I)) type
# $8 : Index Type (B/BP)
# $9 : Attribute on which index is to be made
# $10 : Fullpath to Neo4J Installation directory
# $11 : Username to access Neo4J
# $12 : Password to access Neo4J

if [ "$#" -ne 12 ]; then
    echo "Usage: <script.sh> <label-of-vertex-in-index> <label-of-vertex-in-edge>
    <index-name> <min-no-of-child-in-bTree> <column-no.-in-csv-to-index-on> 
    <input-csv> <index-key-datatype(Date(D)/String(S)/Int(I))> 
    <index-type(B-Tree(B)/B+Tree(BP))> <attribute-name-to-index>
    <Neo4j Installation directory> <Neo4j username> <Neo4j password>" >&2
    exit 1
fi

g++ bTree/bTreeCreate.cpp -o indexCreate
if [ "$8" = "BP" ]; then
    g++ bPlusTree/bPlusTreeCreate.cpp -o indexCreate
fi

echo "[INFO]: Generating Index files";
./indexCreate $2 $3 $4 $5 $6 $7

echo "[INFO]: csv files generated";

# Generate the required ghost index files
pwdir=$(pwd)
pwdir_str="${pwdir//\//\\\/}"
sed -i 's/{INDEXHANDLER_DIR}/file:\/\/'"${pwdir_str}"'/g' add_ghost_index.cql
sed -i 's/{DATA_NODE}/'$1'/g' add_ghost_index.cql

# Modified cql files. Run ovr neo4j shell
${10}/bin/cypher-shell -u ${11} -p ${12} < add_ghost_index.cql
echo "[INFO]: Ghost Indexes added";

sed -i 's/{INDEXHANDLER_DIR}/file:\/\/'"${pwdir_str}"'/g' add_ghost_edges.cql
sed -i 's/{ATTRIBUTE_NAME}/'$9'/g' add_ghost_edges.cql
sed -i 's/{INDEX_NAME}/'$3'/g' add_ghost_edges.cql
sed -i 's/{INDEX_TYPE}/'$8'/g' add_ghost_edges.cql
sed -i 's/{DATA_NODE}/'$1'/g' add_ghost_edges.cql

# Modified cql files. Run ovr neo4j shell
echo "[INFO]: Wait for 1 minute before starting ghost edges"
sleep 60s
echo "[INFO]: Starting Ghost Edges"
${10}/bin/cypher-shell -u ${11} -p ${12} < add_ghost_edges.cql
echo "[INFO]: Ghost Edges added";

# Clean up
sed -i 's/file:\/\/'$pwdir_str'/{INDEXHANDLER_DIR}/g' add_ghost_index.cql
sed -i 's/'$1'/{DATA_NODE}/g' add_ghost_index.cql

sed -i 's/file:\/\/'$pwdir_str'/{INDEXHANDLER_DIR}/g' add_ghost_edges.cql
sed -i 's/'$3'/{INDEX_NAME}/g' add_ghost_edges.cql
sed -i 's/'$9'/{ATTRIBUTE_NAME}/g' add_ghost_edges.cql
sed -i 's/'$8'/{INDEX_TYPE}/g' add_ghost_edges.cql
sed -i 's/'$1'/{DATA_NODE}/g' add_ghost_edges.cql
