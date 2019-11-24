import os
import sys
import re
from datetime import datetime

path = sys.argv[1]

for (dirpath, dirname, filename) in os.walk(path):
  if filename[-1] == 'z':
    continue
  with open(dirpath+"/"+filename, "r") as inp:
    first_line = inp[0]
    date_index = -1
    date_format = -1
    for i in range(len(first_line.strip().split('|'))):
      m1 = re.match(r'\d{4}-\d{2}-\d{2}', first_line[i])
      m2 = re.match(r'\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}\+\d{4}', first_line[i])
      if m1:
        date_format = 0
        date_index = i
        break
      elif m2:
        date_format = 1
        date_index = i
        break
    if date_index == -1:
      continue

    if os.path.isfile(dirpath+"/"+filename+"z"):
      continue

    # File requires update
    print ("Update in filename: " + filename)
    newfile = open(dirpath+"/"+filename+"z", "w")
    for line in inp:
      llist = line.strip().split('|')
      date_str = llist[date_index]
      if date_format == 0:
        t = datetime.strptime(date_str)
        new_date = int((t-datetime.datetime(1970,1,1)).total_seconds()*1000)
        llist[date_index] = str(new_date)
        newfile.write('|'.join(llist))
        newfile.write('\n')
