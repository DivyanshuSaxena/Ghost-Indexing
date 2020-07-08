# Plot the results of janus and neo
#
# Takes the following command line arguments
# 1: Query to plot the graph for
# 2: Directory from which to read the results
# 3: Directory from which to read the filter result sizes
# 4: Plot filter result sizes or parameters on the X axis (size/param)
# 5: Directory to save the results in

import os
import sys
import math
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec

query = sys.argv[1]
results_dir = sys.argv[2]
filter_dir = sys.argv[3]
x_axis = sys.argv[4] == "size"
figure_dir = sys.argv[5] + '/Query ' + query

datasets = ["1000", "2500", "4000", "7500"]
cache_sizes = ["10m", "100m", "500m", "1g"]

if not os.path.exists(figure_dir):
  os.makedirs(figure_dir)

for cache in cache_sizes:
    cache_results_dir = results_dir + '/results_neo_' + cache + '/' + cache
    for dataset in datasets:
        results_size_file = filter_dir + '/bi_' + query + '_filter_results_' + dataset + '.txt'

        with open(results_size_file, 'r') as f:
            content = f.readlines()
        if x_axis:
            param_sizes = [int(x.strip().split(',')[1]) for x in content[1:]]
        else:
            param_sizes = [x.strip().split(',') for x in content[1:]]
            param_num = 1
            for ele in param_sizes:
                ele[0] = 'p' + str(param_num)
                param_num += 1
            param_sizes = [','.join(x) for x in param_sizes]

        # File name for indexes and ghost indexes
        index_result_file = cache_results_dir + '/bi_' + query + '_neo_results_' + dataset + '.txt'
        ghost_result_file = cache_results_dir + '/bi_' + query + '_ghost_results_' + dataset + '.txt'

        # Read index times
        with open(index_result_file, 'r') as f:
            content = f.readlines()
        content = content[1:]
        index_cold_start = [float(x.strip().split(',')[2]) for x in content]
        index_warm_cache = [float(x.strip().split(',')[3]) for x in content]

        # Read ghost index times
        with open(ghost_result_file, 'r') as f:
            content = f.readlines()
        content = content[1:]
        ghost_cold_start = [float(x.strip().split(',')[2]) for x in content]
        ghost_warm_cache = [float(x.strip().split(',')[3]) for x in content]

        max_cold_time = math.ceil(max(max(index_cold_start), max(ghost_cold_start)))
        max_warm_time = math.ceil(max(max(index_warm_cache), max(ghost_warm_cache)))

        # If plotting result sizes, sort them in increasing order
        if x_axis:
            param_sizes, index_cold_start, index_warm_cache, ghost_cold_start, ghost_warm_cache = map(
                list,
                zip(*sorted(
                    zip(param_sizes, index_cold_start, index_warm_cache,
                        ghost_cold_start, ghost_warm_cache))))

        fig_size = (6.4, 4.8)  # default size
        if not x_axis:
            fig_size = (10, 5)

        # Plot these results
        fig = plt.figure(constrained_layout=True, figsize=fig_size)
        gs = gridspec.GridSpec(2, 1, figure=fig)
        ax = fig.add_subplot(gs[0, :])
        plt.title('Neo4j Cache ' + cache + ' | Query ' + query +
                  ' | Cold start | Dataset ' + dataset)
        ax.set_ylabel('Timings (s)')
        ax.set_ylim([0, max_cold_time])
        l1, = ax.plot(param_sizes, index_cold_start, 'r+-')
        l2, = ax.plot(param_sizes, ghost_cold_start, 'b.-')

        ax = fig.add_subplot(gs[1, :])
        plt.title('Neo4j Cache ' + cache + ' | Query ' + query +
                  ' | Warm Cache | Dataset ' + dataset)
        ax.set_ylabel('Timings (s)')
        ax.set_xlabel('Query result sizes')
        ax.set_ylim([0, max_warm_time])
        ax.plot(param_sizes, index_warm_cache, 'r+-')
        ax.plot(param_sizes, ghost_warm_cache, 'b.-')

        fig.legend((l1, l2), ('Index', 'Ghost Index'), 'upper left')
        plt.savefig(figure_dir + '/N' + cache + '_' + query + '_' + dataset + '.png')
