#include <stdio.h>
#include <iostream>
#include <limits.h>
#include <bits/stdc++.h>


using namespace std;

map<string, vector<long long> > dataNodes;
char* label;
char* indexName;

int minChild;
string minVal,maxVal;

ofstream indexEdges("indexEdges.csv");
ofstream indexDataEdges("indexDataEdges.csv");
ofstream leafEdges("leafEdges.csv");
ofstream indexVert("indexVertices.csv");

int counter = 1;

// A BPlusTree node
class BPlusTreeNode {
    int nodeId;
    string *keys; // An array of keys
    int t;     // Minimum degree (defines the range for number of keys)
    BPlusTreeNode **C; // An array of child pointers
    int n;     // Current number of keys
    bool leaf; // Is true when node is leaf. Otherwise false
    BPlusTreeNode *nextLeaf;
    bool isLastLeaf;
public:
    BPlusTreeNode(int _t, bool _leaf); // Constructor

    // A utility function to insert a new key in the subtree rooted with
    // this node. The assumption is, the node must be non-full when this
    // function is called
    pair<string, BPlusTreeNode*> insert(string k);

    // A utility function to split the child y of this node. i is index of y in
    // child array C[]. The Child y must be full when this function is called
    pair<string, BPlusTreeNode*> splitNode();

    // A function to traverse all nodes in a subtree rooted with this node
    void traverse(string minKey, string maxKey);

    void traverseLeaf();

// Make BPlusTree friend of this so that we can access private members of this
// class in BPlusTree functions
friend class BPlusTree;
};

// A BPlusTree
class BPlusTree
{
    BPlusTreeNode *root; // Pointer to root node
    BPlusTreeNode *initNode;
    int t; // Minimum degree
public:
    // Constructor (Initializes tree as empty)
    BPlusTree(int _t)
    { root = NULL; t = _t; }

    // function to traverse the tree
    void traverse() {
        if (root != NULL) {
            root->nodeId = 0;
            root->traverse(minVal, maxVal);
        }
    }

    void traverseLeaf() {
        if (initNode != NULL) initNode->traverseLeaf();
    }

    // The main function that inserts a new key in this B-Tree
    void insert(string k);
};

// Constructor for BPlusTreeNode class
BPlusTreeNode::BPlusTreeNode(int t1, bool leaf1)
{
    // Copy the given minimum degree and leaf property
    t = t1;
    leaf = leaf1;
    nodeId = counter ++;

    if (counter < 0) {
        cout << "PANIC" << endl;
        cout << "Node Limit exceeded" << endl;
    }

    // Allocate memory for maximum number of possible keys
    // and child pointers
    keys = new string[2*t-1];
    C = new BPlusTreeNode *[2*t];

    // Initialize the number of keys as 0
    n = 0;
}

// The main function that inserts a new key in this B-Tree
void BPlusTree::insert(string k)
{
    // If tree is empty
    if (root == NULL) {
        // Allocate memory for root
        initNode = root = new BPlusTreeNode(t, true);
        root->keys[0] = k; // Insert key
        root->n = 1; // Update number of keys in root
        root->isLastLeaf = true;
    }
    else {
        pair<string, BPlusTreeNode*> newNode = root->insert(k);
        if (newNode.second != NULL) {
            BPlusTreeNode *s = new BPlusTreeNode(t, false);
            s->C[0] = root;
            s->keys[0] = newNode.first;
            s->C[1] = newNode.second;
            s->n = 1;
            root = s;
        }
    }
}

pair<string, BPlusTreeNode*> BPlusTreeNode::splitNode() {
    BPlusTreeNode *z = new BPlusTreeNode(t, leaf);
    if (leaf) {
        z->isLastLeaf = isLastLeaf;
        z->nextLeaf = nextLeaf;
        isLastLeaf = false;
        nextLeaf = z;
    }
    z->n = t - 1;

    for (int j = 0; j < t-1; j++)
        z->keys[j] = keys[j+t];

    n = t;
    if (!leaf) {
        for (int j = 0; j < t; j++)
            z->C[j] = C[j+t];
        n = t - 1;
        return make_pair(keys[t-1], z);
    }
    
    return make_pair(z->keys[0], z);
}

pair<string, BPlusTreeNode*> BPlusTreeNode::insert(string k) {
    int i = n-1;
    if (leaf) {
        if (n == 2*t-1) {
            while (i >= 0 && keys[i] > k)
                i--;
            pair<string, BPlusTreeNode*> newNode = splitNode();
            if (i < t) {
                int j=t-1;
                while (j>i) {
                    keys[j+1] = keys[j];
                    j--;
                }
                keys[i+1] = k;
                n++;
            }
            else {
                int j=t-2;
                i -= t; 
                BPlusTreeNode* modNode = newNode.second;
                while (j>i) {
                    modNode->keys[j+1] = modNode->keys[j];
                    j--;
                }
                modNode->keys[i+1] = k;
                modNode->n++;
            }
            return newNode;
        }
        else {
            while (i >= 0 && keys[i] > k) {
                keys[i+1] = keys[i];
                i--;
            }
            keys[i+1] = k;
            n++;
            return make_pair("", (BPlusTreeNode*) NULL);
        }
    }
    else {
        while (i >= 0 && keys[i] > k)
            i--;
        pair<string, BPlusTreeNode*> newChild = C[i+1]->insert(k);
        if (newChild.second == NULL)
            return newChild;
        else if (n == 2*t-1) {
            pair<string, BPlusTreeNode*> newNode = splitNode();
            if (i < t-1) {
                int j=t-2;
                while (j>i) {
                    keys[j+1] = keys[j];
                    C[j+2] = C[j+1];
                    j--;
                }
                keys[i+1] = newChild.first;
                C[i+2] = newChild.second;
                n++;
            }
            else {
                int j=t-2;
                i -= t; 
                BPlusTreeNode* modNode = newNode.second;
                while (j>i) {
                    modNode->keys[j+1] = modNode->keys[j];
                    modNode->C[j+2] = modNode->C[j+1];
                    j--;
                }
                modNode->keys[i+1] = newChild.first;
                modNode->C[i+2] = newChild.second;
                modNode->n++;
            }
            return newNode;
        }
        else {
            int j=n-1;
            while (j>i) {
                keys[j+1] = keys[j];
                C[j+2] = C[j+1];
                j--;
            }
            keys[i+1] = newChild.first;
            C[i+2] = newChild.second;
            n++;
            return make_pair("", (BPlusTreeNode*) NULL);
        }
    }
}

// Function to traverse all nodes in a subtree rooted with this node
void BPlusTreeNode::traverse(string minKey, string maxKey) {

    int vnum = nodeId;
    indexVert << vnum << "|" << indexName << endl;
    int i;
    if (!leaf) {
        
        for (i = 0; i < n; i++) {
            // If this is not leaf, then before printing key[i],
            // traverse the subtree rooted with child C[i].
            if (i == 0)
                C[i]->traverse(minKey, keys[i]);
            else
                C[i]->traverse(keys[i-1], keys[i]);
        }
        if (i == 0)
            C[i]->traverse(minKey, maxKey);
        else
            C[i]->traverse(keys[i-1], maxKey);
        
        string prev = minKey;
        for (i = 0; i < n; i++) {
            int childNum = C[i]->nodeId;
            indexEdges << vnum << "|" << childNum << "|" << prev << "|" << keys[i] << "|" << indexName << endl;
            prev = keys[i];
        }

        // Print the subtree rooted with last child
        int childNum = C[i]->nodeId;
        indexEdges << vnum << "|" << childNum << "|" << prev << "|" << maxKey << "|" << indexName << endl;
    }
    else {
        for (i = 0; i < n; i++) {
            string val = keys[i];
            for (int j=0; j<dataNodes[val].size(); j++) {
                indexDataEdges << vnum << "|" << dataNodes[val][j] << "|" << label << "|" << val << "|" << indexName << endl;
            }
        }
    }
}

void BPlusTreeNode::traverseLeaf() {
    if (nextLeaf == NULL)
        return;
    leafEdges << nodeId << "|" << nextLeaf->nodeId << "|" << indexName << endl;
    nextLeaf->traverseLeaf();
}

int main(int argc, char** argv) {
    if (argc < 7) {
        cout << "Usage: ./<executable> <label-of-vertex> <index-name> <min-no-of-child-in-bTree> <column-no.-in-csv-to-index-on> <input-csv> <index-key-datatype(Date(D)/String(S)/Int(I))>"<<endl;
        return 0;
    }
    label = argv[1];
    indexName = argv[2];
    minChild = atoi(argv[3]);
    int paramNo = atoi(argv[4]);
    char* inFile = argv[5];
    char dataInpType = argv[6][0];
    
    if (dataInpType == 'D') {
        minVal = "1990-01-01T01:01:01.000+0000";
        maxVal = "2018-01-01T01:01:01.000+0000";
    }
    else if (dataInpType == 'S') {
        minVal = " ";
        maxVal = "zzzzzzzzzzzzzzzzzzzzzzzzzz";
    }
    else if (dataInpType == 'I') {
        ostringstream oss;
        oss << LLONG_MAX;
        maxVal = oss.str();
        minVal = "0";
        minVal.insert(0, maxVal.length() - minVal.length(), '0');
    }
    else {
            cout << "invalid datatype - '" << dataInpType << "'" << endl;
            return 0;
    }

    ifstream infile; 
    infile.open(inFile);

    BPlusTree bTreeVar(minChild); // A B-Tree with minium degree 3
    
    string key;
    string inpLine;
    long long value;
    while (getline(infile, inpLine)) {
        int idx=0;
        char tmp;
        value = 0;

        tmp = inpLine[idx++];
        while (tmp != '|') {
            value *= 10;
            value += (tmp - '0');
            tmp = inpLine[idx++];
        }
        string key;
        if (paramNo > 1) {
            int countTmp = 2;
            while (countTmp < paramNo) {
                tmp = inpLine[idx++];
                if (tmp == '|') {
                    countTmp ++;
                }
            }
            tmp = inpLine[idx++];
            while (tmp && tmp != '|') {
                key.append(1, tmp);
                tmp = inpLine[idx++];
            }
        }
        else
            key = value;

        if (dataInpType == 'I')
            key.insert(0, maxVal.length() - key.length(), '0');

        if (dataNodes.find(key) == dataNodes.end()) {
            vector<long long> v;
            dataNodes[key] = v;
            bTreeVar.insert(key);
        }
        dataNodes[key].push_back(value);
    }
    bTreeVar.traverse();
    bTreeVar.traverseLeaf();
    // Create link to index-root
    infile.close();
    indexVert.close();
    indexEdges.close();
    indexDataEdges.close();
    leafEdges.close();
}
