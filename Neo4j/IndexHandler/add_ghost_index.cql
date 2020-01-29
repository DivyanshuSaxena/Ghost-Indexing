// Add Super Index
CREATE (:super_index {index_id: -1});

USING PERIODIC COMMIT 100
LOAD CSV FROM '{INDEXHANDLER_DIR}/indexVertices.csv' AS line FIELDTERMINATOR '|'
CREATE (:index { index_id: toInteger(line[0]), name: line[1]});

// Create Index on index nodes before adding edges
CREATE INDEX ON :index(index_id);

USING PERIODIC COMMIT 10000
LOAD CSV FROM '{INDEXHANDLER_DIR}/indexEdges.csv' AS line FIELDTERMINATOR '|'
MATCH (i1:index), (i2:index) WHERE i1.index_id = toInteger(line[0]) AND i2.index_id = toInteger(line[1]) AND i1.name = line[4] AND i2.name = line[4]
CREATE (i1)-[r:index_edge {min: line[2], max: line[3]}]->(i2);

// Create Index on Data Node type
CREATE INDEX ON :{DATA_NODE}(id);

USING PERIODIC COMMIT 10000
LOAD CSV FROM '{INDEXHANDLER_DIR}/indexDataEdges.csv' AS line FIELDTERMINATOR '|'
MATCH (i:index {index_id:toInteger(line[0])}) WHERE i.name = line[4]
WITH i as i, line as line
MATCH (n:{DATA_NODE}) WHERE n.id = toInteger(line[1])
CREATE (i)-[r:index_data_edge {val: line[3]}]->(n);

MATCH (root:index), (su:super_index) WHERE root.name = "{ATTRIBUTE_NAME}_index_{INDEX_TYPE}_2000" AND root.index_id = 0
CREATE (su)-[r:super_index_edge {attribute: "{ATTRIBUTE_NAME}", index_type: "{INDEX_TYPE}"}]->(root);

USING PERIODIC COMMIT 10000
LOAD CSV FROM '{INDEXHANDLER_DIR}/leafEdges.csv' AS line FIELDTERMINATOR '|'
MATCH (l1:index {index_id:toInteger(line[0])}) WHERE l1.name = line[2]
WITH l1 as l1,  line as line
MATCH (l2:index {index_id:toInteger(line[1])}) WHERE l2.name = line[2]
CREATE (l1)-[r:leaf_edge]->(l2);