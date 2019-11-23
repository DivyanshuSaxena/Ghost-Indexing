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
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.group;

public class Query1Partial extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query1Partial.class);

    public static void main(String[] args) throws Exception {
        Query1Partial q = new Query1Partial();
        q.runQuery(null);

        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        long date = 0;
        if(params == null || params.size() == 0){
            date = 1316025000000L;
        }else{
            date = Long.parseLong(params.get(0));
        }

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        Date dateVar = new Date(date);//dateFormat.parse(date);
        System.out.println("Date: " + dateVar);

        long startTime = System.currentTimeMillis();

        List<Vertex> result = g.V()//.hasLabel("post")       //.hasLabel("post", "comment")
                .has("po_creationDate", P.lt(dateVar))
                .toList();


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        long resultCount = result.size();
        System.out.println("Count: "+resultCount);


        System.out.println("===================="+date+"======================");
        System.out.println("TotalTime: " + totalTime);
        System.out.println("==========================================");
        graph.close();


        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q1: ("+date+")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);
        return queryResult;
    }
}



