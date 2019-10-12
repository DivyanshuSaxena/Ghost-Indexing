package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.groupCount;

public class Query13 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query13.class);

    public static void main(String[] args) throws Exception {
        Query13 query = new Query13();
        query.runQuery(null);
        System.exit(0);
    }


    public QueryResult runQuery(List<String> params) throws ParseException {

        String country = "Jordan";

        if (params != null && params.size() > 0){
            country = params.get(0);
        }
        System.out.println("country: "+country);

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        long startTime = System.currentTimeMillis();
/*********************************************************************************************************
* */
        List<Map<Object, Object>> result =
                g.V().has("pl_name", "Jordan").in("isLocatedIn").hasLabel("post","comment").as("messagex")
                        .out("hasTag").as("tagx").select("messagex","tagx").group()
                        .by(it -> {
//                            System.out.println(((HashMap)it).get("messagex").getClass());
                            Vertex message = (Vertex) ((HashMap)it).get("messagex");
                            Date creationDate;
                            if(message.label().equals("post")){
                                creationDate = message.value("po_creationDate");
                            }else if(message.label() == "comment"){
                                creationDate = message.value("c_creationDate");
                            }else{
                                creationDate = new Date();
                            }
                            ((HashMap) it).put("Year", creationDate.getYear() + 1900);
                            ((HashMap) it).put("Month", creationDate.getMonth());
                            return (creationDate.getYear()+1900)+"-"+creationDate.getMonth();
                        })
                        .toList();


////////////////////////GREMLIN QUERY BELOW//////////////////////////
//        g.V().has("pl_name", "Jordan").in("isLocatedIn").hasLabel("post","comment").as("messagex").out("hasTag").as("tagx").select("messagex","tagx").group().by{Vertex message = (Vertex) ((HashMap)it).get("messagex");Date creationDate;if(message.label().equals("post")){creationDate = message.value("po_creationDate");}else if(message.label() == "comment"){creationDate = message.value("c_creationDate");}else{creationDate = new Date();};     it.put("Year", creationDate.getYear() + 1900);it.put("Month", creationDate.getMonth());return (creationDate.getYear()+1900)+"-"+creationDate.getMonth();}.map{it.get().values()}.unfold()


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

