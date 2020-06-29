// Add Index nodes to Data nodes edges
USING PERIODIC COMMIT 10000
LOAD CSV FROM '{INDEXHANDLER_DIR}/indexDataEdges.csv' AS line FIELDTERMINATOR '|'
MATCH (i:index {index_id:toInteger(line[0])}) WHERE i.name = line[4]
WITH i as i, line as line
MATCH (n:{DATA_NODE}) WHERE n.id = toInteger(line[1])
CREATE (i)-[r:index_data_edge {val: toInteger(line[3])}]->(n);

MATCH (root:index), (su:super_index) WHERE root.name = "{INDEX_NAME}" AND root.index_id = 0
CREATE (su)-[r:super_index_edge {attribute: "{ATTRIBUTE_NAME}", index_type: "{INDEX_TYPE}"}]->(root);

USING PERIODIC COMMIT 10000
LOAD CSV FROM '{INDEXHANDLER_DIR}/leafEdges.csv' AS line FIELDTERMINATOR '|'
MATCH (l1:index {index_id:toInteger(line[0])}) WHERE l1.name = line[2]
WITH l1 as l1,  line as line
MATCH (l2:index {index_id:toInteger(line[1])}) WHERE l2.name = line[2]
CREATE (l1)-[r:leaf_edge]->(l2);