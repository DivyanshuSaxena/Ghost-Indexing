package Queries;

import main.IndexRangeQuery;
import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.in;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class IndexQuery12 extends Query{

    public static void main(String[] args) throws Exception {
        IndexQuery12 query = new IndexQuery12();
        query.runQuery(null);
        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        String date = "2010-11-02T20:16:15.144+0000";

        if (params != null && params.size() > 0){
            date = params.get(0);
        }

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        String maxDate = "2015-08-02T20:16:15.144+0000";
        long likeThreshold = 10;

        long startTime = System.currentTimeMillis();

        IndexRangeQuery irq = new IndexRangeQuery();
        ArrayList<Vertex> vertices = irq.searchRange(g, dateFormat.parse(date), dateFormat.parse(maxDate), "post_creationDate_index100");

        long endTime1   = System.currentTimeMillis();
        long totalTime1 = endTime1 - startTime;

//        System.out.println(vertices);
        System.out.println(vertices.size());

//        List<Object> result = g.V(vertexIds).hasLabel("post").limit(100)
//                .as("messagesx")
//                .in("likes")
//                .groupCount().by(select("messagesx"))
//                .unfold().filter(it -> {
//                    return (Long) ((Map.Entry) it.get()).getValue() > likeThreshold;
//                }).toList();

        List<Vertex> result = g.V(vertices).hasLabel("post")//.limit(400)
                .as("messagesx")
                .where(in("likes").count().is(P.gt(likeThreshold))).toList();

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();


        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);
        System.out.println("TotalTime1: "+ totalTime1);

//        System.out.println("Result: "+result);
        System.out.println("Result: "+result.size());

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q12: ("+params+")");
        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);

        return queryResult;
    }

}
