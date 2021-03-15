package Queries;

import main.IndexSearchQuery;
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
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.group;

public class IndexQuery2 extends Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexQuery2.class);

    public static void main(String[] args) throws Exception {
        IndexQuery2 query = new IndexQuery2();
        query.runQuery(null);
        System.exit(0);

    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        String country1 = "";
        String country2 = "";
        Long date1 = 0L; //"2008-00-00T00:00:00.000+0000";
        Long date2 = 0L; //"2010-03-02T20:16:15.144+0000";

        if (params == null || params.size() == 0){
            country1 = "Switzerland";
            country2 = "United_States";
            date1 = 1262284200000L;
            date2 = 1289154600000L;
        }else{
            country1 = params.get(2);
            country2 = params.get(3);
            date1 = Long.parseLong(params.get(0));
            date2 = Long.parseLong(params.get(1));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("E MMM d HH:mm:ss z yyyy");
        Date dateVar1 = new Date(date1);
        Date dateVar2 = new Date(date2);

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();

        String indexName = "place_name_index"; //************

        long startTime = System.currentTimeMillis();

        IndexSearchQuery irq = new IndexSearchQuery(g, indexName);
        Vertex c1 = irq.searchKey(g, country1);
        Vertex c2 = irq.searchKey(g, country2);


//        System.out.println("Searched vertices generated ****");
        long endTime1   = System.currentTimeMillis();
        long totalTime1 = endTime1 - startTime;
         /*
        * FIXME: Create index on creationDate of comments and include those vertices above in the vertex list
        * */
        List<Map<Object, Object>> result =  g.V(c1,c2)
                .as("countryx")
                .in("isPartOf")
                .in("isLocatedIn").hasLabel("person")
                .as("personx")
                .in("hasCreator").hasLabel("comment","post")
                .filter(__.or(__.has("po_creationDate", P.gte(dateVar1)).has("po_creationDate", P.lte(dateVar2)),
                        __.has("c_creationDate", P.gt(dateVar1)).has("c_creationDate", P.lte(dateVar2))))
//                .has("creationDate", P.gte(dateVar1)).has("creationDate", P.lte(dateVar2))
                .as("messagex").out("hasTag").hasLabel("tag").as("tagx")
                .select("personx","messagex","tagx","countryx")
                .group().by(it -> ((Vertex)((HashMap)it).get("countryx")).value("pl_name"))
                .by(
                        group().by(it -> ((Vertex)((HashMap)it).get("messagex")).label().equals("post") ?
                                ((Date) ((Vertex) ((HashMap) it).get("messagex")).value("po_creationDate")).getMonth() :
                                ((Date) ((Vertex) ((HashMap) it).get("messagex")).value("c_creationDate")).getMonth()
                        )
                                .by(group().by(it -> ((Vertex)((HashMap)it).get("personx")).value("gender"))
                                        .by(group().by(it -> {
                                                    String birthday = ((Vertex)((HashMap)it).get("personx")).value("birthday").toString();
                                                    try {
                                                        long years = Math.abs(dateFormat2.parse(birthday).getYear() -  (new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2013").getYear()));
                                                        return Math.floor( years / (5));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    return -1;
                                                })
                                                        .by(group().by(it -> ((Vertex)((HashMap)it).get("tagx")).value("t_name")))
                                        )
                                )
                )
                .toList();


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("==========================================");

//        long resultCount = 10;
        long resultCount = g.V(c1,c2)
                .as("countryx")
                .in("isPartOf")
                .in("isLocatedIn").hasLabel("person")
                .as("personx")
                .in("hasCreator").hasLabel("comment","post")
                .filter(__.or(__.has("po_creationDate", P.gte(dateVar1)).has("po_creationDate", P.lte(dateVar2)),
                        __.has("c_creationDate", P.gt(dateVar1)).has("c_creationDate", P.lte(dateVar2))))
//                .has("creationDate", P.gte(dateVar1)).has("creationDate", P.lte(dateVar2))
                .as("messagex").out("hasTag").hasLabel("tag").as("tagx")
                .select("personx","messagex","tagx","countryx").count().next();


        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);
        System.out.println("TotalTime1: "+ totalTime1);

        System.out.println("Count: " + resultCount + " " + result);
        System.out.println("==========================================");

        graph.close();

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("IQ2: ("+params+")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);
        return queryResult;
    }

}

