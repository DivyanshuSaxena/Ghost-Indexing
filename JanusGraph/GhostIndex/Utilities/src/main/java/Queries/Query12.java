package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.in;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class Query12 extends Queries.Query {
    private static final Logger LOGGER = LoggerFactory.getLogger(Query12.class);

    public static void main(String[] args) throws Exception {
        Query12 query = new Query12();
        query.runQuery(null);
        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {

        Long date = 1312137000000L;
        long likeThreshold = 10;
        int distributed = 0;

        if (params != null && params.size() > 0) {
            date = Long.parseLong(params.get(0));
            likeThreshold = Integer.parseInt(params.get(1));
            distributed = Integer.parseInt(params.get(2));
        }
        System.out.println(date + " | " + likeThreshold);

        if (distributed == 1) {
            graph = JanusGraphFactory.build().set("storage.backend", "cassandrathrift")
                    .set("storage.hostname", "10.17.5.53")
                    .set("storage.cassandra.thrift.frame_size_mb", "60")
                    .set("storage.cassandra.thrift.max_message_size_mb", "61")
                    .set("index.search.backend", "elasticsearch")
                    .set("index.search.hostname", "10.17.5.53:9210").open();
        } else {
            graph = JanusGraphFactory.open("conf/" + confFile + "/janusgraph-cassandra-es.properties");
        }
        GraphTraversalSource g = graph.traversal();
        // SimpleDateFormat dateFormat = new
        // SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        Date dateVar = new Date(date);

        long startTime = System.currentTimeMillis();

        // This code below works too!! but the one below
        // uses "where" clause and is much smaller!

        // List<Object> result = g.V().hasLabel("post")
        // .has("creationDate", P.gt(dateVar1)).limit(100).as("messagesx")
        // .in("likes")
        // .groupCount().by(select("messagesx"))
        // .unfold().filter(it -> {
        // return (Long) ((Map.Entry) it.get()).getValue() > likeThreshold;
        // }).toList();

        List<Vertex> result = g.V()// .hasLabel("post")
                .has("po_creationDate", P.gte(dateVar))// .limit(400)
                .toList();
        // .as("messagesx")
        // .where(in("likes").count().is(P.gt(likeThreshold))).toList();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();

        System.out.println("==========================================");
        System.out.println("TotalTime: " + totalTime);

        // System.out.println("Result: "+result);
        System.out.println("Result: " + result.size());

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q12: (" + params + ")");
        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(-1);
        queryResult.setResults(result);
        queryResult.setTimeToTraverseIndex(totalTime);

        return queryResult;
    }

}
