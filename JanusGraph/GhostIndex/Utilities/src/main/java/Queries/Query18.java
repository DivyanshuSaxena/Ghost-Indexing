package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Query18 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query18.class);

    public static void main(String[] args) throws Exception {
        Query18 query = new Query18();
        query.confFile = "local";
        query.runQuery(null);
        System.exit(0);
    }


    public QueryResult runQuery(List<String> params) throws ParseException {

        int lengthThreshold = 100;
        long minCreationDate = 1268273000000L;
        long zeroDate = 0L;
        String[] languages = {"uz"}; //uz, tk, ar

        if (params != null && params.size() > 0){
            minCreationDate = Long.parseLong(params.get(0));
            lengthThreshold = Integer.parseInt(params.get(1));
            languages = params.get(2).split(";");
        }

        Date dateVar = new Date(minCreationDate);
        Date zeroDateVar = new Date(zeroDate);
        System.out.println("lengthThreshold: "+lengthThreshold);
        System.out.println("minCreationDate: "+dateVar);

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        System.out.println("conf: " + confFile);
        GraphTraversalSource g = graph.traversal();


        long startTime = System.currentTimeMillis();
/*********************************************************************************************************
 * */

        List<Map<Object, Long>> result = g.V()//.has("po_creationDate", P.gt(dateVar))
                .has("p_creationDate", P.gt(zeroDateVar)).limit(100).as("a")
//                .out("hasCreator").as("a")
                .map(
                        __.in("hasCreator")
                                .filter(__.or(__.has("po_length", P.gt(0)),
                                        __.has("c_length", P.gt(0))))
                                .filter(__.or(__.has("po_length", P.lt(lengthThreshold)),
                                        __.has("c_length", P.lt(lengthThreshold))))
                                .filter(__.or(__.has("po_creationDate", P.gt(dateVar)),
                                        __.has("c_creationDate", P.gt(dateVar))))
//                                .filter(__.or(__.has("po_creationDate", P.gt(dateVar)),
//                                        __.has("c_creationDate", P.gt(dateVar))))
                                .filter(__.or(__.has("language", P.within(languages)),
                                        __.repeat(__.out("replyOf")).until(__.hasLabel("post")).has("language", P.within(languages))))
//                                .hasLabel("comment")
                                .count()).as("b")
                .select("a", "b").groupCount().by(it -> {
                    return ((Map) it).get("b");
                }).toList();


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        graph.close();

        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);

        System.out.println("Result: "+result);
        System.out.println("Result: "+result.size());

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q12: ("+params+")");
        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);

        return queryResult;
    }

}
