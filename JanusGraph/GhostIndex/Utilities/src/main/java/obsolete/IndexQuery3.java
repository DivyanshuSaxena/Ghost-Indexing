package Queries;

import main.IndexRangeQuery;
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
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class IndexQuery3 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexQuery3.class);

    public static void main(String[] args) throws Exception {
        IndexQuery3 query = new IndexQuery3();
        query.runQuery(null);
        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        int month1 = 1;
        int year1 = 2010;
        if(params != null && params.size() > 0){
            month1 = Integer.parseInt(params.get(1));
            year1 = Integer.parseInt(params.get(0));
        }

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        String indexName = "post_creationDate_index";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        int month2 = month1+1;
        int year2 = year1;
        if(month1 == 12){
            month2 = 1;
            year2 = year1+1;
        }

        //dd.mm.yyyy
        String dateMin1 = "01."+month1+"."+year1;
        if(month1 < 10){
            dateMin1 = "01.0"+month1+"."+year1;
        }
        String dateMax1 = "01."+month2+"."+year2;
        if(month2 < 10){
            dateMax1 = "01.0"+month2+"."+year2;
        }

        String dateMin2 = "01."+month2+"."+year2;
        String dateMax2 = "01."+(month2+1)+"."+(year2);
        if(month2 < 9){
            dateMin2 = "01.0"+month2+"."+year2;
            dateMax2 = "01.0"+(month2+1)+"."+(year2);
        }else if(month2 == 9){
            dateMin2 = "01.0"+month2+"."+year2;
        }else if(month2 == 12){
            dateMax2 = "01.0"+1+"."+(year2+1);
        }

        System.out.println(dateMin1 + " to " + dateMax1);
        System.out.println(dateMin2 + " to " + dateMax2);

        long startTime = System.currentTimeMillis();

        IndexRangeQuery irq1 = new IndexRangeQuery();
        ArrayList<Vertex> vertices1 = irq1.searchRange(g, dateFormat.parse(dateMin1), dateFormat.parse(dateMax1), indexName);
        IndexRangeQuery irq2 = new IndexRangeQuery();
        ArrayList<Vertex> vertices2 = irq2.searchRange(g, dateFormat.parse(dateMin2), dateFormat.parse(dateMax2), indexName);

        long endTime1 = System.currentTimeMillis();
        long totalTime1 = endTime1 - startTime;

        List<Map<Object, Long>> resultSet1 = g.V(vertices1).hasLabel("post")                        //.hasLabel("post", "comment")
                .as("messagex")
                .out("hasTag")
                .as("tagx")
                .groupCount()
                .by(select("tagx"))
                .toList();


        List<Map<Object, Long>> resultSet2 = g.V(vertices2).hasLabel("post")                        //.hasLabel("post", "comment")
                .as("messagex")
                .out("hasTag")
                .as("tagx")
                .groupCount()
                .by(select("tagx"))
                .toList();

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();

        List<Map<Object, Long>> result = resultSet1;
        result.addAll(resultSet2);
        long resultCount = 0;
        for(int i=0;i<result.size();i++){
            Map<Object, Long> map = result.get(i);
            for (Map.Entry<Object, Long> entry : map.entrySet()) {
                resultCount += entry.getValue();
            }
        }



        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);
        System.out.println("TotalTime1: "+ totalTime1);

//        for (Map map : resultSet1) {
//            System.out.println("map: " + map);
//            System.out.println("map: " + map.keySet().size());
//        }
//        for (Map map : resultSet2) {
//            System.out.println("map: " + map);
//            System.out.println("map: " + map.keySet().size());
//        }

        System.out.println("Count: " + resultCount);
        System.out.println("==========================================");


        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("IQ3: ("+params+")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToTraverseIndex(totalTime1);
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);
        return queryResult;
    }
}

