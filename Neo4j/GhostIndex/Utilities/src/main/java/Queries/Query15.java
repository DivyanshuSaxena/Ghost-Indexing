package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.filter;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.in;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

public class Query15 extends Queries.Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query15.class);

    public static void main(String[] args) throws Exception {
        Query15 query = new Query15();
        query.runQuery(null);
        System.exit(0);
    }


    public QueryResult runQuery(List<String> params) throws ParseException {

        String country = "Kenya";

        if (params != null && params.size() > 0){
            country = params.get(0);
        }

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        long startTime = System.currentTimeMillis();
/*********************************************************************************************************
* */

        VertexProperty<String> x = null;
        long sumOfFriends = g.V().has("pl_name", country).in("isPartOf").in("isLocatedIn").as("personx")
                .out("knows").out("isLocatedIn").out("isPartOf").has("pl_name",country).count().next();
        long persons = g.V().has("pl_name", country).in("isPartOf").in("isLocatedIn").as("personx").count().next();

        long avg = (long) Math.floor(sumOfFriends/persons);

        GraphTraversal<Vertex, Vertex> result = g.V().has("pl_name",country).in("isPartOf").in("isLocatedIn")
                .filter(out("knows").out("isLocatedIn").out("isPartOf").has("pl_name",country).count().is(avg));

//        out("isLocatedIn").out("isPartOf").has("pl_name",country).count().equals((Double) avg));
//                .has("creationDate", P.gt(dateVar1)).limit(100).as("messagesx")
//                .in("likes")
//                .groupCount().by(select("messagesx"))
//                        .unfold().filter(it -> {
//                            return (Long) ((Map.Entry) it.get()).getValue() > likeThreshold;
//                        }).toList();


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);

//        System.out.println("Result: "+result);
//        System.out.println("Result: "+result.size());

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q12: ("+params+")");
//        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);

        return queryResult;
    }

}

