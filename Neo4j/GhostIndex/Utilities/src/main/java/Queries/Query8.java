package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.constant;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.is;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

public class Query8 extends Query {
    private static final Logger LOGGER = LoggerFactory.getLogger(Query8.class);

    public static void main(String[] args) throws Exception {
        Query8 query = new Query8();
        query.confFile = "local";
        query.runQuery(null);
        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        String tag = "Horse_Rotorvator";
        if(params != null && params.size() > 0){
            tag = params.get(0);
        }

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        long startTime = System.currentTimeMillis();

        List<Map<Object, Long>> result = g.V().has("t_name", tag).in("hasTag").in("replyOf").as("comments")
                        .not(out("hasTag").has("t_name",tag)).out().values("t_name").groupCount().by()
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

