# Read the results and calculate statistics for a given query
# Writes the statistics in file stats_<query>_<cache>.csv
# 
# Takes the following command line arguments
# 1: the query number for which the statistics are to be calculated
# 2: the cache size used for the experiment

import os
import sys

query = sys.argv[1]
cache_size = sys.argv[2]
stats_file = './results/stats_' + query + '_' + cache_size + '.csv'

results_folder = './results/' + cache_size + '/'
datasets = ["1000", "2500", "4000", "7500"]

cold_speedup = {}
warm_speedup = {}

for dataset in datasets:
  es_file = 'bi_' + query + '_neo_results_' + dataset + '.txt'
  ghost_file = 'bi_' + query + '_ghost_results_' + dataset + '.txt'

  es_results = []
  ghost_results = []
  
  # Read lines from ES and GhostIndex results
  with open(results_folder + es_file, 'r') as f:
    es_results = f.readlines()
  es_results = [x.strip().split(',') for x in es_results][1:]

  with open(results_folder + ghost_file, 'r') as f:
    ghost_results = f.readlines()
  ghost_results = [x.strip().split(',') for x in ghost_results][1:]

  average_cold_speedup = 0
  average_warm_speedup = 0

  # Get cold cache and warm cache speed-ups
  for it in range(len(es_results)):
    es_cold_start = float(es_results[it][2])
    es_warm_cache = float(es_results[it][3])
    ghost_cold_start = float(ghost_results[it][2])
    ghost_warm_cache = float(ghost_results[it][3])

    average_cold_speedup += (es_cold_start/ghost_cold_start)
    average_warm_speedup += (es_warm_cache/ghost_warm_cache)

  # Write to map
  average_cold_speedup = average_cold_speedup / len(es_results)
  average_warm_speedup = average_warm_speedup / len(es_results)
  cold_speedup[dataset] = average_cold_speedup
  warm_speedup[dataset] = average_warm_speedup

# Write in file and output on the console
print("Cache size: " + cache_size)
with open(stats_file, 'w') as f:
  f.write('Dataset, Cold Speedup, Warm Speedup\n')
  for dataset in datasets:
    f.write(str(dataset) + ', ' + str(cold_speedup[dataset]) + ', ' + 
            str(warm_speedup[dataset]) + '\n')
    print("Dataset " + str(dataset) + ": "+ str(cold_speedup[dataset]) + 
          " " + str(warm_speedup[dataset]))
