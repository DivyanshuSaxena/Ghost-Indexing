# Takes the following command line arguments
# 1: the query number which is to be fired
# 2: the fullpath to the neo4j/bin/cypher-shell binary
# 3: the path to the cypher/queries subfolder in ldbc_snb_implementations
#    folder or the path of the ghost-queries folder in the parent directory
# 4: Whether query has to be run from the lucene index (ldbc_snb, 0) or the
#    new ghost query folder (1)
# 5: username for the cypher shell
# 6: password for the cypher shell

import os
import sys
import time

query = sys.argv[1]
is_ghost = int(sys.argv[4])
query_path = sys.argv[3]

if is_ghost == 1:
  query_file = query_path + '/bi-ghost-' + query + '.cypher'
else:
  query_file = query_path + '/bi-' + query + '.cypher'
print ('Reading from %s\n' %query_file)

num_tries = 3

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
  cypher_shell = sys.argv[2] + ' -u ' + sys.argv[5] + ' -p ' + sys.argv[6]

  # Run each parameter for num_tries times
  result_count = 0
  average_time = 0
  for i in range(num_tries):
    start_time = time.time()
    os.system(cypher_shell + ' < ' + os.getcwd() + '/temp.cypher > out.txt')
    end_time = time.time()
    average_time += (end_time - start_time)

    count = 0
    with open('out.txt') as f:
      for line in f:
        count += 1
    if (result_count == 0):
      result_count = count
    elif (result_count != count):
      print('Errored execution. Results count not matching')
  average_time /= num_tries

  # Write in the final output file
  of.write(query_param + ',' + str(result_count-1) + ',' + str(average_time) + '\n')

os.system('rm temp.cypher')
os.system('rm out.txt')