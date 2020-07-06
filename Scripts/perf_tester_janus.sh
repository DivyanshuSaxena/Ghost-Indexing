#!/bin/bash

# Script to run the Performance Tester class in GhostIndex/Utilities project
# Arguments:
# $1 : Fullpath to Janusgraph Installation directory
# $2 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_janus_<number>]
# $3 : Fullpath to Ghost-Indexing/Janusgraph folder
# $4 : Fan-out factor for BPlus index to be created
# $5 : Query number for which the test is to be run
# $6 : Whether the experiments are being run in distributed settings (0/1)

datasets=(1000 2500 4000 7500)
iterations=(0 1)
# datasets=( 7500 )
# iterations=( 0 )
cwd=$(pwd)

load() {

	# Arguments:
	# $1 : Fullpath to Janusgraph Installation directory
	# $2 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_janus_<number>]
	# $3 : Fullpath to Ghost-Indexing/Janusgraph folder
	# $4 : Fan-out factor for BPlus index to be created
	# $5 : Whether the experiments are being run in distributed settings (0/1)

	for dataset in ${datasets[*]}; do
		echo "=================================================="
		echo "Starting Loading Process for dataset: "$dataset
		echo "--------------------------------------------------"
		
		for iteration in ${iterations[*]}; do

			# Copy Graph and ES Index to build Ghost Index on
			if [[ $iteration -ne 0 ]]; then
				cp -r $1/db/cassandra${dataset} $1/db/cassandra${dataset}g
				cp -r $3/../Scripts/db/es${dataset} $3/../Scripts/db/es${dataset}g
				cp -r $3/../Scripts/log/${dataset} $3/../Scripts/log/${dataset}g
			fi

			# Start Janusgraph
			if [[ $iteration -eq 0 ]]; then
				cp $3/GhostIndex/conf/standalone/cassandra/cassandra.$dataset.yaml $1/conf/cassandra/cassandra.yaml
				cp $3/GhostIndex/conf/standalone/elasticsearch/elasticsearch.$dataset.yml $1/elasticsearch/config/elasticsearch.yml
			else
				cp $3/GhostIndex/conf/standalone/cassandra/cassandra.$dataset.g.yaml $1/conf/cassandra/cassandra.yaml
				cp $3/GhostIndex/conf/standalone/elasticsearch/elasticsearch.$dataset.g.yml $1/elasticsearch/config/elasticsearch.yml
			fi
			sleep 5s
			$1/bin/janusgraph.sh start

			if [[ $iteration -eq 0 ]]; then
				# Load Data for current dataset
				echo "Loading dataset onto Janusgraph"
				cd $3/dataLoader
				time ./loadit.sh $2/social_network_janus_$dataset $3/dataLoader/sorted $1/bin $dataset $5
				cd $cwd
			else
				# Add BPlus Ghost Indexes
				echo "Building Index"
				cd $3/IndexHandler
				echo "time ./unifyIndex.sh post post_creationDate_index_bPlus_$4 $4 3 $2/social_network_janus_$dataset/post_0_0.csv D BP po_id po_creationDate $5 ${dataset}"
				time ./unifyIndex.sh post post_creationDate_index_bPlus_$4 $4 3 $2/social_network_janus_$dataset/post_0_0.csv D BP po_id po_creationDate $5 $dataset
				##################################
				# Add unifyIndex.sh command here #
				##################################
				# echo "time ./unifyIndex.sh tagClass tagClass_bTree_$4 $4 2 $2/social_network_janus_$dataset/tagclass_0_0.csv S B tc_id tc_name $5 ${dataset}"
				# time ./unifyIndex.sh tagclass tagclass_bTree_$4 $4 2 $2/social_network_janus_$dataset/tagclass_0_0.csv S B tc_id tc_name $5 $dataset
				cd $cwd
			fi

			# Stop Janusgraph
			echo "Loading completed for dataset: "$dataset
			echo "--------------------------------------------------"
			$1/bin/janusgraph.sh stop
			sleep 10s
		done
	done

}

performance() {

	# Arguments:
	# $1 : Fullpath to Janusgraph Installation directory
	# $2 : Fullpath to Ghost-Indexing/Janusgraph folder
	# $3 : Fan-out factor for BPlus index to be created
	# $4 : Query number for which the test is to be run
	# $5 : Whether the experiments are being run in distributed settings (0/1)

	# Compile the project
	cd $2/GhostIndex/Utilities
	mvn compile
	cd $cwd

	for dataset in ${datasets[*]}; do
		echo "=================================================="
		echo "Starting Testing Process for dataset: "$dataset
		echo "--------------------------------------------------"

		for iteration in ${iterations[*]}; do

			# Start Janusgraph
			if [[ $5 -gt 0 ]]; then
				echo "Running for distributed setting"
				$1/bin/gremlin-server.sh start
			else
				if [[ $iteration -eq 0 ]]; then
					cp $2/GhostIndex/conf/standalone/cassandra/cassandra.$dataset.yaml $1/conf/casssandra/cassandra.yaml
					cp $2/GhostIndex/conf/standalone/elasticsearch/elasticsearch.$dataset.yml $1/elasticsearch/config/elasticsearch.yml
				else
					cp $2/GhostIndex/conf/standalone/cassandra/cassandra.$dataset.g.yaml $1/conf/casssandra/cassandra.yaml
					cp $2/GhostIndex/conf/standalone/elasticsearch/elasticsearch.$dataset.g.yml $1/elasticsearch/config/elasticsearch.yml
				fi
				$1/bin/janusgraph.sh start
			fi

			if [[ $iteration -eq 0 ]]; then
				# Run Performance Tester for the given Query class
				echo "Running ES Implementation for Query"
				cd $2/GhostIndex/Utilities
				mvn exec:java -Dexec.mainClass=main.PerformanceTester -Dexec.args="Query$4 $4 1 $dataset $5 ${iteration} local"
				cd $cwd
			else
				# Run Performance Tester for the given Query class again
				echo "Running GhostIndex Implementation for Query"
				cd $2/GhostIndex/Utilities
				mvn exec:java -Dexec.mainClass=main.PerformanceTester -Dexec.args="BIndexQuery$4 $4 1 $dataset $5 ${iteration} local"
				cd $cwd
			fi

			# Cleanup data, to prepare for next dataset
			echo "Performance test completed for dataset: "$dataset
			echo "--------------------------------------------------"
			if [[ $5 -gt 0 ]]; then
				echo "Running for distributed setting"
				$1/bin/gremlin.sh < remove_cassandra_data.gremlin
				./remove_es_indexes_dist.sh
			else
				$1/bin/janusgraph.sh stop
			fi
			
			sleep 10s
		done
	done

}

load_distributed() {

	# Arguments:
	# $1 : Fullpath to Janusgraph Installation directory
	# $2 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_janus_<number>]
	# $3 : Fullpath to Ghost-Indexing/Janusgraph folder
	# $4 : Fan-out factor for BPlus index to be created
	# $5 : Whether the experiments are being run in distributed settings (0/1)
	# $6 : Fullpath to Elasticsearch Installation directory

	for dataset in ${datasets[*]}; do

		echo "=================================================="
		echo "Starting Loading Process for dataset: "$dataset
		echo "--------------------------------------------------"

		for iteration in ${iterations[*]}; do
			# Copy Graph and ES Index to build Ghost Index on
			# if [[ $iteration -ne 0 ]]; then
			# 	$3/../Scripts/distributed/copy.sh $6 $dataset
			# fi

			# Copy configs to remote
			echo "--------------------------------------------------"
			echo "Copying configs to all nodes: "
			echo "--------------------------------------------------"
			$3/../Scripts/distributed/send_config.sh $3 $6 $dataset $iteration

			# Start Cassandra, Elasticsearch and Gremlin server
			echo "--------------------------------------------------"
			echo "Starting Cassandra, Elasticsearch and Gremlin server: "
			echo "--------------------------------------------------"
			$3/../Scripts/distributed/services.sh start $6
			$1/bin/gremlin-server.sh start
			sleep 100

			# Load Data for current dataset
			echo "--------------------------------------------------"
			echo "Loading dataset onto Janusgraph"
			echo "--------------------------------------------------"
			cd $3/dataLoader
			time ./loadit.sh $2/social_network_janus_$dataset $3/dataLoader/sorted $1/bin $dataset $5
			cd $cwd

			if [[ $iteration -eq 1 ]]; then
				# Add BPlus Ghost Indexes
				echo "--------------------------------------------------"
				echo "Building Ghost Index on Janusgraph"
				echo "--------------------------------------------------"
				cd $3/IndexHandler
				echo "time ./unifyIndex.sh post post_creationDate_index_bPlus_$4 $4 3 $2/social_network_janus_$dataset/post_0_0.csv D BP po_id po_creationDate $5 ${dataset}"
				time ./unifyIndex.sh post post_creationDate_index_bPlus_$4 $4 3 $2/social_network_janus_$dataset/post_0_0.csv D BP po_id po_creationDate $5 $dataset
				cd $cwd
			fi

			# Stop Janusgraph
			echo "--------------------------------------------------"
			echo "Loading completed for dataset: "$dataset
			echo "--------------------------------------------------"
			$3/../Scripts/distributed/services.sh stop

		done
	done
}

performance_distributed() {

	# Arguments:
	# $1 : Fullpath to Janusgraph Installation directory
	# $2 : Fullpath to Ghost-Indexing/Janusgraph folder
	# $3 : Fan-out factor for BPlus index to be created
	# $4 : Query number for which the test is to be run
	# $5 : Whether the experiments are being run in distributed settings (0/1)
	# $6 : Fullpath to Elasticsearch Installation directory

	# Compile the project
	cd $2/GhostIndex/Utilities
	mvn compile

	for dataset in ${datasets[*]}; do

		echo "=================================================="
		echo "Starting Testing Process for dataset: "$dataset
		echo "--------------------------------------------------"

		for iteration in ${iterations[*]}; do

			# Copy configs to remote
			$2/../Scripts/distributed/send_config.sh $2 $6 $dataset $iteration

			# Start Cassandra, Elasticsearch and Gremlin server
			$2/../Scripts/distributed/services.sh start
			$1/bin/gremlin-server.sh start

			# Run Performance Tester for the given Query class
			echo "Loading dataset onto Janusgraph"
			cd $2/GhostIndex/Utilities
			mvn exec:java -Dexec.mainClass=main.PerformanceTester -Dexec.args="Query$4 $4 1 $dataset $5 ${iteration} local"
			cd $cwd

			# Stop Janusgraph
			echo "Loading completed for dataset: "$dataset
			echo "--------------------------------------------------"
			$3/../Scripts/distributed/services.sh stop
			sleep 10s

		done
	done

}

if [[ $1 = "load" ]]; then
	shift
	if [[ $5 -ne 1 ]]; then
		load $@
	else
		load_distributed $@
	fi
elif [[ $1 = "test" ]]; then
	shift
	if [[ $5 -ne 1 ]]; then
		performance $@
	else
		performance_distributed $@
	fi
else
	echo "Usage: perf_tester_janus.sh [load/performance] [<List of arguments>]"
fi
