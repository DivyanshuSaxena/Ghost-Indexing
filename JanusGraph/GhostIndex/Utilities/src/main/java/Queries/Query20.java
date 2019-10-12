package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
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

public class Query20 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query20.class);

    public static void main(String[] args) throws Exception {
        Query20 query = new Query20();
        query.runQuery(null);
        System.exit(0);
    }


    public QueryResult runQuery(List<String> params) throws ParseException {

        List<String> tagClass = new ArrayList<String>();
        tagClass.add("SoccerManager");
        tagClass.add("Saint");
        tagClass.add("ComicsCreator");

        if (params != null && params.size() > 0){
            tagClass = params;
        }

        System.out.println("tagClass: "+tagClass);

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        long startTime = System.currentTimeMillis();
/*********************************************************************************************************
 * */

        List<Long> result = g.V().has("tc_name", P.within(tagClass))
                .map(it -> { return __.repeat(in("isSubClassOf")).times(4).emit().in("hasType").in("hasTag").count().next()
                        + __.__(((Vertex)((B_O_Traverser)it).get())).in("hasType").in("hasTag").count().next();}).toList();


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);

        System.out.println("Result: "+result);
        System.out.println("Result: "+result.size());

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q20: ("+params+")");
        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);

        return queryResult;
    }

}
