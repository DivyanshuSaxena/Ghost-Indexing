// Query 12 (LDBC SNB BI Dataset) using Ghost Indexes
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

public class BIndexQuery12 extends BaseQuery {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Query12 execution.");

        BIndexQuery12 q = new BIndexQuery12();
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

            List<Vertex> result = g.V().has("index_id", -1).out().has("name", "post_creationDate_index_bPlus_2000")
                    .repeat(__.outE("INDEX_EDGE").not(__.has("max", P.lte(date))).inV())
                    .until(__.out("INDEX_EDGE").count().is(0)).outE("INDEX_DATA_EDGE").has("val", P.gte(date)).inV()
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
