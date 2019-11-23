package main;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import static org.janusgraph.graphdb.database.serialize.AttributeUtil.compare;

public class IndexRangeQueryPre {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexRangeQueryPre.class);

    private Vertex root;

    public IndexRangeQueryPre(GraphTraversalSource g, String indexName) {
        root = getIndexRoot(g, indexName);
        System.out.println("--------------" + root);
    }

    public IndexRangeQueryPre(Vertex root) {
        this.root = root;
    }

    /*
    * FIXME: Make the 'Global Root Vertex' pointing to all indices and get root for this index
    * */
    private Vertex getIndexRoot(GraphTraversalSource g, String indexName){
        return  g.V().has("id", "0").hasLabel("INDEX").has("name", indexName).toList().get(0);
    }


    /*
    * Given a minKey and a maxKey find all nodes between the min-max on the given index
    * */
    public ArrayList<Vertex> searchRange(Object minKey, Object maxKey){

        Vertex indexRoot = root;

        System.out.println("ROOT: "+indexRoot);
        Stack<Vertex> vStack = new Stack<Vertex>();
        vStack.push(indexRoot);

        ArrayList<Vertex> result = new ArrayList<>();
        Iterator<Edge> edges = null;
        int i = 0;
        while(!vStack.empty()){
            Vertex v = vStack.pop();

            edges = v.edges(Direction.OUT,"INDEX_DATA_EDGE");
            for (Iterator<Edge> it = edges; it.hasNext(); ) {
                Edge e = it.next();
                if(compare(minKey, e.value("val")) <= 0 && compare(maxKey, e.value("val")) >= 0){
                    result.add(e.inVertex());
                }
            }
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

