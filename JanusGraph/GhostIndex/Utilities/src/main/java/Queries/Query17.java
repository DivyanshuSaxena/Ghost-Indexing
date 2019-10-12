package Queries;

//import main.ParamsCSVReader;
import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

public class Query17 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query17.class);

    public static void main(String[] args) throws Exception {
        Query17 query = new Query17();
        query.confFile = "local";
        query.runQuery(null);
        System.exit(0);
    }


    public QueryResult runQuery(List<String> params) throws ParseException {

        String country = "India";

        if (params != null && params.size() > 0){
            country = params.get(0);
        }
        System.out.println("Country: "+country);

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        long startTime = System.currentTimeMillis();
/*********************************************************************************************************
* */

        List<HashSet> result = g.V().has("pl_name",country).in("isPartOf").in("isLocatedIn")
                    .hasLabel("person").match(__.as("a").out("knows").as("b"),
                        __.as("b").out("isLocatedIn").out("isPartOf").has("pl_name",country),
                        __.as("b").out("knows").as("c"),
                        __.as("c").out("isLocatedIn").out("isPartOf").has("pl_name",country),
                        __.as("c").out("knows").as("a")).select("a","b","c").by("p_id")
                        .map(it -> {
                            return new HashSet(it.get().values());
                        } ).dedup().toList();

        

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);

        System.out.println("Result: "+result);
//        System.out.println("Result: "+result.size());

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q12: ("+params+")");
//        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);

        return queryResult;
    }

}

