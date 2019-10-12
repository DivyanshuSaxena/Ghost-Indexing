package main;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.zookeeper.data.Id;
import org.janusgraph.core.*;
import org.janusgraph.core.attribute.Geo;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.*;
import org.janusgraph.example.GraphOfTheGodsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.janusgraph.core.attribute.Geo.geoWithin;
import static org.janusgraph.core.attribute.Text.textContains;
//import static org.janusgraph.core.attribute.Text.eq;
import static org.janusgraph.graphdb.database.serialize.AttributeUtil.compare;

public class TestConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConnection.class);

    public static void main(String[] args){
        JanusGraph graph = JanusGraphFactory.open("conf/baadal/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();
        try {
//            exceptionalFunction();
            System.out.println("sdsdsdsds");
        }catch (JanusGraphException e){
            e.printStackTrace();
            graph.close();
            System.out.println("Meow!!");
        }
        System.out.println("Super-cool!");
        graph.close();


    }

    public static void exceptionalFunction(){

        int a = 1;
        if(a==1){
            throw new JanusGraphException("BRO!!!");
        }
        System.out.println("You are done!");

    }

    public static void load(JanusGraph graph, String mixedIndexName, boolean uniqueNameCompositeIndex) {
        JanusGraphManagement mgmt = graph.openManagement();
        PropertyKey name = mgmt.makePropertyKey("name").dataType(String.class).make();
        JanusGraphManagement.IndexBuilder nameIndexBuilder = mgmt.buildIndex("name", Vertex.class).addKey(name, Mapping.STRING.asParameter());
//        if (uniqueNameCompositeIndex) {
//            nameIndexBuilder.unique();
//        }

//        JanusGraphIndex namei = nameIndexBuilder.buildCompositeIndex();
//        mgmt.setConsistency(namei, ConsistencyModifier.LOCK);
        JanusGraphIndex namei = nameIndexBuilder.buildMixedIndex("search");
        PropertyKey age = mgmt.makePropertyKey("age").dataType(Integer.class).make();
        if (null != mixedIndexName) {
            mgmt.buildIndex("vertices", Vertex.class).addKey(age).buildMixedIndex(mixedIndexName);
        }

        PropertyKey time = mgmt.makePropertyKey("time").dataType(Integer.class).make();
        PropertyKey date = mgmt.makePropertyKey("date").dataType(Date.class).make();
        PropertyKey reason = mgmt.makePropertyKey("reason").dataType(String.class).make();
        PropertyKey place = mgmt.makePropertyKey("place").dataType(Geoshape.class).make();
        if (null != mixedIndexName) {
//            mgmt.buildIndex("edges", Edge.class).addKey(reason).addKey(place).buildMixedIndex(mixedIndexName);
            System.out.println("uallaa");
            mgmt.buildIndex("edges", Edge.class).addKey(place).addKey(reason).buildMixedIndex(mixedIndexName);
            mgmt.buildIndex("lmao", Edge.class).addKey(reason).buildMixedIndex(mixedIndexName);
            mgmt.buildIndex("dateIdx", Edge.class).addKey(date).buildMixedIndex(mixedIndexName);
        }

        mgmt.makeEdgeLabel("father").multiplicity(Multiplicity.MANY2ONE).make();
        mgmt.makeEdgeLabel("mother").multiplicity(Multiplicity.MANY2ONE).make();
        EdgeLabel battled = mgmt.makeEdgeLabel("battled").signature(new PropertyKey[]{time}).make();
        mgmt.buildEdgeIndex(battled, "battlesByTime", Direction.BOTH, Order.decr, new PropertyKey[]{time});
        mgmt.makeEdgeLabel("lives").make();//.signature(new PropertyKey[]{reason}).make();
        mgmt.makeEdgeLabel("pet").make();
        mgmt.makeEdgeLabel("brother").make();
        mgmt.makeVertexLabel("titan").make();
        mgmt.makeVertexLabel("location").make();
        mgmt.makeVertexLabel("god").make();
        mgmt.makeVertexLabel("demigod").make();
        mgmt.makeVertexLabel("human").make();
        mgmt.makeVertexLabel("monster").make();
        mgmt.commit();
        JanusGraphTransaction tx = graph.newTransaction();
        Vertex saturn = tx.addVertex(new Object[]{T.label, "titan", "name", "saturn", "age", Integer.valueOf(10000)});
        Vertex sky = tx.addVertex(new Object[]{T.label, "location", "name", "sky"});
        Vertex sea = tx.addVertex(new Object[]{T.label, "location", "name", "sea"});
        Vertex jupiter = tx.addVertex(new Object[]{T.label, "god", "name", "jupiter", "age", Integer.valueOf(5000)});
        Vertex neptune = tx.addVertex(new Object[]{T.label, "god", "name", "neptune", "age", Integer.valueOf(4500)});
        Vertex hercules = tx.addVertex(new Object[]{T.label, "demigod", "name", "hercules", "age", Integer.valueOf(30)});
        Vertex alcmene = tx.addVertex(new Object[]{T.label, "human", "name", "alcmene", "age", Integer.valueOf(45)});
        Vertex pluto = tx.addVertex(new Object[]{T.label, "god", "name", "pluto", "age", Integer.valueOf(4000)});
        Vertex nemean = tx.addVertex(new Object[]{T.label, "monster", "name", "nemean"});
        Vertex hydra = tx.addVertex(new Object[]{T.label, "monster", "name", "hydra"});
        Vertex cerberus = tx.addVertex(new Object[]{T.label, "monster", "name", "cerberus"});
        Vertex tartarus = tx.addVertex(new Object[]{T.label, "location", "name", "tartarus"});
        jupiter.addEdge("father", saturn, new Object[0]);
        jupiter.addEdge("lives", sky, new Object[]{"reason", "lovesfreshbreezes"});
        jupiter.addEdge("brother", neptune, new Object[0]);
        jupiter.addEdge("brother", pluto, new Object[0]);
        neptune.addEdge("lives", sea, new Object[0]).property("reason", "loveswaves");
        neptune.addEdge("brother", jupiter, new Object[0]);
        neptune.addEdge("brother", pluto, new Object[0]);
        hercules.addEdge("father", jupiter, new Object[0]);
        hercules.addEdge("mother", alcmene, new Object[0]);
        hercules.addEdge("battled", nemean, new Object[]{"time", Integer.valueOf(1),"date",new Date(1000) , "place", Geoshape.point(38.1F, 23.7F)});
        hercules.addEdge("battled", hydra, new Object[]{"time", Integer.valueOf(2),"date",new Date(3000), "place", Geoshape.point(37.7F, 23.9F)});
        hercules.addEdge("battled", cerberus, new Object[]{"time", Integer.valueOf(12), "date",new Date(2000), "place", Geoshape.point(39.0F, 22.0F)});
        pluto.addEdge("brother", jupiter, new Object[0]);
        pluto.addEdge("brother", neptune, new Object[0]);
        pluto.addEdge("lives", tartarus, new Object[]{"reason", "nofearofdeath"});
        pluto.addEdge("pet", cerberus, new Object[0]);
        cerberus.addEdge("lives", tartarus, new Object[0]);
        tx.commit();
    }
}

