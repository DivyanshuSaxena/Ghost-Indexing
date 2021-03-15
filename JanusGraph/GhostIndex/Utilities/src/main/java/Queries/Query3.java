package Queries;

import main.QueryResult;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class Query3 extends Query {
    private static final Logger LOGGER = LoggerFactory.getLogger(Query3.class);

    public static void main(String[] args) throws Exception {
        Query3 query = new Query3();
        query.confFile = "local";
        query.runQuery(null);
        System.exit(0);
    }

    public QueryResult runQuery(List<String> params) throws ParseException {
        int month1 = 1;
        int year1 = 2010;
        if(params != null && params.size() > 0){
            month1 = Integer.parseInt(params.get(1));
            year1 = Integer.parseInt(params.get(0));
        }

        graph = JanusGraphFactory.open("conf/"+confFile+"/janusgraph-cassandra-es.properties");
        GraphTraversalSource g = graph.traversal();


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        int month2 = month1+1;
        int year2 = year1;
        if(month1 == 12){
            month2 = 1;
            year2 = year1+1;
        }

        //dd.mm.yyyy
        String dateMin1 = "01."+month1+"."+year1;
        if(month1 < 10){
            dateMin1 = "01.0"+month1+"."+year1;
        }
        String dateMax1 = "01."+month2+"."+year2;
        if(month2 < 10){
            dateMax1 = "01.0"+month2+"."+year2;
        }

        String dateMin2 = "01."+month2+"."+year2;
        String dateMax2 = "01."+(month2+1)+"."+(year2);
        if(month2 < 9){
            dateMin2 = "01.0"+month2+"."+year2;
            dateMax2 = "01.0"+(month2+1)+"."+(year2);
        }else if(month2 == 9){
            dateMin2 = "01.0"+month2+"."+year2;
        }else if(month2 == 12){
            dateMax2 = "01.0"+1+"."+(year2+1);
        }

        System.out.println(dateMax1 + " " + dateMin1);
        System.out.println(dateMax2 + " " + dateMin2);
        Date dMax1 = dateFormat.parse(dateMax1);
        Date dMin1 = dateFormat.parse(dateMin1);
        Date dMax2 = dateFormat.parse(dateMax2);
        Date dMin2 = dateFormat.parse(dateMin2);


        long startTime = System.currentTimeMillis();
        /*
        * FIXME: Sort order??
        * */

        List<Map<Object, Long>> resultSet1 = g.V()//.hasLabel("post")                        //.hasLabel("post", "comment")
                .has("po_creationDate", P.lt(dMax1))
                .has("po_creationDate", P.gte(dMin1))
                .as("messagex")
                .out("hasTag")
                .as("tagx")
                .groupCount()
                .by(select("tagx"))
                .toList();


        List<Map<Object, Long>> resultSet2 = g.V()//.hasLabel("post")                        //.hasLabel("post", "comment")
                .has("po_creationDate", P.lt(dMax2))
                .has("po_creationDate", P.gte(dMin2))
                .as("messagex")
                .out("hasTag")
                .as("tagx")
                .groupCount()
                .by(select("tagx"))
                .toList();

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        graph.close();

        List<Map<Object, Long>> result = resultSet1;
        result.addAll(resultSet2);
        long resultCount = 0;
        for(int i=0;i<result.size();i++){
            Map<Object, Long> map = result.get(i);
            for (Map.Entry<Object, Long> entry : map.entrySet()) {
                resultCount += entry.getValue();
            }
        }


        System.out.println("==========================================");
        System.out.println("TotalTime: "+ totalTime);
//        for (Map map : resultSet1) {
//            System.out.println("map: " + map);
//            System.out.println("map: " + map.keySet().size());
//        }
//        for (Map map : resultSet2) {
//            System.out.println("map: " + map);
//            System.out.println("map: " + map.keySet().size());
//        }

        System.out.println("Count: " + resultCount);
        System.out.println("==========================================");


        QueryResult queryResult = new QueryResult();
        queryResult.setQueryName("Q3: ("+params+")");
        queryResult.setResultCount(resultCount);
        queryResult.setTimeToRun(totalTime);
        queryResult.setResults(result);
        return queryResult;
    }

}

