package Queries;

import main.IndexSearchQuery;
import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;
import static org.janusgraph.core.attribute.Text.textContains;

public class IndexQuery4 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexQuery4.class);

    public static void main(String[] args) throws Exception {

        IndexQuery4 q = new IndexQuery4();

        q.runQuery(null);

        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        //Queries: (OfficeHolder, Jordan), (TennisPlayer, Jordan), (Saint, Jordan), (Country, Jordan), (Albumn, Jordan)
        String tagClass = "";
        String country = "";
        String indexName = "place_name_index";

        if(params == null || params.size() <= 1) {
            tagClass = "Monarch";
            country = "China";
        } else {
            tagClass = params.get(0);
            country = params.get(1);
        }

        long startTime = System.currentTimeMillis();

        IndexSearchQuery irq = new IndexSearchQuery(g, indexName);
        Vertex vertex = irq.searchKey(g, country);

        long endTime1   = System.currentTimeMillis();
        long totalTime1 = endTime1 - startTime;

        List<Map<Object, Long>> results =
            g.V(vertex)//.hasLabel("place")
                    .in("isPartOf")
                    .in("isLocatedIn").as("personx")
                    .in("hasModerator").as("forumx")
                    .out("containerOf").as("postx")
                    .out("hasTag").out("hasType").has("name",tagClass)
                    .select("forumx","postx","personx")
                    .map(it -> {
                        HashMap<String,Vertex> hm = (HashMap) it.get();
                        HashMap<String,String> hmNew = new HashMap<>();
                        hm.put("personx", hm.get("personx").value("firstName"));
                        hm.put("forumx", hm.get("forumx").value("title"));
                        return hm;
                    })
                    .groupCount().by(select("personx","forumx"))
                    .toList();



        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();


        long resultCount = 0;
        for (int i = 0; i<results.size();i++){
            Map<Object, Long> map = results.get(i);
            for (Map.Entry<Object, Long> entry : map.entrySet()) {
                resultCount += entry.getValue();
            }
        }

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);
        System.out.println("TotalTime1: "+ totalTime1);
        System.out.println("ResultCount: "+ resultCount);
//        System.out.println(results);
        System.out.println("==========================================");


        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q4: (" + params + ")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToRun(totalTime);
        queryResult.setTimeToTraverseIndex(totalTime1);
        queryResult.setResults(results);
        return queryResult;
    }

}

