# Creates the queries with all params and fires on neo4j shell.
# Requires a params/ and results/ folder in this directory
#  
# Takes the following command line arguments
# 1: the query number which is to be fired
# 2: the dataset size on which the query has been run
# 3: Whether query has to be run from the lucene index (ldbc_snb, 0) or the
#    new ghost query folder (1)
# 4: Batch size for clearing the cache
# 5: the fullpath to the neo4j/ installation directory
# 6: the path to the cypher/queries subfolder in ldbc_snb_implementations
#    folder or the path of the ghost-queries folder in the parent directory
# 7: page cache size used by neo4j
# 8: username for the cypher shell
# 9: password for the cypher shell

import os
import sys
import time
from utils import config_parser

if len(sys.argv) != 10:
  print ('Command line arguments incorrect. Check documentation for help.')
  sys.exit(0)

query = sys.argv[1]
dataset = sys.argv[2]
is_ghost = int(sys.argv[3])
batch_size = int(sys.argv[4])
query_path = sys.argv[6]
cache_size = sys.argv[7]

results_folder = './results/' + cache_size + '/'
if not os.path.exists(results_folder):
  os.makedirs(results_folder)

params_folder = './params/'
neo4j_bin = sys.argv[5] + '/bin/neo4j'
cypher_shell = sys.argv[5] + '/bin/cypher-shell'

if is_ghost == 1:
  query_file = query_path + '/bi-ghost-' + query + '.cypher'
else:
  query_file = query_path + '/bi-' + query + '.cypher'
print ('Reading from %s\n' %query_file)

cold_tries = 2
num_tries = 5

output_file = results_folder + 'bi_' + query + '_neo_results_' + dataset + '.txt'
if is_ghost == 1:
  output_file = results_folder + 'bi_' + query + '_ghost_results_' + dataset + '.txt'
of = open(output_file, 'w')
of.write('Parameter,Count,Cold Start Time,Warm Cache Time\n')

# Read query from the file
with open(query_file, 'r') as f:
  query_lines = f.readlines()
query_lines = [x.strip() for x in query_lines]

# Separate parameter comment and query
query_partition = 0
for query_index in range(len(query_lines)):
  if query_lines[query_index] == '*/':
    query_partition = query_index
    break
param_comment = ''.join(query_lines[2:query_index])
query_content = '\n'.join(query_lines[query_index+1:])
# print (query_content)

# Read parameters one by one.
parameter_file = params_folder + 'bi_' + query + '_neo_param.txt'
with open(parameter_file, 'r') as f:
  content = f.readlines()
content = [x.strip() for x in content]
header = content[0].split('|')
params = content[1:]

# Parse the config file to know which parameters are date
date_params = config_parser.read_section(query, section='QUERY')

# Maintain counter for implementing batches
counter = 0
for query_param in params:
  print (query_param)

  # Create temporary query file.
  temp_file = 'temp.cypher'
  tf = open(temp_file, 'w')

  # Write query params  
  paramlist = query_param.split('|')
  for index in range(len(paramlist)):
    if date_params[index+1] != 'S':
      tf.write(':param ' + str(header[index])
              + '=>' + str(paramlist[index]) + ';\n')
    else:
      tf.write(':param ' + str(header[index])
              + '=>"' + str(paramlist[index]) + '";\n')
  
  # Write query content
  tf.write(query_content + ';\n')
  tf.close()

  # Run the query
  cypher_shell = cypher_shell + ' -u ' + sys.argv[8] + ' -p ' + sys.argv[9]

  # Run each parameter for num_tries times
  result_count = 0
  average_time = cold_start_time = 0
  for i in range(num_tries):
    start_time = time.time()
    os.system(cypher_shell + ' < ' + os.getcwd() + '/temp.cypher > out.txt')
    end_time = time.time()
    average_time += (end_time - start_time)

    count = 0
    with open('out.txt') as f:
      for line in f:
        count += 1

    if i < cold_tries:
      cold_start_time += (end_time - start_time)
      result_count = count
    elif (result_count != count):
      print('Errored execution. Results count not matching')

  average_time = (average_time - cold_start_time)/(num_tries - cold_tries)
  cold_start_time = cold_start_time/cold_tries
  
  counter += 1
  if (counter % batch_size == 0):
    os.system(neo4j_bin + ' restart')
    print("Restarting Neo4j -> Wait for 1min before resuming")
    time.sleep(60)

  # Write in the final output file
  of.write(query_param + ',' + str(result_count-1) + ',' + 
           str(cold_start_time) + ',' + str(average_time) + '\n')
  print ('Completed this parameter')

os.system('rm temp.cypher')
os.system('rm out.txt')
