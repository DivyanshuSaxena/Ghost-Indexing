#include <stdio.h>
#include <iostream>
#include <limits.h>
#include <bits/stdc++.h>


using namespace std;

map<string, vector<long long> > dataNodes;

int minChild;
char* label;
char* indexName;

string minVal,maxVal;

ofstream indexVert("indexVertices.csv");
ofstream indexEdges("indexEdges.csv");
ofstream indexDataEdges("indexDataEdges.csv");

int counter = 0;

// A BTree node
class BTreeNode
{
	string *keys; // An array of keys
	int t;	 // Minimum degree (defines the range for number of keys)
	BTreeNode **C; // An array of child pointers
	int n;	 // Current number of keys
	bool leaf; // Is true when node is leaf. Otherwise false
public:
	BTreeNode(int _t, bool _leaf); // Constructor

	// A utility function to insert a new key in the subtree rooted with
	// this node. The assumption is, the node must be non-full when this
	// function is called
	void insertNonFull(string k);

	// A utility function to split the child y of this node. i is index of y in
	// child array C[]. The Child y must be full when this function is called
	void splitChild(int i, BTreeNode *y);

	// A function to traverse all nodes in a subtree rooted with this node
	int traverse(int vnum);

	// A function to search a key in subtree rooted with this node.
	BTreeNode *search(string k); // returns NULL if k is not present.

// Make BTree friend of this so that we can access private members of this
// class in BTree functions
friend class BTree;
};

// A BTree
class BTree
{
	BTreeNode *root; // Pointer to root node
	int t; // Minimum degree
public:
	// Constructor (Initializes tree as empty)
	BTree(int _t)
	{ root = NULL; t = _t; }

	// function to traverse the tree
	void traverse()
	{ if (root != NULL) root->traverse(0); }

	// function to search a key in this tree
	BTreeNode* search(string k)
	{ return (root == NULL)? NULL : root->search(k); }

	// The main function that inserts a new key in this B-Tree
	void insert(string k);
};

// Constructor for BTreeNode class
BTreeNode::BTreeNode(int t1, bool leaf1)
{
	// Copy the given minimum degree and leaf property
	t = t1;
	leaf = leaf1;

	// Allocate memory for maximum number of possible keys
	// and child pointers
	keys = new string[2*t-1];
	C = new BTreeNode *[2*t];

	// Initialize the number of keys as 0
	n = 0;
}

// Function to traverse all nodes in a subtree rooted with this node
int BTreeNode::traverse(int vnum)
{
	// There are n keys and n+1 children, travers through n keys
	// and first n children

    
	int i;
    for (i = 0; i < n; i++)
	{
		// If this is not leaf, then before printing key[i],
		// traverse the subtree rooted with child C[i].
		if (leaf == false) {
			C[i]->traverse(vnum * (2*minChild + 1) + i + 1);
        }
    }
	if (leaf == false) {
		C[i]->traverse(vnum * (2*minChild + 1) + i + 1);
    }
    indexVert << vnum << "|" << indexName << endl;
    
    string prev = minVal;
    for (i = 0; i < n; i++)
	{
        int childNum = vnum * (2*minChild + 1) + i + 1;
        if (leaf == false) {
                indexEdges << vnum << "|" << childNum << "|" << prev << "|" << keys[i] << "|" << indexName << endl;
        }
        prev = keys[i];
    }

	// Print the subtree rooted with last child
	if (leaf == false) {
		int childNum = vnum * (2*minChild+1) + i + 1;
        indexEdges << vnum << "|" << childNum << "|" << prev << "|" << maxVal << "|" << indexName << endl;
    }

    {
        for (i = 0; i < n; i++) {
            string val = keys[i];
            for (int j=0; j<dataNodes[val].size(); j++) {
                indexDataEdges << vnum << "|" << dataNodes[val][j] << "|" << label << "|" << val << "|" << indexName << endl;
            }
        }
    }
    
    return vnum;
}

// Function to search key k in subtree rooted with this node
BTreeNode *BTreeNode::search(string k)
{
	// Find the first key greater than or equal to k
	int i = 0;
	while (i < n && k > keys[i])
		i++;

	// If the found key is equal to k, return this node
	if (keys[i] == k)
		return this;

	// If key is not found here and this is a leaf node
	if (leaf == true)
		return NULL;

	// Go to the appropriate child
	return C[i]->search(k);
}

// The main function that inserts a new key in this B-Tree
void BTree::insert(string k)
{
	// If tree is empty
	if (root == NULL)
	{
		// Allocate memory for root
		root = new BTreeNode(t, true);
		root->keys[0] = k; // Insert key
		root->n = 1; // Update number of keys in root
	}
	else // If tree is not empty
	{
		// If root is full, then tree grows in height
		if (root->n == 2*t-1)
		{
			// Allocate memory for new root
			BTreeNode *s = new BTreeNode(t, false);

			// Make old root as child of new root
			s->C[0] = root;

			// Split the old root and move 1 key to the new root
			s->splitChild(0, root);

			// New root has two children now. Decide which of the
			// two children is going to have new key
			int i = 0;
			if (s->keys[0] < k)
				i++;
			s->C[i]->insertNonFull(k);

			// Change root
			root = s;
		}
		else // If root is not full, call insertNonFull for root
			root->insertNonFull(k);
	}
}

// A utility function to insert a new key in this node
// The assumption is, the node must be non-full when this
// function is called
void BTreeNode::insertNonFull(string k)
{
	// Initialize index as index of rightmost element
	int i = n-1;

	// If this is a leaf node
	if (leaf == true)
	{
		// The following loop does two things
		// a) Finds the location of new key to be inserted
		// b) Moves all greater keys to one place ahead
		while (i >= 0 && keys[i] > k)
		{
			keys[i+1] = keys[i];
			i--;
		}

		// Insert the new key at found location
		keys[i+1] = k;
		n = n+1;
	}
	else // If this node is not leaf
	{
		// Find the child which is going to have the new key
		while (i >= 0 && keys[i] > k)
			i--;

		// See if the found child is full
		if (C[i+1]->n == 2*t-1)
		{
			// If the child is full, then split it
			splitChild(i+1, C[i+1]);

			// After split, the middle key of C[i] goes up and
			// C[i] is splitted into two. See which of the two
			// is going to have the new key
			if (keys[i+1] < k)
				i++;
		}
		C[i+1]->insertNonFull(k);
	}
}

// A utility function to split the child y of this node
// Note that y must be full when this function is called
void BTreeNode::splitChild(int i, BTreeNode *y)
{
	// Create a new node which is going to store (t-1) keys
	// of y
	BTreeNode *z = new BTreeNode(y->t, y->leaf);
	z->n = t - 1;

	// Copy the last (t-1) keys of y to z
	for (int j = 0; j < t-1; j++)
		z->keys[j] = y->keys[j+t];

	// Copy the last t children of y to z
	if (y->leaf == false)
	{
		for (int j = 0; j < t; j++)
			z->C[j] = y->C[j+t];
	}

	// Reduce the number of keys in y
	y->n = t - 1;

	// Since this node is going to have a new child,
	// create space of new child
	for (int j = n; j >= i+1; j--)
		C[j+1] = C[j];

	// Link the new child to this node
	C[i+1] = z;

	// A key of y will move to this node. Find location of
	// new key and move all greater keys one space ahead
	for (int j = n-1; j >= i; j--)
		keys[j+1] = keys[j];

	// Copy the middle key of y to this node
	keys[i] = y->keys[t-1];

	// Increment count of keys in this node
	n = n + 1;
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

    BTree bTreeVar(minChild); // A B-Tree with minium degree 3
    
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

    // Create link to index-root
    infile.close();
    indexVert.close();
    indexEdges.close();
    indexDataEdges.close();
}
