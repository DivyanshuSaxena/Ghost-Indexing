// Query 4 (LDBC SNB BI Dataset) using ES Indexes
// 
// Params:
// tagClass: used by Query 4
// country: used by Query 4
// numTries: Number of tries to run the parameter in a single graph instance
package Queries;

import main.QueryResult;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class BIndexQuery4 extends BaseQuery {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Query4 execution.");

        BIndexQuery4 q = new BIndexQuery4();
        q.confFile = "local";
        JanusGraph graph = JanusGraphFactory.open("../conf/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();
        q.runQuery(g, null);

        System.exit(0);
    }

    public QueryResult runQuery(GraphTraversalSource g, List<String> params) throws ParseException {
        String tagClass = "";
        String country = "";
        int numTries = 0;
        if (params == null || params.size() <= 1) {
            tagClass = "OfficeHolder";
            country = "Egypt";
            numTries = 3;
        } else {
            tagClass = params.get(0);
            country = params.get(1);
            numTries = Integer.parseInt(params.get(2));
        }

        // Numbers to be reported
        int resultSize = 0;
        long averageTime = 0;
        long coldStartTime = 0;

        for (int it = 0; it < numTries; it++) {
            long startTime = System.currentTimeMillis();

            List<Map<Object, Long>> result = g.V().has("index_id", -1).out().has("name", "tagclass_bTree_20")
                    .repeat(__.outE("INDEX_EDGE").has("min", P.lte(tagClass)).has("max", P.gte(tagClass)).inV())
                    .until(__.out("INDEX_EDGE").count().is(0)).outE("INDEX_DATA_EDGE").has("val", P.eq(tagClass)).inV()
                    .in("hasType").in("hasTag").as("postx").in("containerOf").as("forumx", "forumx_title")
                    .out("hasModerator").as("personx").out("isLocatedIn").out("isPartOf").has("pl_name", country)
                    .select("forumx", "forumx_title", "postx", "personx").groupCount()
                    .by(select("personx", "forumx", "forumx_title").by("p_id").by("f_id").by("title")).toList();

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            if (it == 0) {
                coldStartTime = totalTime;
            } else {
                averageTime += totalTime;
            }
            resultSize = result.get(0).size();

            System.out.println("============" + tagClass + "|" + country + "============");
            System.out.println("DataSize: " + result.get(0).size());
            System.out.println("TotalTime: " + totalTime);
            System.out.println("==========================================");
        }

        averageTime = averageTime / (numTries - 1);
        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q4: (" + params + ")");
        queryResult.setResultCount(resultSize);
        queryResult.setColdStartTime(coldStartTime);
        queryResult.setWarmCacheTime(averageTime);
        queryResult.setTimeToTraverseIndex(-1);
        return queryResult;
    }

}
