# Takes the following command line arguments
# 1: the query number for which the Neo4J parameters are to be generated.
# 2: the parameter number which is to be converted from datetime to Neo4j compatible.
# 3: the path to the Janusgraph/GhostIndex folder

import os
import sys
from datetime import datetime

query = sys.argv[1]
param_index = int(sys.argv[2])
filename = sys.argv[3]+'/substitution_parameters/bi_'+query+'_param.txt'

with open(filename) as f:
  content = f.readlines()
# you may also want to remove whitespace characters like `\n` at the end of each line
content = [x.strip() for x in content]
header = content[0]
content = content[1:]

params = []
for param in content:
  if param_index is not -1:
    paramlist = param.split('|');
    timestamp = datetime.fromtimestamp(int(paramlist[param_index])/1000.0)
    timestamp = str(timestamp).replace('-', '').replace(' ', '').replace(':', '')
    print (timestamp)
    paramlist[param_index] = timestamp+'000'
    params.append('|'.join(paramlist))
  else:
    params.append(param)

writefile = 'bi_'+query+'_neo_param.txt'
with open(writefile, 'w') as f:
  f.write("%s\n" % header)
  for item in params:
    f.write("%s\n" % item)
