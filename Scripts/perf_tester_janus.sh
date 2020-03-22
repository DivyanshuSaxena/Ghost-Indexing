#!/bin/bash

# Script to run the Performance Tester class in GhostIndex/Utilities project
# Arguments:
# $1 : Fullpath to Janusgraph Installation directory
# $2 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_janus_<number>]
# $3 : Fullpath to Ghost-Indexing/Janusgraph folder
# $4 : Fan-out factor for BPlus index to be created
# $5 : Query number for which the test is to be run
# $6 : Whether the experiments are being run in distributed settings (0/1)

# Note:
# This makes use of GhostIndex/perf_tester_cmd.sh which is the command that Intelliij uses for running Utilities.main.PerformanceTester
# Make sure to edit perf_tester_cmd.sh accordingly.

datasets=( 1000 2500 4000 7500 10000 )
#datasets=( 1000 )
cwd=$(pwd)

for dataset in ${datasets[*]}; do
	echo "=================================================="
	echo "Starting for dataset: "$dataset
	echo "--------------------------------------------------"

	# Start Janusgraph
	if [[ $6 -gt 0 ]]; then
		echo "Running for distributed setting"
		$1/bin/gremlin-server.sh start
	else
		$1/bin/janusgraph.sh start
	fi

	# Load Data for current dataset
	echo "Loading dataset onto Janusgraph"
	cd $3/dataLoader
	time ./loadit.sh $2/social_network_janus_$dataset $3/dataLoader/sorted $1/bin $6
	cd $cwd

	# Run Performance Tester for the given Query class
	echo "Running ES Implementation for Query"
	cd $3/GhostIndex
	./perf_tester_cmd.sh Query$5 $5 $dataset
	cd $cwd

	# Add BPlus Ghost Indexes
	echo "Building Index"
	cd $3/IndexHandler
	time ./unifyIndex.sh post post_creationDate_index_bPlus_$4 $4 3 $2/social_network_janus_$dataset/post_0_0.csv D BP po_id po_creationDate $6
	cd $cwd

	# Run Performance Tester for the given Query class again
	echo "Running GhostIndex Implementation for Query"
	cd $3/GhostIndex
	./perf_tester_cmd.sh BPIndexQuery$5 $5 $dataset
	cd $cwd

	# Cleanup data, to prepare for next dataset
	echo "Performance test completed for dataset: "$dataset
	echo "--------------------------------------------------"
	if [[ $6 -gt 0 ]]; then
		echo "Running for distributed setting"
		$1/bin/gremlin.sh < remove_cassandra_data.gremlin
		./remove_es_indexes_dist.sh
	else
		$1/bin/janusgraph.sh stop
	fi
	echo "y" | $1/bin/janusgraph.sh clean
done
