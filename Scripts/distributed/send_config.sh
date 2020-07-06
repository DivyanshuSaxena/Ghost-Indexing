#!/bin/bash

# Arguments:
	# $1 : Fullpath to Ghost-Indexing/Janusgraph folder
	# $2 : Fullpath to Elasticsearch installation directory
	# $3 : dataset size [1000 | 2500 | 4000 | 7500 | 10000]
	# $4 : ghostindex or not [0 | 1]


names=("node-m" "node-1" "node-2" "node-3" "node-4")
ips=("10.17.5.53" "10.17.50.89" "10.17.50.201" "10.17.50.127" "10.17.50.228")

for i in {0..4}; do
	$1/../Scripts/distributed/write_config.sh $1 $2 ${names[$i]} $3 $4
	cd $1/GhostIndex/conf/distributed/
	if [[ $i -ne 0 ]]; then
		scp cassandra.yaml ${ips[$i]}:/home/baadalvm/
		echo ghost2019 | ssh -tt ${ips[$i]} "sudo mv /home/baadalvm/cassandra.yaml /etc/cassandra/cassandra.yaml"
		scp elasticsearch.yml ${ips[$i]}:$2/config/
		# cat cassandra.yaml | ssh -t ${ips[$i]} "tee /etc/cassandra/cassandra.yaml"
		# cat elasticsearch.yml | ssh -t ${ips[$i]} "tee $2/config/elasticsearch.yaml"
	else
		echo ghost2019 | sudo cp cassandra.yaml /etc/cassandra/
		cp elasticsearch.yml $2/config/
	fi
done
