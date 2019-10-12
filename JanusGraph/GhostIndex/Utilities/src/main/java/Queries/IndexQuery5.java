package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;
import static org.janusgraph.core.attribute.Text.textContains;

public class IndexQuery5 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexQuery5.class);

    public static void main(String[] args) throws Exception {

        IndexQuery5 q = new IndexQuery5();

        q.runQuery(null);

        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        String tagClass = "Single";
        String country = "Jordan";
        long startTime = System.currentTimeMillis();

        System.out.println(
        g.V().hasLabel("place")
                .has("name",textContains(country))
                .in("isPartOf")
                .in("isLocatedIn")
                .hasLabel("person").as("personx")
                .in("hasMember").as("forumx")
                .groupCount().by(select("forumx"))

                .unfold()
                .order().by(it -> {
                    return -1 * ((Long) ((Map.Entry)it).getValue());
                })
	            .limit(100)
                .map(it -> ((Map.Entry)it.get()).getKey()).as("popularForumx")
                .out("hasMember").as("personxx")
//                .count().next()
                .in("hasCreator").limit(100)

//                .where( in("containerOf").where(P.within()))

//                .out("hasMember").as("personxx")
//                .in("hasCreator").limit(100)
////                .hasLabel("post")
//                .match(
//                        __.as("postxx").in("containerOf").as("popularForumx")
//                ).select("personxx","popularForumx","postxx")
//                .groupCount().by(it -> ((HashMap)it).get("personxx"))
//	            .unfold()
//            .toList()
        );

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("TotalTime: "+ totalTime);
        System.out.println("==========================================");

        graph.close();
        return null;
    }

}

