// Query 1 (LDBC SNB BI Database) using Ghost Indexes
// 
// Params:
// date: used by Query 1
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
import java.util.Date;
import java.util.List;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

public class BIndexQuery1 extends BaseQuery {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Query1 execution.");

        BIndexQuery1 q = new BIndexQuery1();
        q.confFile = "local";
        JanusGraph graph = JanusGraphFactory.build().set("storage.backend", "cassandrathrift")
                .set("index.search.backend", "elasticsearch").open();
        GraphTraversalSource g = graph.traversal();
        q.runQuery(g, null);

        System.exit(0);
    }

    public QueryResult runQuery(GraphTraversalSource g, List<String> params) throws ParseException {
        long date = 0;
        int numTries = 0;

        if (params == null || params.size() == 0) {
            date = 1332354600000L;
            numTries = 3;
        } else {
            date = Long.parseLong(params.get(0));
            numTries = Integer.parseInt(params.get(1));
        }

        // Numbers to be reported
        int resultSize = 0;
        long averageTime = 0;
        long coldStartTime = 0;

        for (int it = 0; it < numTries; it++) {
            long startTime = System.currentTimeMillis();
            /*
             * FIXME: Create index on creationDate of comments and include that in query //
             *
             */
            List<Object> result = g.V().has("index_id", -1).out()
                    .has("name", "post_creationDate_index_bPlus_2000")
                    .repeat(__.outE("INDEX_EDGE").not(__.has("min", P.gte(date))).inV())
                    .until(__.outE("INDEX_EDGE").count().is(0)).outE("INDEX_DATA_EDGE").has("val", P.lte(date)).inV()
                    .has("po_length", P.gt(0)).group().by(item -> {
                        long value = ((Vertex) item).value("po_creationDate");
                        Date creationDate = new Date(value);
                        return creationDate.getYear() + 1900;
                    })
                    .by(groupCount().by(values("po_length").choose(is(P.lt(40)), constant("0"),
                            choose(is(P.lt(80)), constant("1"), choose(is(P.lt(160)), constant("2"), constant("3"))))))
                    .unfold().toList();

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            if (it == 0) {
                coldStartTime = totalTime;
            } else {
                averageTime += totalTime;
            }
            resultSize = result.size();

            System.out.println("====================" + date + "======================");
            System.out.println("DataSize: " + result.size());
            System.out.println("TotalTime: " + totalTime);
            System.out.println("==========================================");
        }

        averageTime = averageTime / (numTries - 1);
        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("BQ1: (" + date + ")");
        queryResult.setResultCount(resultSize);
        queryResult.setColdStartTime(coldStartTime);
        queryResult.setWarmCacheTime(averageTime);
        queryResult.setTimeToTraverseIndex(-1);
        return queryResult;
    }
}
