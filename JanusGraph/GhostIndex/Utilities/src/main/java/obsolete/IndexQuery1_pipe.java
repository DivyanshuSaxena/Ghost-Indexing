package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.group;

public class IndexQuery1_pipe extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexQuery1_pipe.class);

    public static void main(String[] args) throws Exception {
        IndexQuery1_pipe query = new IndexQuery1_pipe();
        query.confFile = "baadal";
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
        String indexName = "post_creationDate_index_bPlus_1000_vcentric";
        long initDateVar = 1171373841000L;

        Date s_date = new Date(initDateVar);
        Date e_date = new Date(date);
        
        //Timing start
        long startTime = System.currentTimeMillis();


        Long result = g.V().has("index_id", -1).out().has("name",indexName)           //index root
                .repeat(
                        __.outE("INDEX_EDGE").not(__.has("min", P.gte(e_date) ) )
                                .not(__.has("max", P.lte(s_date))).inV()
                )
                .until(__.outE("INDEX_EDGE").count().is(0))
                .outE("INDEX_DATA_EDGE").has("val", P.gte(s_date)).has("val", P.lte(e_date)).inV()
                .count().next();

//                .count()
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


//                .count()

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


//        long resultCount = g.V().has("index_id", -1).out().has("name",indexName)           //index root
//                .repeat(
//                        __.outE("INDEX_EDGE").not(__.has("min", P.gte(e_date) ) )
//                                .not(__.has("max", P.lte(s_date))).inV()
//                )
//                .until(__.outE("INDEX_EDGE").count().is(0))
//                .outE("INDEX_DATA_EDGE").has("val", P.gte(s_date)).has("val", P.lte(e_date)).inV()
//                .count().next();

        System.out.println("TotalTime: " + totalTime);
        System.out.println("==========================================");
        graph.close();

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("IndexQ1: ("+params+")");
        queryResult.setResultCount(result);
//        queryResult.setTimeToRun(totalTime);
        queryResult.setTimeToTraverseIndex(totalTime);
        queryResult.setResults(result);
        return queryResult;

    }

}

