#!/bin/bash

# Script to run the Performance Tester class in GhostIndex/Utilities project
# Arguments:
# $1 : Fullpath to Janusgraph Installation directory
# $2 : Fullpath to ldbc_snb_datagen folder [Note: All datasets must be mentioned as social_network_<number>]
# $3 : Fullpath to Ghost-Indexing/Janusgraph folder
# $4 : Fan-out factor for BPlus index to be created
# $5 : Query number for which the test is to be run

# Note:
# This makes use of GhostIndex/perf_tester_cmd.sh which is the command that Intelliij uses for running Utilities.main.PerformanceTester
# Make sure to edit perf_tester_cmd.sh accordingly.

datasets=( 1000 2500 4000 7500 10000 )
cwd=$(pwd)

for dataset in ${datasets[*]}; do
	echo "=================================================="
	echo "Starting for dataset: "$dataset
	echo "--------------------------------------------------"

	# Start Janusgraph
	$1/bin/janusgraph.sh start

	# Load Data for current dataset
	echo "Loading dataset onto Janusgraph"
	cd $3/dataLoader
	time ./loadit.sh $2/social_network_$dataset $3/dataLoader/sorted $1/bin
	cd $cwd

	# Run Performance Tester for the given Query class
	echo "Running ES Implementation for Query"
	cd $3/GhostIndex
	./perf_tester_cmd.sh Query$5 $5 $dataset
	cd $cwd

	# Add BPlus Ghost Indexes
	echo "Building Index"
	cd $3/IndexHandler
	time ./unifyIndex.sh post post_creationDate_index_bPlus_$4 $4 3 $2/social_network_$dataset/post_0_0.csv D BP po_id po_creationDate
	cd $cwd

	# Run Performance Tester for the given Query class again
	echo "Running GhostIndex Implementation for Query"
	cd $3/GhostIndex
	./perf_tester_cmd.sh BPIndexQuery$5 $5 $dataset
	cd $cwd

	# Cleanup data, to prepare for next dataset
	echo "Performance test completed for dataset: "$dataset
	echo "--------------------------------------------------"
	$1/bin/janusgraph.sh stop
	echo "y" | $1/bin/janusgraph.sh clean
done