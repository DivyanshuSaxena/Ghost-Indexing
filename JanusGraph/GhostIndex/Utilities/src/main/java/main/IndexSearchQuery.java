package main;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static org.janusgraph.graphdb.database.serialize.AttributeUtil.compare;

public class IndexSearchQuery {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexSearchQuery.class);

    private Vertex root;

    public IndexSearchQuery(GraphTraversalSource g, String indexName) {
        root = getIndexRoot(g, indexName);
    }

    public IndexSearchQuery(Vertex root) {
        this.root = root;
    }

    public static void main(String[] args) throws ParseException {
        JanusGraph graph = JanusGraphFactory.open("conf/baadal/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        IndexSearchQuery indexSearchQuery = new IndexSearchQuery(g, "post_creationDate_index");

        String searchKey = "2010-01-06T07:31:46.667+0000";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        Date dateVar1 = dateFormat.parse(searchKey);
        System.out.println(indexSearchQuery.searchKey(g, dateVar1));

        System.out.println("-------------------------");

        System.exit(0);
    }

    private Vertex getIndexRoot(GraphTraversalSource g, String indexName){
        return  g.V().has("id", "0").hasLabel("INDEX").has("name", indexName).toList().get(0);
    }

    /*
    * Given a key find nodes for this key on the given index
    * */
    public Vertex searchKey(GraphTraversalSource g, Object key) {
        Vertex indexRoot = root;

        Vertex vOld = null;
        Vertex vNew = indexRoot;
        Object searchKey = key;

        Vertex result = null;
        Iterator<Edge> edges = null;
        int i = 0;
        while(true){
            vOld = vNew;
            edges = vOld.edges(Direction.OUT,"INDEX_DATA_EDGE");
            for (Iterator<Edge> it = edges; it.hasNext(); ) {
                Edge e = it.next();

                if(e.value("val").equals(searchKey)){
                    result = e.inVertex();
                    break;
                }
            }
            if(result!=null){
                break;
            }
            edges = vOld.edges(Direction.OUT,"INDEX_EDGE");
            if(!edges.hasNext()){
                System.out.println("Result not found");
                break;
            }

            for (Iterator<Edge> it = edges; it.hasNext(); ) {
                Edge e = it.next();

                if(compare(e.value("min"),searchKey) <= 0 && compare(e.value("max"),searchKey) >= 0 ) {
                    vNew = e.inVertex();
                    break;
                }
            }
            if(vNew == vOld){
                break;
            }

        }
        return result;
    }
}

