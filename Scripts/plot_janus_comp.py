# Plot the comparison of janusgraph results for two fan-out factors
#
# Takes the following command line arguments
# 1: Query to plot the graph for
# 2: Directory from which to read the results for first fan-out
# 3: Directory from which to read the results for second fan-out
# 4: Directory from which to read the filter result sizes
# 5: Plot filter result sizes or parameters on the X axis (size/param)
# 6: Directory to save the results in

import os
import sys
import math
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec

query = sys.argv[1]
results_dir_1 = sys.argv[2]
results_dir_2 = sys.argv[3]
filter_dir = sys.argv[4]
x_axis = sys.argv[5] == "size"
figure_dir = sys.argv[6]

# Get the fan-outs for the two directories
fanout_1 = results_dir_1.split('/')[-1]
fanout_2 = results_dir_2.split('/')[-1]

if not os.path.exists(figure_dir):
  os.makedirs(figure_dir)

datasets = ["1000", "2500", "4000", "7500", "10000"]
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
            ele[0] = str(param_num)
            if len(ele[1]) > 3:
                ele[1] = int(ele[1])//1000
                ele[1] = str(ele[1]) + 'k'
            param_num += 1
        param_sizes = [','.join(x) for x in param_sizes]

    # File name for indexes and ghost indexes
    result_file_1 = results_dir_1 + '/BIndexQuery' + query + '_' + dataset + '.csv'
    result_file_2 = results_dir_2 + '/BIndexQuery' + query + '_' + dataset + '.csv'

    # Read index times
    with open(result_file_1, 'r') as f:
        content = f.readlines()
    cold_start_1 = [float(x.strip().split(' ')[1]) / 1000 for x in content]
    warm_cache_1 = [float(x.strip().split(' ')[4]) / 1000 for x in content]

    # Read ghost index times
    with open(result_file_2, 'r') as f:
        content = f.readlines()
    cold_start_2 = [float(x.strip().split(' ')[1]) / 1000 for x in content]
    warm_cache_2 = [float(x.strip().split(' ')[4]) / 1000 for x in content]

    max_cold_time = math.ceil(max(max(cold_start_1), max(cold_start_2)))
    max_warm_time = math.ceil(max(max(warm_cache_1), max(warm_cache_2)))

    # If plotting result sizes, sort them in increasing order
    if x_axis:
        param_sizes, cold_start_1, warm_cache_1, cold_start_2, warm_cache_2 = map(
            list,
            zip(*sorted(
                zip(param_sizes, cold_start_1, warm_cache_1, cold_start_2,
                    warm_cache_2))))

    fig_size = (6.4, 4.8)  # default size
    if not x_axis:
        fig_size = (10, 5)

    # Plot these results
    fig = plt.figure(constrained_layout=True, figsize=fig_size)
    gs = gridspec.GridSpec(2, 1, figure=fig)
    ax = fig.add_subplot(gs[:, :])
    # plt.title('JanusGraph | Query ' + query + ' | Cold start | Dataset ' +
    #           dataset)
    # ax.set_ylabel('Timings (s)')
    # ax.set_ylim([0, max_cold_time])
    l1, = ax.plot(param_sizes, cold_start_1, 'r+-')
    l2, = ax.plot(param_sizes, cold_start_2, 'b.-')

    # ax = fig.add_subplot(gs[:, :])
    plt.title('JanusGraph | Query ' + query + ' | Warm Cache | Dataset ' +
              dataset)
    ax.set_ylabel('Timings (s)')
    ax.set_xlabel('Query result sizes')
    ax.set_ylim([0, max_warm_time])
    ax.plot(param_sizes, warm_cache_1, 'r+-')
    ax.plot(param_sizes, warm_cache_2, 'b.-')

    fig.legend((l1, l2), (fanout_1, fanout_2), 'upper left')
    plt.savefig(figure_dir + '/JG_' + query + '_' + dataset + '.png')
    plt.show()
