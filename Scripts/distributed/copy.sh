#!/bin/bash

# Arguments:
	# $1 : Fullpath to Elasticsearch installation directory
	# $2 : dataset size [1000 | 2500 | 4000 | 7500 | 10000]

names=("node-m" "node-1" "node-2" "node-3" "node-4")
ips=("10.17.5.53" "10.17.50.89" "10.17.50.201" "10.17.50.127" "10.17.50.228")

for i in {0..4}; do
	if [[ $i -ne 0 ]]; then
		echo "Copying es logs"
		echo ghost2019 | ssh -tt ${ips[$i]} "sudo cp -r $1/logs/$2 $1/logs/$2g"
		echo "Copying es data"
		echo ghost2019 | ssh -tt ${ips[$i]} "sudo cp -r $1/data/$2 $1/data/$2g"
		echo "Copying cassandra data"
		echo ghost2019 | ssh -tt ${ips[$i]} "sudo cp -r /var/lib/cassandra/$2 /var/lib/cassandra/$2g"
	else
		sudo cp -r $1/logs/$2 $1/logs/$2g
		sudo cp -r $1/data/$2 $1/data/$2g
		sudo cp -r /var/lib/cassandra/$2 /var/lib/cassandra/$2g
	fi
done