# Takes the following command line arguments
# 1: the query number which is to be fired
# 2: the fullpath to the neo4j/bin/cypher-shell binary
# 3: the path to the cypher/queries subfolder in ldbc_snb_implementations folder
# 4: username for the cypher shell
# 5: password for the cypher shell

import os
import sys
import time

query = sys.argv[1]
query_path = sys.argv[3]
query_file = query_path + '/bi-' + query + '.cypher'

output_file = 'bi_' + query + '_neo_results.txt'
of = open(output_file, 'w')
of.write('Parameter,Count,Time\n')

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
parameter_file = 'bi_' + query + '_neo_param.txt'
with open(parameter_file, 'r') as f:
  content = f.readlines()
content = [x.strip() for x in content]
header = content[0].split('|')
params = content[1:]

for query_param in params:
  # Create temporary query file.
  temp_file = 'temp.cypher'
  tf = open(temp_file, 'w')

  # Write query params  
  paramlist = query_param.split('|')
  for index in range(len(paramlist)):
    tf.write(':param ' + str(header[index])
              + '=>' + str(paramlist[index]) + ';\n')
  
  # Write query content
  tf.write(query_content + ';\n')
  tf.close()

  # Run the query
  cypher_shell = sys.argv[2] + ' -u ' + sys.argv[4] + ' -p ' + sys.argv[5]

  start_time = time.time()
  os.system(cypher_shell + ' < ' + os.getcwd() + '/temp.cypher > out.txt')
  end_time = time.time()
  print (end_time - start_time)

  count = 0
  with open('out.txt') as f:
    for line in f:
      count += 1

  # Write in the final output file
  of.write(query_param + ',' + str(count-1) + ',' + str(end_time - start_time) + '\n')

os.system('rm temp.cypher')
os.system('rm out.txt')