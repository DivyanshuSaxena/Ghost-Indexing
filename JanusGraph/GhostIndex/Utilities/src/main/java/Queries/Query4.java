package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;
import static org.janusgraph.core.attribute.Text.textContains;

public class Query4 extends Query {
    private static final Logger LOGGER = LoggerFactory.getLogger(Query4.class);

    public static void main(String[] args) throws Exception {
        Query4 q = new Query4();
        q.confFile = "local";
        q.runQuery(null);

        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        // Queries: (OfficeHolder, Jordan), (TennisPlayer, Jordan), (Saint, Jordan),
        // (Country, Jordan), (Albumn, Jordan)
        String tagClass = "";
        String country = "";
        long startTime = System.currentTimeMillis();
        int distributed = 0;

        if (params == null || params.size() <= 1) {
            tagClass = "OfficeHolder";
            country = "Egypt";
            distributed = 0;
        } else {
            tagClass = params.get(0);
            country = params.get(1);
            distributed = Integer.parseInt(params.get(2));
        }

        if (distributed == 1) {
            graph = JanusGraphFactory.build().set("storage.backend", "cassandrathrift")
                    .set("storage.hostname", "10.17.5.53")
                    .set("storage.cassandra.frame-size-mb", 60)
                    .set("index.search.backend", "elasticsearch")
                    .set("index.search.hostname", "10.17.5.53:9210").open();
        } else {
            graph = JanusGraphFactory.open("conf/" + confFile + "/janusgraph-cassandra-es.properties");
        }
        GraphTraversalSource g = graph.traversal();

        // List<Map<String, Object>> results = g.V().match(
        // __.as("forumx").out("containerOf").as("postx"),
        // __.as("forumx").out("hasModerator").out("isLocatedIn").out("isPartOf").hasLabel("place").has("name",textContains(country)).as("countryx"),
        // __.as("postx").out("hasTag").out("hasType").hasLabel("tagclass").has("name",textContains(tagClass)).as("tagTypex")
        // ).select("forumx","postx").toList();

        List<Map<Object, Long>> results = g.V().has("pl_name", country).in("isPartOf").in("isLocatedIn").as("personx")
                .in("hasModerator").as("forumx").out("containerOf").as("postx").out("hasTag").out("hasType")
                .has("tc_name", tagClass).select("forumx", "postx", "personx").map(it -> {
                    HashMap<String, Vertex> hm = (HashMap) it.get();
                    HashMap<String, String> hmNew = new HashMap<>();
                    hmNew.put("personx", hm.get("personx").value("firstName"));
                    hmNew.put("forumx", hm.get("forumx").value("title"));
                    return hmNew;
                }).groupCount().by(select("personx", "forumx")).toList();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        graph.close();
        System.out.println(tagClass + " | " + country);

        long resultCount = 0;
        for (int i = 0; i < results.size(); i++) {
            Map<Object, Long> map = results.get(i);
            for (Map.Entry<Object, Long> entry : map.entrySet()) {
                resultCount += entry.getValue();
            }
        }

        System.out.println("==========================================");
        System.out.println("TotalTime: " + totalTime);
        System.out.println("ResultCount: " + resultCount);
        // System.out.println(results);
        System.out.println("==========================================");

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q4: (" + params + ")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(results);
        return queryResult;
    }
}
