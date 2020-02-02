MATCH p=()-[r:super_index_edge]->() DELETE r;

MATCH p=()-[r:index_data_edge]->() DELETE r;

MATCH p=()-[r:index_edge]->() DELETE r;

MATCH p=()-[r:leaf_edge]->() DELETE r;

MATCH (n:super_index) DELETE n;

DROP INDEX ON :Message(id);

DROP INDEX ON :index(index_id);

MATCH (n:index) DELETE n;