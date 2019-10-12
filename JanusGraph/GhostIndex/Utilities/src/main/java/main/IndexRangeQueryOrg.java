package main;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.SchemaAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import static org.janusgraph.graphdb.database.serialize.AttributeUtil.compare;

public class IndexRangeQueryOrg {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexRangeQueryOrg.class);

    public static void main(String[] args) throws ParseException {
        JanusGraph graph = JanusGraphFactory.open("conf/baadal/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        JanusGraphManagement management = graph.openManagement();
        JanusGraphIndex index_id = management.getGraphIndex("index_id");
        try {
            management.updateIndex(index_id, SchemaAction.DISABLE_INDEX).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        management.commit();

        IndexRangeQueryOrg indexRangeQuery = new IndexRangeQueryOrg();

        String minKey1 = "2010-01-06T07:31:46.667+0000";
        String maxKey1 = "2010-02-06T07:31:46.667+0000";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        Date minKey = dateFormat.parse(minKey1);
        Date maxKey = dateFormat.parse(maxKey1);

//        String minKey = "Aden";
//        String maxKey = "Jordan";

        ArrayList<Vertex> result = indexRangeQuery.searchRange(g, minKey, maxKey, "post_creationDate_index");

        graph.close();
        System.out.println("-------------------------");
        System.out.println("Result Vertices: "+ result);
        System.exit(0);
    }

    /*
    * FIXME: Make the 'Global Root Vertex' pointing to all indices and get root for this index
    * */
    public Vertex getIndexRoot(GraphTraversalSource g, String indexName){
        return g.V().has("index_id", -1).hasLabel("SUPER_INDEX").out("SUPER_INDEX_EDGE").has("name",indexName).next();
    }



    /*
    * Given a minKey and a maxKey find all nodes between the min-max on the given index
    * */
    public ArrayList<Vertex> searchRange(GraphTraversalSource g, Object minKey, Object maxKey, String indexName){

        System.out.println("getting root");
        Vertex indexRoot = getIndexRoot(g, indexName);

        System.out.println("got root");

//        System.out.println("ROOT: "+indexRoot);
        Stack<Vertex> vStack = new Stack<Vertex>();
        vStack.push(indexRoot);

        ArrayList<Vertex> result = new ArrayList<>();
        Iterator<Edge> edges = null;

        while(!vStack.empty()){
            Vertex v = vStack.pop();
//            System.out.println("-1");
//            System.out.println(vStack.size());

//            int decount = 0, tcount=0;
            edges = v.edges(Direction.OUT,"INDEX_DATA_EDGE");
            for (Iterator<Edge> it = edges; it.hasNext(); ) {
                Edge e = it.next();
//                tcount++;
                if(compare(minKey, e.value("val")) <= 0 && compare(maxKey, e.value("val")) >= 0){
                    result.add(e.inVertex());
//                    decount++;
                }
            }
//            System.out.println("de+" + decount + " / " + tcount);

            edges = v.edges(Direction.OUT,"INDEX_EDGE");


            int count = 0;
            for (Iterator<Edge> it = edges; it.hasNext(); ) {
                Edge e = it.next();

                if((compare(e.value("min"),minKey) <= 0 && compare(e.value("max"),minKey) >= 0)
                        || (compare(e.value("min"), maxKey) <= 0 && compare(e.value("max"), maxKey) >= 0)
                        || (compare(e.value("min"), minKey) >= 0 && compare(e.value("max"), maxKey) <= 0)) {
                    vStack.push(e.inVertex());
                    count++;
                }
            }
//            System.out.println("+" + count);

        }
        return result;
    }
}

