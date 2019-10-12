package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.B_O_Traverser;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.in;

public class Query23 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query23.class);

    public static void main(String[] args) throws Exception {
        Query23 query = new Query23();
        query.confFile = "local";
        query.runQuery(null);
        System.exit(0);
    }


    public QueryResult runQuery(List<String> params) throws ParseException {

        String home = "Jordan";

        if (params != null && params.size() > 0){
            home = params.get(0);
        }

        System.out.println("home: "+home);

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        long startTime = System.currentTimeMillis();
/*********************************************************************************************************
 * */

        List<Map<Object, Object>> result = g.V().has("pl_name", home)
                .in("isPartOf")
                .in("isLocatedIn")
                .in("hasCreator")
                .filter(__.out("isLocatedIn").has("pl_name", P.neq(home)))
                .group()
                .by(it -> {
                    //ERROR HERE **********
                    return ((Date) ((Vertex) it).value("po_creationDate")).getMonth() + 1900;
                })
                .by(__.out("isLocatedIn")).toList();


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);

        System.out.println("Result: "+result);
        System.out.println("Result: "+result.size());

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q23: ("+params+")");
        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);

        return queryResult;
    }

}
