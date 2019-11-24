import os
import sys
import re
from datetime import datetime, timedelta

path = sys.argv[1]

def get_updated_line(line, date_indexes):
  llist = line.strip().split('|')
  for date_index, date_format in date_indexes:
    date_str = llist[date_index]
    if date_format == 0:
      # Default format is YYYY-MM-dd
      t = datetime.strptime(date_str, "%Y-%m-%d")
      new_date = int((t-datetime.utcfromtimestamp(0)).total_seconds()*1000)
      llist[date_index] = str(new_date)
    else:
      t = datetime.strptime(date_str[:-5], "%Y-%m-%dT%H:%M:%S.%f")
      t -= timedelta(minutes=int(date_str[-4:])%100, hours=int(date_str[-4:])//100)
      new_date = int((t-datetime.utcfromtimestamp(0)).total_seconds()*1000)
      llist[date_index] = str(new_date)
  return llist

for (dirpath, dirname, filenames) in os.walk(path):
  for filename in filenames:
    if filename[-1] == 'z':
      continue
    print (filename)
    with open(dirpath+"/"+filename, "r") as inp:
      first_line = inp.readline()
      date_indexes = []
      first_line_list = first_line.strip().split('|')
      for i in range(len(first_line_list)):
        m1 = re.match(r'^\d{4}-\d{2}-\d{2}$', first_line_list[i])
        m2 = re.match(r'^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}\+\d{4}$', first_line_list[i])
        if m1:
          print ("m1 match found")
          date_indexes.append((i, 0))
        elif m2:
          print ("m2 match found")
          date_format = 1
          date_indexes.append((i, 1))

      if len(date_indexes) == -1:
        continue

      if os.path.isfile(dirpath+"/"+filename+"z"):
        continue

      # File requires update
      print ("Update in filename: " + filename)
      newfile = open(dirpath+"/"+filename+"z", "w")
      first_line_list = get_updated_line(first_line, date_indexes)
      newfile.write('|'.join(first_line_list))
      newfile.write('\n')
      for line in inp:
        llist = get_updated_line(line, date_indexes)
        newfile.write('|'.join(llist))
        newfile.write('\n')
