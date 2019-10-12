if [ "$JANUS_HOME" = "" ]; then
    echo "JANUS_HOME not set"
    exit 1
fi

if [ "$#" -ne 5 ]; then
    echo "Usage: <script.sh> <label-of-vertex> <index-name> <min-no-of-child-in-bTree> <column-no.-in-csv-to-index-on> <input-csv>" >&2
    exit 1
fi

g++ bPlusTree/bPlusTreeCreate.cpp -o indexCreate
./indexCreate $1 $2 $3 $4 $5 D

PWD=$(pwd)
sed -i "s/indexVertices.csv/$PWD\/indexVertices.csv" indexCreationScripts/read_frm_file_date.gremlin
sed -i "s/indexEdges.csv/$PWD\/indexEdges.csv" indexCreationScripts/read_frm_file_date.gremlin
sed -i "s/indexDataEdges.csv/$PWD\/indexDataEdges.csv" indexCreationScripts/read_frm_file_date.gremlin
sed -i "s/indexDataEdges.csv/$PWD\/indexDataEdges.csv" indexCreationScripts/read_frm_file_date.gremlin
sed -i "s/leafEdges.csv/$PWD\/leafEdges.csv" indexCreationScripts/read_frm_file_leaves.gremlin

cd $JANUS_HOME
bin/gremlin.sh -e $PWD/read_frm_file_date.gremlin

### CLEAN UP
cd $PWD
sed -i "s/$PWD\/indexVertices.csv/indexVertices.csv" indexCreationScripts/read_frm_file_date.gremlin
sed -i "s/$PWD\/indexEdges.csv/indexEdges.csv" indexCreationScripts/read_frm_file_date.gremlin
sed -i "s/$PWD\/indexDataEdges.csv/indexDataEdges.csv" indexCreationScripts/read_frm_file_date.gremlin
sed -i "s/$PWD\/leafEdges.csv/leafEdges.csv" indexCreationScripts/read_frm_file_leaves.gremlin

#rm indexCreate *.csv
