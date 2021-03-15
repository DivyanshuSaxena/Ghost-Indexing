package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.group;

public class Query2 extends Query {
    private static final Logger LOGGER = LoggerFactory.getLogger(Query2.class);

    public static void main(String[] args) throws Exception {
        Query2 query = new Query2();
        query.confFile = "local";
        query.runQuery(null);
        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        String country1 = "";
        String country2 = "";
        Long date1 = 0L; // "2008-00-00T00:00:00.000+0000";
        Long date2 = 0L; // "2010-03-02T20:16:15.144+0000";
        int distributed = 0;

        if (params == null || params.size() == 0) {
            country1 = "Aden";
            country2 = "Jordan";
            date1 = 1262284200000L;
            date2 = 1289154600000L;
            distributed = 0;
        } else {
            country1 = params.get(2);
            country2 = params.get(3);
            date1 = Long.parseLong(params.get(0));
            date2 = Long.parseLong(params.get(1));
            distributed = Integer.parseInt(params.get(2));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("E MMM d HH:mm:ss z yyyy");
        Date dateVar1 = new Date(date1);
        Date dateVar2 = new Date(date2);

        System.out.println("date1: " + dateVar1);
        System.out.println("date2: " + dateVar2);

        if (distributed == 1) {
            graph = JanusGraphFactory.build().set("storage.backend", "cassandrathrift")
                    .set("storage.hostname", "10.17.5.53")
                    .set("storage.cassandra.frame-size-mb", 60)
                    .set("index.search.backend", "elasticsearch")
                    .set("index.search.hostname", "10.17.5.53:9210").open();
        } else {
            graph = JanusGraphFactory.open("conf/" + confFile + "/janusgraph-cassandra-es.properties");
        }
        GraphTraversalSource g = graph.traversal();

        long startTime = System.currentTimeMillis();

        /*
         * FIXME: Checking running and correctness of query2 - not yet complete.
         * grouping left
         */
        // Vertex c1 = g.V().has("pl_id", P.gte(0)).has("name",
        // textContains(country1)).next();
        // Vertex c2 = g.V().has("pl_id", P.gte(0)).has("name",
        // textContains(country2)).next();

        Vertex c1 = g.V().has("pl_name", country1).next();// .has("name", textContains(country1)).next();
        Vertex c2 = g.V().has("pl_name", country2).next();// .has("name", textContains(country2)).next();

        System.out.println("country: " + c1 + " " + c2);
        List<Map<Object, Object>> result = g.V(c1, c2).as("countryx").in("isPartOf").in("isLocatedIn")
                .hasLabel("person").as("personx").in("hasCreator").hasLabel("comment", "post")
                .filter(__.or(__.has("po_creationDate", P.gte(dateVar1)).has("po_creationDate", P.lte(dateVar2)),
                        __.has("c_creationDate", P.gt(dateVar1)).has("c_creationDate", P.lte(dateVar2))))
                // .has("creationDate", P.gte(dateVar1)).has("creationDate", P.lte(dateVar2))
                .as("messagex").out("hasTag").hasLabel("tag").as("tagx")
                .select("personx", "messagex", "tagx", "countryx").group()
                .by(it -> ((Vertex) ((HashMap) it).get("countryx")).value("pl_name"))
                .by(group()
                        .by(it -> ((Vertex) ((HashMap) it).get("messagex")).label().equals("post")
                                ? ((Date) ((Vertex) ((HashMap) it).get("messagex")).value("po_creationDate")).getMonth()
                                : ((Date) ((Vertex) ((HashMap) it).get("messagex")).value("c_creationDate")).getMonth())
                        .by(group().by(it -> ((Vertex) ((HashMap) it).get("personx")).value("gender"))
                                .by(group().by(it -> {
                                    String birthday = ((Vertex) ((HashMap) it).get("personx")).value("birthday")
                                            .toString();
                                    try {
                                        long years = Math.abs(dateFormat2.parse(birthday).getYear()
                                                - (new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2013").getYear()));
                                        return Math.floor(years / (5));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    return -1;
                                }).by(group().by(it -> ((Vertex) ((HashMap) it).get("tagx")).value("t_name"))))))
                .toList();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        long resultCount = g.V(c1, c2).as("countryx").in("isPartOf").in("isLocatedIn").hasLabel("person").as("personx")
                .in("hasCreator").hasLabel("comment", "post")
                .filter(__.or(__.has("po_creationDate", P.gte(dateVar1)).has("po_creationDate", P.lte(dateVar2)),
                        __.has("c_creationDate", P.gt(dateVar1)).has("c_creationDate", P.lte(dateVar2))))
                // .has("creationDate", P.gte(dateVar1)).has("creationDate", P.lte(dateVar2))
                .as("messagex").out("hasTag").hasLabel("tag").as("tagx")
                .select("personx", "messagex", "tagx", "countryx").count().next();

        System.out.println("==========================================");
        System.out.println("TotalTime: " + totalTime);
        System.out.println("Count: " + resultCount);
        System.out.println("==========================================");

        graph.close();

        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q2: (" + params + ")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);
        return queryResult;
    }
}
