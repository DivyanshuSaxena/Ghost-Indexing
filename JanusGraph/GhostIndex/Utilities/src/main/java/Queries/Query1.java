package Queries;

import main.PerformanceResult;
import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.group;

public class Query1 extends Query {
    private static final Logger LOGGER = LoggerFactory.getLogger(Query1.class);

    public static void main(String[] args) throws Exception {
        Query1 q = new Query1();
        q.confFile = "local";
        q.runQuery(null);

        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        long date = 0;
        int distributed = 0;
        if (params == null || params.size() == 0) {
            // date = 1316025000000L;
            date = 1332354600000L;
            distributed = 0;
        } else {
            date = Long.parseLong(params.get(0));
            distributed = Integer.parseInt(params.get(1));
        }

    	if (distributed == 1) {
	        System.out.println("DISTRIBUTED SETTING");
            graph = JanusGraphFactory.build().set("storage.backend", "cassandrathrift")
                    .set("storage.hostname", "10.17.5.53")
                    .set("storage.cassandra.thrift.frame_size_mb", 60)
                    .set("storage.cassandra.thrift.max_message_size_mb", 61)
                    .set("index.search.backend", "elasticsearch")
                    .set("index.search.hostname", "10.17.5.53:9210").open();
        } else {
            graph = JanusGraphFactory.open("conf/" + confFile + "/janusgraph-cassandra-es.properties");
        }
        GraphTraversalSource g = graph.traversal();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        // Date dateVar = new Date(date);//dateFormat.parse(date);
        // System.out.println("Date: " + dateVar);

        long startTime = System.currentTimeMillis();
        /*
         * FIXME: Create index on creationDate of comments and include that in query //
         * *
         */
        List<Map<Object, Object>> result = g.V() // .hasLabel("post", "comment")
                .has("po_creationDate", P.lt(date))// .toList();
                .group().by(it -> {
                    long value = ((Vertex) it).value("po_creationDate");
                    Date creationDate = new Date(value);
                    return creationDate.getYear() + 1900;
                }).by(group().by(T.label).by(group().by(it -> {
                    int len = ((Vertex) it).value("po_length");
                    if (len < 40) {
                        return "short";
                    } else if (len < 80) {
                        return "one liner";
                    } else if (len < 160) {
                        return "tweet";
                    } else {
                        return "long";
                    }
                }))).toList();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Long resultCount = g.V() //.hasLabel("post", "comment")
        // .has("po_creationDate", P.lt(dateVar)).count().next();
        // System.out.println("Count: "+resultCount);

        System.out.println("====================" + date + "======================");
        System.out.println("DataSize: " + result.get(0).size());
        System.out.println("TotalTime: " + totalTime);

        // long resultCount = g.V().hasLabel("post") //.hasLabel("post", "comment")
        // .has("po_creationDate", P.lte(dateVar)).count().next();

        // System.out.println("Count: " + result);
        // for (Map map : result) {
        // System.out.println("map: " + map);
        // }
        System.out.println("==========================================");
        graph.close();

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q1: (" + date + ")");
        queryResult.setResultCount(result.get(0).size());
        queryResult.setTimeToRun(totalTime);
        queryResult.setTimeToTraverseIndex(-1);
        // queryResult.setResults(result);
        return queryResult;
    }
}
