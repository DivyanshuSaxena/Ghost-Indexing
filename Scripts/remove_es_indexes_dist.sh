curl -X GET http://10.17.5.53:9210/_cat/indices\?v\=
echo "\n"
curl -X DELETE http://10.17.5.53:9210/janusgraph_birthday
echo "\n"
curl -X DELETE http://10.17.5.53:9210/janusgraph_p_creationdate
echo "\n"
curl -X DELETE http://10.17.5.53:9210/janusgraph_po_creationdate
echo "\n"
curl -X DELETE http://10.17.5.53:9210/janusgraph_f_creationdate
echo "\n"
curl -X DELETE http://10.17.5.53:9210/janusgraph_c_creationdate
echo "\n"
curl -X DELETE http://10.17.5.53:9210/janusgraph_po_length
echo "\n"
echo "Removed Mixed Indices from ES cluster\n"
curl -X GET http://10.17.5.53:9210/_cat/indices\?v\=

