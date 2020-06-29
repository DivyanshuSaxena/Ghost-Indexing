# Creates the queries with all params and fires on neo4j shell.
# Requires a params/ and results/ folder in this directory
#  
# Takes the following command line arguments
# 1: the query number which is to be fired
# 2: the dataset size on which the query has been run
# 3: the fullpath to the neo4j/ installation directory
# 4: the path to the ghost-queries folder in the parent directory
# 5: username for the cypher shell
# 6: password for the cypher shell

import os
import sys
import time
from utils import config_parser

if len(sys.argv) != 7:
  print ('Command line arguments incorrect. Check documentation for help.')
  sys.exit(0)

query = sys.argv[1]
dataset = sys.argv[2]
query_path = sys.argv[4]

results_folder = './results/'
if not os.path.exists(results_folder):
  os.makedirs(results_folder)

params_folder = './params/'
neo4j_bin = sys.argv[3] + '/bin/neo4j'
cypher_shell = sys.argv[3] + '/bin/cypher-shell'

query_file = query_path + '/bi-filter-' + query + '.cypher'
print ('Reading from %s\n' %query_file)

output_file = results_folder + 'bi_' + query + '_filter_results_' + dataset + '.txt'
of = open(output_file, 'w')
of.write('Parameter,Filter Count\n')

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
  temp_file = 'temp_filter.cypher'
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

  # Run the query for each parameter
  cypher_shell = cypher_shell + ' -u ' + sys.argv[5] + ' -p ' + sys.argv[6]
  os.system(cypher_shell + ' < ' + os.getcwd() + '/temp_filter.cypher > out.txt')

  with open('out.txt') as f:
    content = f.readlines()
  result_count = int(content[1])

  # Write in the final output file
  of.write(query_param + ',' + str(result_count) + '\n')
  print ('Completed this parameter')

os.system('rm temp_filter.cypher')
os.system('rm out.txt')
