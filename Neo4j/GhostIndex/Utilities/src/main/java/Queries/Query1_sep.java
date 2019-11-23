package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.group;

public class Query1_sep extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(Query1_sep.class);

    public static void main(String[] args) throws Exception {
        Query1_sep q = new Query1_sep();
        q.confFile = "local";
        q.runQuery(null);

        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        long date = 0;
        if(params == null || params.size() == 0){
            date = 1316025000000L;
        }else{
            date = Long.parseLong(params.get(0));
        }


        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        Date dateVar = new Date(date);//dateFormat.parse(date);
        System.out.println("Date: " + dateVar);

        long startTime = System.currentTimeMillis();
        /*
        * FIXME: Create index on creationDate of comments and include that in query
//        * */
        List<Vertex> vertices_mid = g.V()//.hasLabel("post")       //.hasLabel("post", "comment")
                .has("po_creationDate", P.lt(dateVar)).toList();
        List<Map<Object, Object>> result = g.V(vertices_mid)
                .group().by(it -> {
                    return ((Date) ((Vertex) it).value("po_creationDate")).getYear() + 1900;
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


        long endTime  = System.currentTimeMillis();
        long totalTime = endTime - startTime;

//        Long resultCount = g.V()                        //.hasLabel("post", "comment")
//                .has("po_creationDate", P.lt(dateVar)).count().next();
//        System.out.println("Count: "+resultCount);




        System.out.println("===================="+date+"======================");
        System.out.println("TotalTime: " + totalTime);

//        long resultCount = g.V().hasLabel("post")                        //.hasLabel("post", "comment")
//                        .has("po_creationDate", P.lte(dateVar)).count().next();

//        System.out.println("Count: " + result);
//            for (Map map : result) {
//                System.out.println("map: " + map);
//            }
        System.out.println("==========================================");
        graph.close();

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q1: ("+date+")");
        queryResult.setResultCount(result.size());
        queryResult.setTimeToRun(1);
        queryResult.setTimeToTraverseIndex(totalTime);
//        queryResult.setResults(result);
        return queryResult;
    }
}



