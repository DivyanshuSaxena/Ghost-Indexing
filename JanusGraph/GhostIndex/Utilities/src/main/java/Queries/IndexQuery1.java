package Queries;

import main.IndexRangeQuery;
import main.IndexRangeQueryOrg;
import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.barrier;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.group;

public class IndexQuery1 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexQuery1.class);

    public static void main(String[] args) throws Exception {
        IndexQuery1 query = new IndexQuery1();
        query.confFile = "local";
        query.runQuery(null);

    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        long date = 0;
        if(params == null || params.size() == 0){
            date = 1271519200368L;
        }else{
            date = Long.parseLong(params.get(0));
        }

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        System.out.println("init date: "+new Date(date));
        String indexName = "post_creationDate_index_bPlus_100";
        long initDateVar = 1171373841000L;

        //Timing start
        long startTime = System.currentTimeMillis();

        //Use index to retrieve the vertices
        IndexRangeQueryOrg irq = new IndexRangeQueryOrg();
        ArrayList<Vertex> vertices = irq.searchRange(g, new Date(initDateVar), new Date(date), indexName);

//        g.V().by
//        System.out.println("used");
        /////debug id print
//        ArrayList<Long> vertexIds = new ArrayList<Long>();
//        for (int i = 0; i < vertices.size(); i++) {
//            vertexIds.add((Long) vertices.get(i).id());//.value("po_creationDate")).getTime());
//        }
//        Collections.sort(vertexIds);
//        for (int i = 0; i < vertexIds.size(); i++) {
//            System.out.println(vertexIds.get(i));
////            vertexIds.add((Long) ((Date)vertices.get(i).value("po_creationDate")).getTime());
//        }

//        System.out.println("IDs generated : " + vertexIds.size());
        long resultCount = vertices.size();

        long endTime1 = System.currentTimeMillis();
        long totalTime1 = endTime1 - startTime;

        /*
        * FIXME: Create index on creationDate of comments and include those vertices above in the vertex list
        * */

//        List<Map<Object, Object>> result = g.V(vertices)//.hasLabel("post")
//                .group().by(it -> {
//                    return ((Date) ((Vertex) it).value("po_creationDate")).getYear() + 1900;
//                }).by(group().by(T.label).by(group().by(it -> {
//                    int len = ((Vertex) it).value("po_length");
//                    if (len < 40) {
//                        return "short";
//                    } else if (len < 80) {
//                        return "one liner";
//                    } else if (len < 160) {
//                        return "tweet";
//                    } else {
//                        return "long";
//                    }
//                }))).toList();


        /////// DEBUG PRINT
//        for(Map map:result){
//            //System.out.println("map: "+map);
//            Object[] keys = map.keySet().toArray();
//            for(int i=0;i<keys.length;i++) {
//
//                System.out.println(keys[i]);
////                System.out.println(map.get(keys[i]));
//                Map<Object, Object> yearMap = (Map<Object, Object>) map.get(keys[i]);
//                Map<Object, Object> postMap = (Map<Object, Object>) yearMap.get("post");
//                for(int j=0;j<=4;j++) {
//                    System.out.println(j + ": " + postMap.get(j));
//                }
//            }
//        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;


        System.out.println("TotalTime: " + totalTime);
        System.out.println("TotalTime1: " + totalTime1);

        System.out.println("Count: "+resultCount);
//        for (Map map : result) {
//            System.out.println("map: " + map);
//        }
        System.out.println("==========================================");
        graph.close();

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("IndexQ1: ("+params+")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToRun(totalTime);
        queryResult.setTimeToTraverseIndex(totalTime1);
//        queryResult.setResults(result);
        return queryResult;

    }

}

