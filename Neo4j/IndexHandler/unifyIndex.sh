#!/bin/bash

if [ "$JANUS_HOME" = "" ]; then
    echo "JANUS_HOME not set"
    exit 1
fi

if [ "$#" -ne 9 ]; then
    echo "Usage: <script.sh> <label-of-vertex> <index-name> <min-no-of-child-in-bTree> <column-no.-in-csv-to-index-on> <input-csv> <index-key-datatype(Date(D)/String(S)/Int(I))> <index-type(B-Tree(B)/B+Tree(BP))> <id-attribute-name> <attribute-name-to-index>" >&2
    exit 1
fi

g++ bTree/bTreeCreate.cpp -o indexCreate
if [ "$7" = "BP" ]; then
    g++ bPlusTree/bPlusTreeCreate.cpp -o indexCreate
fi

echo "[INFO]: Generating Index files";
./indexCreate $1 $2 $3 $4 $5 $6

echo "[INFO]: csv files generated"
#exit 0

PWD_ORIG=$(pwd)
TYPE="string"
if [ "$6" = "D" ]; then
    TYPE="date"
fi
echo "__$PWD_ORIG"
PWD_ORIG_STR="${PWD_ORIG//\//\\\/}"
echo "--$PWD_ORIG_STR"

sed -i 's/indexVertices.csv/'"${PWD_ORIG_STR}"'\/indexVertices.csv/' indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i 's/indexEdges.csv/'"${PWD_ORIG_STR}"'\/indexEdges.csv/' indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i 's/indexDataEdges.csv/'"${PWD_ORIG_STR}"'\/indexDataEdges.csv/' indexCreationScripts/read_frm_file_${TYPE}.gremlin
#echo "yo man ${TYPE} 0: $0 8: $8"
sed -i "s/'id'/'$8'/" indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i "s/'@ATTRIBUTE'/'$9'/" indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i "s/'@INDEX_TYPE'/'$7'/" indexCreationScripts/read_frm_file_${TYPE}.gremlin
echo "bye man";
sed -i 's/leafEdges.csv/'"${PWD_ORIG_STR}"'\/leafEdges.csv/' indexCreationScripts/read_frm_file_leaves.gremlin

cd $JANUS_HOME
echo ":load ${PWD_ORIG}/indexCreationScripts/read_frm_file_${TYPE}.gremlin" | bin/gremlin.sh

if [ "$7" = "BP" ]; then
    echo ":load ${PWD_ORIG}/indexCreationScripts/read_frm_file_leaves.gremlin" | bin/gremlin.sh
fi


### CLEAN UP
cd $PWD_ORIG
sed -i 's/'"${PWD_ORIG_STR}"'\/indexVertices.csv/indexVertices.csv/' indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i 's/'"${PWD_ORIG_STR}"'\/indexEdges.csv/indexEdges.csv/' indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i 's/'"${PWD_ORIG_STR}"'\/indexDataEdges.csv/indexDataEdges.csv/' indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i "s/'$8'/'id'/" indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i "s/'$9'/'@ATTRIBUTE'/" indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i "s/'$7'/'@INDEX_TYPE'/" indexCreationScripts/read_frm_file_${TYPE}.gremlin
sed -i 's/'"${PWD_ORIG_STR}"'\/leafEdges.csv/leafEdges.csv/' indexCreationScripts/read_frm_file_leaves.gremlin

echo "Cleanup done"

#rm indexCreate *.csv
