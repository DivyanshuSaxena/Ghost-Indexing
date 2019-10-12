package main;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.B_LP_O_S_SE_SL_Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.B_O_Traverser;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal.Symbols.select;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class TestConnectionM extends __{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConnectionM.class);

    public static void main(String[] args) {
        JanusGraph graph = JanusGraphFactory.open("conf/local/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();
        Date zeroDateVar = new Date(0);

        Vertex c1 = g.V().has("pl_name", "Aden").next();//.has("name", textContains(country1)).next();
        Vertex c2 = g.V().has("pl_name", "Jordan").next();

        String tagName1 = "Josip_Broz_Tito";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        String date1 = "2009-11-14T20:16:15.144+0000";
        Date dateVar1 = null;
        try {
            dateVar1 = dateFormat.parse(date1);
        } catch(Exception w) {
            w.printStackTrace();
        }
        System.out.println(dateVar1.getTime());

        String home= "India";

            System.out.println(g.V()
                .has("po_creationDate", P.gt(dateVar1))//.limit(1000)
                .where(out("hasTag").has("t_name",tagName1))
//                .toList()
                .out("hasCreator").as("personsx")
                .groupCount().by(select("personsx")).unfold().toList());

//        String a = new __("a");
//        System.out.println(__.__("a").next());
//        //LOGGER.info("Vertex Count: "+g.V().count());
////        System.out.println("YO: " + g.V().hasLabel("person").as("a").map(__.bothE("knows").otherV().count()).as("b").select("a", "b").toList());
//        System.out.println("HI: " + g.V(1573024).as("a").out("anc").select("a")//.has("p_creationDate", P.gt(zeroDateVar))
//                .map(__.out("knows").sideEffect(it1 -> {System.out.println("y1**: " + it1);})
//
//                        .out("knows").select("a").sideEffect(it1 -> {System.out.println(" y2: " + it1);})
//                        .filter(it -> {
////                                System.out.println(__.select("a").getClass());
//                                System.out.println(((Vertex)it.get()).values("p_id").next().equals(__.select("a").unfold()));
//                            return true;//.get().values("po   _id").next().equals(__.select("a").sideEffect(it1 -> {System.out.println("y: " + it1);})
//                                        //.values("p_id").next());
//                            })
////                        .out("knows").sideEffect(it -> {System.out.print("y: " + it);})
////                        .is( select("a").unfold().sideEffect(it -> { System.out.println(" go: " + it);} ))
////                        .has("p_id", __.select("a").sideEffect(it -> {System.out.println(" o: " + it);} ).values("p_id"))
//                        .sideEffect(it -> {System.out.println(" yolo: " + it);} )
//                        .count() ).as("b")
//                .select("a", "b").toList());
        System.exit(0);
    }
}

