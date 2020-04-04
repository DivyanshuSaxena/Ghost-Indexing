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
