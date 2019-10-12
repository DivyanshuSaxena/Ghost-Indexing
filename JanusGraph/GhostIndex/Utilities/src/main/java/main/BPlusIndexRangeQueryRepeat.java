package main;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
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

public class BPlusIndexRangeQueryRepeat {
    private static final Logger LOGGER = LoggerFactory.getLogger(BPlusIndexRangeQueryRepeat.class);

    private Vertex root;

    public BPlusIndexRangeQueryRepeat(GraphTraversalSource g, String indexName) {
        root = getIndexRoot(g, indexName);
    }

    public BPlusIndexRangeQueryRepeat(Vertex root) {
        this.root = root;
    }

    public static void main(String[] args) throws ParseException {
        JanusGraph graph = JanusGraphFactory.open("conf/baadal/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        BPlusIndexRangeQueryRepeat indexSearchQuery = new BPlusIndexRangeQueryRepeat(g, "post_creationDate_index_bPlus_50");

        String searchMinKey = "2009-01-06T07:31:46.667+0000";
        String searchMaxKey = "2010-05-06T07:31:46.667+0000";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        Date dateVar1 = dateFormat.parse(searchMinKey);
        Date dateVar2 = dateFormat.parse(searchMaxKey);
        System.out.println(indexSearchQuery.searchRange(dateVar1, dateVar2));

        System.out.println("-------------------------");

        System.exit(0);
    }

    private Vertex getIndexRoot(GraphTraversalSource g, String indexName){
        return g.V().has("index_id", -1).hasLabel("SUPER_INDEX").out("SUPER_INDEX_EDGE").has("name",indexName).next();

//        return  g.V().has("index_id", "0").hasLabel("INDEX").has("name", indexName).toList().get(0);
    }

    /*
    * Given a key find nodes for this key on the given index
    * */

    public ArrayList<Vertex> searchRange(Object minKey, Object maxKey){
//        if (compare(minKey, maxKey) > 0) {
//            System.out.println("Range ill-defined");
//            return null;
//        }

        System.out.println("minKey: "+minKey);
        System.out.println("maxKey: "+maxKey);

        Vertex startLeaf = searchGreaterThan(minKey);   // can replace by search()
        if (startLeaf == null) {
            System.out.println("yo");
            return null;
        }
//        System.out.println("StartAt " + startLeaf);
        Vertex endLeaf = searchLesserThan(maxKey);      // can replace by search()
        if (endLeaf == null) {
            System.out.println("yo222");
            return null;
        }

        System.out.println("startleaf: "+ startLeaf);
        System.out.println("EndAt: " + endLeaf);

        ArrayList<Vertex> ret = new ArrayList<>();
        ArrayList<Vertex> ret1 = new ArrayList<>();
        if (!startLeaf.id().toString().equals(endLeaf.id().toString())) {
            Iterator<Edge> edges = startLeaf.edges(Direction.OUT, "INDEX_DATA_EDGE");
            while (edges.hasNext()) {
                Edge outE = edges.next();

                if (compare(outE.value("val"),minKey) >= 0) {
//                    System.out.println("x: "+outE.value("val"));
                    ret1.add(outE.inVertex());
                }
            }
            startLeaf = startLeaf.edges(Direction.OUT, "NEXT_LEAF_EDGE").next().inVertex();
        }
        /*
            while (!startLeaf.id().toString().equals(endLeaf.id().toString())) {
                Iterator<Edge> edges = startLeaf.edges(Direction.OUT, "INDEX_DATA_EDGE");
                while (edges.hasNext()) {
                    Edge outE = edges.next();
                    ret.add(outE.inVertex());
    //                System.out.println("yyy: "+outE.value("val"));
                }
    //            System.out.println(startLeaf);
                startLeaf = startLeaf.edges(Direction.OUT, "NEXT_LEAF_EDGE").next().inVertex();
            }
        */
        Object endIndexId = endLeaf.value("index_id");
        GraphTraversal<Vertex, Vertex> leaves = __.__(startLeaf).emit().repeat(__.out("NEXT_LEAF_EDGE").simplePath()).until(__.has("index_id", endIndexId));

        leaves.out("INDEX_DATA_EDGE");
        ret.addAll(leaves.toList());


        Iterator<Edge> edges = endLeaf.edges(Direction.OUT, "INDEX_DATA_EDGE");
        while (edges.hasNext()) {
            Edge outE = edges.next();
            if (compare(outE.value("val"),minKey) >= 0 && compare(outE.value("val"),maxKey) <= 0) {
                ret.add(outE.inVertex());
            }
        }
        System.out.println("here");
        System.out.println("size: "+ret.size() + ret1.size());
        return ret;
    }

    public Vertex searchGreaterThan(Object searchKey) {
        Vertex indexRoot = root;
        Vertex vOld = null;
        Vertex vNew = indexRoot;
        Vertex result = indexRoot;
        Iterator<Edge> edges = null;


        while((edges = vNew.edges(Direction.OUT,"INDEX_EDGE")).hasNext()){
            vOld = vNew;

//            Object minVal = null;
            for (Iterator<Edge> it = edges; it.hasNext(); ) {
                Edge e = it.next();

//                if(minVal == null){
//                    minVal = e.property("min");
//                }

                if (compare(e.value("max"),searchKey) < 0)
                    continue;
                if (compare(e.value("min"),searchKey) <= 0) {
                    vNew = e.inVertex();
                    break;
                }

                // compare(searchKey, e.value("min")) < 0 => true already
//                if ( compare(e.value("min"), minVal) <= 0) {
//                    minVal = e.property("min");
//                    vNew = e.inVertex();
//                }
            }
            if(vNew == vOld){
                System.out.println("No Results Found");
                return null;
            }
        }
        return vNew;
    }

    public Vertex searchLesserThan(Object searchKey) {
        Vertex indexRoot = root;
        Vertex vOld = null;
        Vertex vNew = indexRoot;
        Vertex result = indexRoot;
        Iterator<Edge> edges = null;
        int i = 0;
        while((edges = vNew.edges(Direction.OUT,"INDEX_EDGE")).hasNext()){
            vOld = vNew;
//            Object maxVal = edges.next().property("max");

            for (Iterator<Edge> it = edges; it.hasNext(); ) {
                Edge e = it.next();

                if (compare(e.value("min"),searchKey) > 0)
                    continue;
                if (compare(e.value("max"),searchKey) >= 0) {
                    vNew = e.inVertex();
                    break;
                }
//                if (compare(searchKey, e.value("max")) > 0 && compare(e.value("max"), maxVal) >= 0) {
//                    maxVal = e.property("max");
//                    vNew = e.inVertex();
//                }
            }
            if(vNew == vOld){
                System.out.println("No Results Found");
                return null;
            }
        }
        return vNew;
    }

    public Vertex search(Object searchKey) {
        Vertex indexRoot = root;
        Vertex vOld = null;
        Vertex vNew = indexRoot;
        Vertex result = null;
        Iterator<Edge> edges = null;
        int i = 0;
        while((edges = vNew.edges(Direction.OUT,"INDEX_EDGE")).hasNext()){
            vOld = vNew;
            Object maxVal = edges.next().property("max");
            for (Iterator<Edge> it = edges; it.hasNext(); ) {
                Edge e = it.next();

                if (compare(e.value("min"),searchKey) <= 0 && compare(e.value("max"),searchKey) >= 0) {
                    vNew = e.inVertex();
                    break;
                }
            }
            if(vNew == vOld){
                System.out.println("No Results Found");
                return null;
            }
        }
        return result;
    }
}
