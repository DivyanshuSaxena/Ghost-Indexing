import os
import sys

path = sys.argv[1]

for (dirpath, dirname, filename) in os.walk(path):
  print (filename)