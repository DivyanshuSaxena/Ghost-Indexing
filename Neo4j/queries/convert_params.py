# Takes the following command line arguments
# 1: the query number for which the Neo4J parameters are to be generated.
# 2: the path to the Janusgraph/GhostIndex folder

import os
import sys
from utils import config_parser
from datetime import datetime

query = sys.argv[1]
date_params = config_parser.read_section(query, section='QUERY')
print (date_params)
filename = sys.argv[2]+'/substitution_parameters/bi_'+query+'_param.txt'

with open(filename) as f:
  content = f.readlines()
content = [x.strip() for x in content]
header = content[0]
content = content[1:]

params = []
for param in content:
  paramlist = param.split('|')
  for index in range(len(paramlist)):
    if date_params[index+1] == 'D':
      timestamp = datetime.fromtimestamp(int(paramlist[index])/1000.0)
      timestamp = str(timestamp).replace('-', '').replace(' ', '').replace(':', '')
      print (timestamp)
      paramlist[index] = timestamp+'000'
  params.append('|'.join(paramlist))

writefile = './params/bi_'+query+'_neo_param.txt'
with open(writefile, 'w') as f:
  f.write("%s\n" % header)
  for item in params:
    f.write("%s\n" % item)
