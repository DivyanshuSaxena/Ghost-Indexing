package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

public class Query14 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query14.class);

    public static void main(String[] args) throws Exception {
        Query14 query = new Query14();
        query.runQuery(null);
        System.exit(0);
    }


    public QueryResult runQuery(List<String> params) throws ParseException {

        long date1 = 1262304000000L;
        long date2 = 1264982400000L;

        if (params != null && params.size() > 0){
            date1 = Long.parseLong(params.get(0));
            date2 = Long.parseLong(params.get(1));
        }
        System.out.println("date1: "+date1);
        System.out.println("date2: "+date2);

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        long startTime = System.currentTimeMillis();
/*********************************************************************************************************
* */
        List<Traverser<Object>> result =
                g.V( ).has("po_creationDate", P.gte( new Date(date1)) )
                                    .has("po_creationDate", P.lte( new Date(date2)) ).as("postx")
                        .emit().repeat(__.in("replyOf").has("c_creationDate", P.gte(new Date(date1)))
                        .has("c_creationDate", P.lte(new Date(date2)))
                        .as("messagex") ).until(__.in("replyOf").count().is(0)).select("postx")
                        .out("hasCreator").as("personx").select("postx","personx", "messagex")
                        .group().by(it -> ((HashMap)it).get("personx")).by(groupCount().by(it -> ((HashMap)it).get("postx"))).unfold()
                        .map(it -> {
                            System.out.println(((Map.Entry)it.get()).getClass());
                            System.out.println( ((HashMap) ((Map.Entry)it.get()).getValue()).values().toArray()[0].getClass());

                            long numPosts = ((HashMap) ((Map.Entry)it.get()).getValue()).values().toArray().length;
                            long numMessages=0;
                            for (Object value : ((HashMap) ((Map.Entry)it.get()).getValue()).values()) {
                                numMessages += (Long)value;
                            }
                            System.out.println(numMessages);
                            HashMap<String, Long> res = new HashMap<>();
                            res.put("numPosts", numPosts);
                            res.put("numMessages", numMessages);
                            ((Map.Entry)it.get()).setValue(res);
                            return it;
                        })
                        .toList();

//////////////////////////////////////////TEST QUERY BELOW//////////////////////////////
//                g.V( 84721696,83660800, 103870520 ).as("postx")
//                        .emit().repeat(__.in("replyOf").has("c_creationDate", P.lte(new Date(111111111111111111L)))
//                        .as("messagex") ).until(__.in("replyOf").count().is(0)).select("postx")
//                        .out("hasCreator").as("personx").select("postx","personx", "messagex")
//                        .group().by(it -> ((HashMap)it).get("personx")).by(groupCount().by(it -> ((HashMap)it).get("postx"))).unfold()
//                        .map(it -> {
//                            System.out.println(((Map.Entry)it.get()).getClass());
//                            System.out.println( ((HashMap) ((Map.Entry)it.get()).getValue()).values().toArray()[0].getClass());
//
//                            long numPosts = ((HashMap) ((Map.Entry)it.get()).getValue()).values().toArray().length;
//                            long numMessages=0;
//                            for (Object value : ((HashMap) ((Map.Entry)it.get()).getValue()).values()) {
//                                numMessages += (Long)value;
//                            }
//                            System.out.println(numMessages);
//                            HashMap<String, Long> res = new HashMap<>();
//                            res.put("numPosts", numPosts);
//                            res.put("numMessages", numMessages);
//                            ((Map.Entry)it.get()).setValue(res);
//                            return it;
//                        })
//                        .toList();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);

        System.out.println("Result: "+result);
//        System.out.println("Result: "+result.size());

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q14: ("+params+")");
//        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);

        return queryResult;
    }

}

