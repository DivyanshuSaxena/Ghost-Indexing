package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.provider.certpath.Vertex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.count;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class Query6 extends Query {
    private static final Logger LOGGER = LoggerFactory.getLogger(Query6.class);

    public static void main(String[] args) throws Exception {
        Query6 query = new Query6();
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

        /*
        * FIXME: INCOMPLETE => Cant understand how to aggregate
        * */

        long startTime = System.currentTimeMillis();

        List<Map<String, Object>> result = g.V().has("t_name", tag).in("hasTag").match(
                __.as("messagex").in("likes").count().as("likeCountx"),
                __.as("messagex").out("hasCreator").as("personx")
                ,__.as("messagex").repeat((Traversal) __.in("replyOf")).emit().until(__.in("replyOf").count().is(0)).count().as("replyCountx")
        ).select("messagex","personx","likeCountx","replyCountx")
        .toList();


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        graph.close();

        long resultCount = 0;
//        for(int i=0;i<result.size();i++){
//            Map<Object, Long> map = result.get(i);
//            for (Map.Entry<Object, Long> entry : map.entrySet()) {
//                resultCount += entry.getValue();
//            }
//        }

        System.out.println("result:"+result);

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);
        for (Map map : result) {
            System.out.println("map: " + map);
            System.out.println("map: " + map.keySet().size());
        }

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

