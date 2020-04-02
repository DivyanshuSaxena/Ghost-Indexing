package Queries;

import main.BPlusIndexRangeQuery;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.group;

public class BPIndexQuery1 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(BPIndexQuery1.class);

    public static void main(String[] args) throws Exception {
        BPIndexQuery1 query = new BPIndexQuery1();
        query.confFile = "local";
        query.runQuery(null);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        long date = 0;
        int distributed = 0;
        if(params == null || params.size() == 0){
            date = 1332354600000L;
        }else{
            date = Long.parseLong(params.get(0));
            distributed = Integer.parseInt(params.get(1));
        }

        System.out.println(new Date(date));
        if (distributed == 1) {
            System.out.println("DISTRIBUTED SETTING");
            graph = JanusGraphFactory.build().set("storage.backend", "cassandrathrift")
                    .set("storage.hostname", "10.17.5.53").set("storage.cassandra.thrift.frame_size_mb", "60")
                    .set("index.search.backend", "elasticsearch").set("index.search.hostname", "10.17.5.53:9210").open();
        } else {
            graph = JanusGraphFactory.open("conf/" + confFile + "/janusgraph-cassandra-es.properties");
        }
        GraphTraversalSource g = graph.traversal();

        String indexName = "post_creationDate_index_bPlus_2000";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        String initDateVar = "2007-00-00T00:00:00.000+0000";
        Date initDate = dateFormat.parse(initDateVar);

        long startTime = System.currentTimeMillis();

        BPlusIndexRangeQuery irq = new BPlusIndexRangeQuery(g, indexName);
//        ArrayList<Vertex> vertices = irq.searchRange(g, dateFormat.parse(initDateVar), dateFormat.parse(date), indexName);
        ArrayList<Vertex> vertices = irq.searchRange(initDate, new Date(date));


//        System.out.println("IDs generated : " + vertices.size());

        long endTime1 = System.currentTimeMillis();
        long totalTime1 = endTime1 - startTime;
         /*
        * FIXME: Create index on creationDate of comments and include those vertices above in the vertex list
        * */

//
        List<Map<Object, Object>> result = null;
        if(vertices.size() > 0) {
            result = g.V(vertices) //.hasLabel("post")
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
        }else{
            result = new ArrayList<>();
        }

//        for(Map map:result){
//            //System.out.println("map: "+map);
//            Object[] keys = map.keySet().toArray();
//            for(int i=0;i<keys.length;i++) {
//
//                System.out.println(keys[i]);
//                System.out.println(map.get(keys[i]));
//                Map<Object, Object> yearMap = (Map<Object, Object>) map.get(keys[i]);
//                Map<Object, Object> postMap = (Map<Object, Object>) yearMap.get("post");
//                for(int j=0;j<=4;j++) {
//                    System.out.println(j + ": " + postMap.get(j));
//                }
//            }
//        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        long resultCount = vertices.size();


        System.out.println("TotalTime: " + totalTime);
        System.out.println("DataSize: " + result.get(0).size());

//        for (Map map : result) {
//            System.out.println("map: " + map);
//        }
        System.out.println("==========================================");
        graph.close();

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("IQ1: ("+params+")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToRun(totalTime);
        queryResult.setTimeToTraverseIndex(totalTime1);
//        queryResult.setResults(result);
        return queryResult;

    }

}

