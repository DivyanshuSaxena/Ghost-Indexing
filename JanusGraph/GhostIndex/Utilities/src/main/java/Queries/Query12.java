// Query 12 (LDBC SNB BI Dataset) using ES Indexes
// 
// Params:
// date: used by Query 12
// likeThreshold: used by Query 12
// numTries: Number of tries to run the parameter in a single graph instance
package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

import java.text.ParseException;
import java.util.List;

public class Query12 extends BaseQuery {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Query12 execution.");

        Query12 q = new Query12();
        q.confFile = "local";
        JanusGraph graph = JanusGraphFactory.open("../conf/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();
        q.runQuery(g, null);

        System.exit(0);
    }

    public QueryResult runQuery(GraphTraversalSource g, List<String> params) throws ParseException {
        long date = 0;
        long likeThreshold = 10;
        int numTries = 0;
        if (params == null || params.size() == 0) {
            date = 1311273000000L;
            likeThreshold = 400;
            numTries = 3;
        } else {
            date = Long.parseLong(params.get(0));
            likeThreshold = Integer.parseInt(params.get(1));
            numTries = Integer.parseInt(params.get(2));
        }

        // Numbers to be reported
        int resultSize = 0;
        long averageTime = 0;
        long coldStartTime = 0;

        for (int it = 0; it < numTries; it++) {
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

            List<Vertex> result = g.V().has("po_creationDate", P.gte(date))
                    .where(__.inE("likes").count().is(P.gt(likeThreshold))).toList();

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            if (it == 0) {
                coldStartTime = totalTime;
            } else {
                averageTime += totalTime;
            }
            resultSize = result.size();

            System.out.println("============" + date + "|" + likeThreshold + "============");
            System.out.println("DataSize: " + result.size());
            System.out.println("TotalTime: " + totalTime);
            System.out.println("==========================================");
        }

        averageTime = averageTime / (numTries - 1);
        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q12: (" + params + ")");
        queryResult.setResultCount(resultSize);
        queryResult.setColdStartTime(coldStartTime);
        queryResult.setWarmCacheTime(averageTime);
        queryResult.setTimeToTraverseIndex(-1);
        return queryResult;
    }

}
