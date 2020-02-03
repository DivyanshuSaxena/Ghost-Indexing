# Read the config.ini file for the respective query

import os
from configparser import ConfigParser

dirname = os.path.dirname(__file__)
def read_section(query, section='EXEC'):
  config_file = os.path.join(dirname, '../../config/query' + query + '.ini')
  parser = ConfigParser()
  parser.read(config_file)

  db = {}
  if section in parser:
    params = parser.items(section)
    for param in params:
      print (param)
      if section == 'QUERY':
        db[int(param[0][5])] = param[1]
      else:
        db[param[0]] = param[1]
  
  return db   
