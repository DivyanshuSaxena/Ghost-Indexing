package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Query19 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query19.class);

    public static void main(String[] args) throws Exception {
        Query19 query = new Query19();
        query.runQuery(null);
        System.exit(0);
    }


    public QueryResult runQuery(List<String> params) throws ParseException {

        long minBirthday = 1268273000000L;
        String[] languages = {"uz"}; //uz, tk, ar

        if (params != null && params.size() > 0){
            minBirthday = Integer.parseInt(params.get(0));
        }

        Date bDateVar = new Date(minBirthday);
        System.out.println("minBirthday: " + bDateVar);

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        long startTime = System.currentTimeMillis();
/*********************************************************************************************************
 * */

        List<Vertex> result = g.V().has("birthday", P.gt(bDateVar)).as("a")
                .in("hasCreator")
                .hasLabel("comment").out("isReplyOf")
                .out("hasCreator")
                .filter(__.not(__.or(__.out("knows").is(__.select("a")))))
                .toList();

        g.V().map(it -> {

           return "a";
        });

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);

        System.out.println("Result: "+result);
        System.out.println("Result: "+result.size());

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q12: ("+params+")");
        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);

        return queryResult;
    }

}
