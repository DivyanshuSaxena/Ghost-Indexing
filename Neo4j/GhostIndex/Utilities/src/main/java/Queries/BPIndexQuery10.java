package Queries;

import main.BPlusIndexRangeQuery;
import main.IndexRangeQuery;
import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BPIndexQuery10 extends Query {
    private static final Logger LOGGER = LoggerFactory.getLogger(BPIndexQuery10.class);

    public static void main(String[] args) throws Exception {
        BPIndexQuery10 q = new BPIndexQuery10();
        q.confFile = "local";
        q.runQuery(null);

        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        String tagName1 = "Josip_Broz_Tito";
        Long date1 = 1258229775144L;

        if(params == null || params.size() == 0){
            tagName1 = "Josip_Broz_Tito";
        }else{
            tagName1 = params.get(0);
            date1 = Long.parseLong(params.get(1));
        }
        Date dateVar1 = new Date(date1);

        System.out.println("tagName1 : " + tagName1);
        System.out.println("date1 : " + date1);

        String indexName = "post_creationDate_index_bPlus_2000";
        long dateMax1 = 1500354600000L;

        long startTime = System.currentTimeMillis();

//        List<Vertex> persons1 = g.V().has("t_name",tagName1).in("hasInterest").toList();
//        Set interestedPersonSet = new HashSet(persons1);
//
//
//        List<Object> person2 =
//                g.V()
//                .has("po_creationDate", P.gt(dateVar1))//.limit(1000)
//                .where(out("hasTag").has("t_name",tagName1))
//                .out("hasCreator").as("personsx")
//                .groupCount().by(select("personsx")).unfold().toList();
//
//        interestedPersonSet.add(((Map.Entry)person2.get(0)).getKey());
//
//        for(Object p:person2){
//            Map.Entry<Vertex,Long> p1 = (Map.Entry<Vertex, Long>) p;
//            if(interestedPersonSet.contains(p1.getKey())) {
//                p1.setValue(p1.getValue() + 100);
//            }
//        }

        long endTime  = System.currentTimeMillis();
        long totalTime = endTime - startTime;


        long startTime1 = System.currentTimeMillis();
//        List<Vertex> person3 = g.V().has("po_creationDate", P.gt(dateVar1)).toList();
        BPlusIndexRangeQuery irq1 = new BPlusIndexRangeQuery(g, indexName);
        ArrayList<Vertex> vertices1 = irq1.searchRange(dateVar1, new Date(dateMax1));

        long endTime2  = System.currentTimeMillis();
        long totalTime2 = endTime2 - startTime1;


        graph.close();
        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q10: ("+dateVar1 + "|" + tagName1 +")");
        queryResult.setResultCount(vertices1.size());
        queryResult.setTimeToTraverseIndex(totalTime2);
        queryResult.setTimeToRun(totalTime);
//        queryResult.setResults(result);
        return queryResult;
    }

}

