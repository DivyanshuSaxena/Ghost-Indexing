# Ghost-Indexing

Implementation of addition of Ghost Indexes in JanusGraph and Neo4j Graph Databases

## Benchmark

The queries used in the current implementation have been taken from the LDBC Social Network Benchmarking Dataset.

### Versions for JanusGraph and Neo4J

Since both JanusGraph and Neo4J are projects undergoing active changes. We have stuck to the following versions for these frameworks: 

- **JanusGraph**: version 0.4.0 (using Cassandra as storage backend, and Elasticsearch as indexing backend)
- **Neo4J**: community edition 3.2

## Directory Structure

```shell
.
├── IndexForQueries.txt
├── JanusGraph
├── Neo4j
└── Scripts

```

- `IndexForQueries.txt`: Lists the Indexes which can possibly be used for implementing ghost indexes for each of the queries mentioned in the Business Intelligence Workload for the SNB Dataset.
- `JanusgGraph`: Holds the JanusGraph implementation for loading the original data onto the graph database, creating index nodes, and implementations for a subset of queries from the Business Intelligence workload.
- `Neo4J`: Holds the implementation for creating index nodes on Neo4J graph database, and ghost index implementations for a subset of queries from the Business Intelligence Workload.
- `Scripts`: Contains various scripts for running complete experiments (all in one scripts for loading data node and ghost indexes and running queries), and plotting results. If using for the first time, please refer to these scripts before going into other directories.

For individual directories, refer to each of these folders.

## Authors

- [Divyanshu Saxena](https://github.com/DivyanshuSaxena)
- [Srijan Sinha](https://github.com/srijan-sinha)

Supervised by:
[Prof. Srikanta Bedathur](http://www.cse.iitd.ac.in/~srikanta/)