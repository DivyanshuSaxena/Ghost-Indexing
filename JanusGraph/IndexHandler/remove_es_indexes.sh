curl -X GET http://127.0.0.1:9200/_cat/indices\?v\=
echo "\n"
curl -X DELETE http://127.0.0.1:9200/janusgraph_birthday
echo "\n"
curl -X DELETE http://127.0.0.1:9200/janusgraph_p_creationdate
echo "\n"
curl -X DELETE http://127.0.0.1:9200/janusgraph_po_creationdate
echo "\n"
curl -X DELETE http://127.0.0.1:9200/janusgraph_f_creationdate
echo "\n"
curl -X DELETE http://127.0.0.1:9200/janusgraph_c_creationdate
echo "\n"
curl -X DELETE http://127.0.0.1:9200/janusgraph_po_length
echo "\n"
echo "Removed Mixed Indices from ES cluster\n"
curl -X GET http://127.0.0.1:9200/_cat/indices\?v\=

