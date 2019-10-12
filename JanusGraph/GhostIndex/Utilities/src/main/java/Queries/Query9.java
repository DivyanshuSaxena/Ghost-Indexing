package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

public class Query9 extends Query {
    private static final Logger LOGGER = LoggerFactory.getLogger(Query9.class);

    public static void main(String[] args) throws Exception {
        Query9 query = new Query9();
        query.confFile = "local";
        query.runQuery(null);
        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        String tagClass1 = "BaseballPlayer";
        String tagClass2 = "MilitaryPerson";
        long threshold = 200;
        if(params != null && params.size() > 0){
            tagClass1 = params.get(0);
            tagClass2 = params.get(1);
            threshold = Long.parseLong(params.get(2));
        }

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        long startTime = System.currentTimeMillis();

        List<Map<Object, Long>> result = g.V().has("tc_name", tagClass1).in("hasTag").hasLabel("tag")
                .in("hasTag").hasLabel("post").as("post1x").in("containerOf").filter(out("hasMember").count().is(P.gt(threshold)))
                .as("forumx").select("post1x", "forumx").groupCount().by(it-> ((HashMap) it).get("forumx"))
                    .toList();


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        graph.close();

        long resultCount = result.size();

        System.out.println("result:"+result);

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);
//        for (Map map : result) {
//            System.out.println("map: " + map);
//            System.out.println("map: " + map.keySet().size());
//        }

        System.out.println("Count: " + resultCount);
        System.out.println("==========================================");


        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q6: ("+params+")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);
        return queryResult;
    }

}

