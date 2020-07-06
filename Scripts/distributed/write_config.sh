#!/bin/bash

# Arguments:
	# $1 : Fullpath to Ghost-Indexing/Janusgraph folder
	# $2 : Fullpath to Elasticsearch installation directory
	# $3 : Node name [node-m | node-1 | node-2 | node-3 | node-4]
	# $4 : dataset size [1000 | 2500 | 4000 | 7500 | 10000]
	# $5 : ghostindex or not [0 | 1]

cwd=$(pwd)

# Set node ip
ip="10.17.5.53";
masternode="false"
datanode="true"
if [[ $3 = "node-1" ]]; then
	ip="10.17.50.89"
elif [[ $3 = "node-2" ]]; then
	ip="10.17.50.201"
elif [[ $3 = "node-3" ]]; then
	ip="10.17.50.127"
elif [[ $3 = "node-4" ]]; then
	ip="10.17.50.228"
elif [[ $3 = "node-m" ]]; then
	masternode="true"
	datanode="false"
fi

cd $1/GhostIndex/conf/distributed/
rm -rf cassandra.yaml
rm -rf elasticsearch.yml

if [[ $5 -ne 1 ]]; then
	# Write cassandra config
	sed "s|@DATA|/var/lib/cassandra/$4/data|g" cassandra_unsub.yaml > cassandra.yaml
	sed -i "s|@COMMITLOG|/var/lib/cassandra/$4/commitlog|g" cassandra.yaml
	sed -i "s|@SAVEDCACHES|/var/lib/cassandra/$4/saved_caches|g" cassandra.yaml
	# sed "s|@DATA|/var/lib/cassandra/data|g" cassandra_unsub.yaml > cassandra.yaml
	# sed -i "s|@COMMITLOG|/var/lib/cassandra/commitlog|g" cassandra.yaml
	# sed -i "s|@SAVEDCACHES|/var/lib/cassandra/saved_caches|g" cassandra.yaml
	sed -i "s|@NODEIP|$ip|g" cassandra.yaml

	# Write elasticsearch config
	sed "s|@LOGS|$2/logs/$4|g" elasticsearch_unsub.yml > elasticsearch.yml
	sed -i "s|@NODEMASTER|$masternode|g" elasticsearch.yml
	sed -i "s|@NODEDATA|$datanode|g" elasticsearch.yml
	sed -i "s|@DATA|$2/data/$4|g" elasticsearch.yml
	sed -i "s|@NAME|$3|g" elasticsearch.yml
	sed -i "s|@NODEIP|$ip|g" elasticsearch.yml
else
	# Write cassandra config
	sed "s|@DATA|/var/lib/cassandra/$4g/data|g" cassandra_unsub.yaml > cassandra.yaml
	sed -i "s|@COMMITLOG|/var/lib/cassandra/$4g/commitlog|g" cassandra.yaml
	sed -i "s|@SAVEDCACHES|/var/lib/cassandra/$4g/saved_caches|g" cassandra.yaml
	# sed "s|@DATA|/var/lib/cassandra/data|g" cassandra_unsub.yaml > cassandra.yaml
	# sed -i "s|@COMMITLOG|/var/lib/cassandra/commitlog|g" cassandra.yaml
	# sed -i "s|@SAVEDCACHES|/var/lib/cassandra/saved_caches|g" cassandra.yaml
	sed -i "s|@NODEIP|$ip|g" cassandra.yaml

	# Write elasticsearch config
	sed "s|@LOGS|$2/logs/$4g|g" elasticsearch_unsub.yml > elasticsearch.yml
	sed -i "s|@NODEMASTER|$masternode|g" elasticsearch.yml
	sed -i "s|@NODEDATA|$datanode|g" elasticsearch.yml
	sed -i "s|@DATA|$2/data/$4g|g" elasticsearch.yml
	sed -i "s|@NAME|$3|g" elasticsearch.yml
	sed -i "s|@NODEIP|$ip|g" elasticsearch.yml
fi