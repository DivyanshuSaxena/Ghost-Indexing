#include <bits/stdc++.h>

using namespace std;


int main(int argc, char** argv) {
    char* indexName = argv[1];
    int searchMin = atoi(argv[2]);
    int searchMax = atoi(argv[3]);

    // Finding the right index from index-root 
    cout << "_vertex = _rootIndex.outE(\"INDEX_EDGE\", \"name\", "<<indexName<<").inV().next()" << endl;

    
    cout << "_searchMin = " << searchMin << endl;
    cout << "_searchMax = " << searchMax << endl;
    
    cout << "while (_vertex.has(label, \"INDEX\").size > 0) {" << endl;
    cout << "    _vertex = _vertex.outE(\"INDEX_EDGE\").filter{!(it.get.value(\"min\") > _searchMax) && !(it.get.value(\"max\") < _searchMin)}.inV();" << endl;
    cout << "}" << endl;

    cout << "_result = vertex.outE(\"INDEX_DATA_EDGE\", \"data\", _searchVal).inV()"<<endl;
}

