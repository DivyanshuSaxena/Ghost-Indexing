#include <bits/stdc++.h>

using namespace std;


int main(int argc, char** argv) {
    char* indexName = argv[1];
    int searchVal = atoi(argv[2]);

    // Finding the right index from index-root 
    cout << "_vertex = _rootIndex.outE(\"INDEX_EDGE\", \"name\", "<<indexName<<").inV().next()" << endl;

    cout << "_searchVal = " << searchVal << endl;
    
    cout << "while (_vertex.hasLabel(\"INDEX\").size() > 0) {" << endl;
    cout << "   _result = _vertex.outE(\"INDEX_DATA_EDGE\", \"val\", _searchVal).inV()";
    cout << "   if (_result.size() > 0) {" << endl;
    cout << "       break;" << endl;
    cout << "   }" << endl;
    cout << "   _vertex.outE(\"INDEX_EDGE\").filter{(it.get.value(\"min\") < _searchVal) && (it.get.value(\"max\") > _searchVal)}.inV();" << endl;
    cout << "}" << endl;

}

