// Query 4 (LDBC SNB BI Dataset) using ES Indexes
// 
// Params:
// tagClass: used by Query 4
// country: used by Query 4
// numTries: Number of tries to run the parameter in a single graph instance
package Queries;

import main.QueryResult;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class Query4 extends BaseQuery {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Query4 execution.");

        Query4 q = new Query4();
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

            List<Map<Object, Long>> result = g.V().has("pl_name", country).in("isPartOf").in("isLocatedIn")
                    .as("personx").in("hasModerator").as("forumx", "forumx_title").out("containerOf").as("postx")
                    .out("hasTag").out("hasType").has("tc_name", tagClass)
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
