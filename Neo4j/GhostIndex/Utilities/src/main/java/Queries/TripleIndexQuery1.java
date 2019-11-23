////package Queries;
//
//import javafx.util.Pair;
//import main.IndexRangeQuery;
//import main.IndexRangeQueryPre;
//import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
//import org.apache.tinkerpop.gremlin.structure.Direction;
//import org.apache.tinkerpop.gremlin.structure.Edge;
//import org.apache.tinkerpop.gremlin.structure.T;
//import org.apache.tinkerpop.gremlin.structure.Vertex;
//import org.janusgraph.core.JanusGraph;
//import org.janusgraph.core.JanusGraphFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.group;
//import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.in;
//import static org.janusgraph.graphdb.database.serialize.AttributeUtil.compare;
//
//public class TripleIndexQuery1 {
//    private static final Logger LOGGER = LoggerFactory.getLogger(TripleIndexQuery1.class);
//
//    public static void main(String[] args) throws Exception {
//        Query1 q = new Query1();
//        q.runQuery(null);
//
//        System.exit(0);
//    }
//
//    public QueryResult runQuery(List<String> params) throws ParseException {
//        String date = "";
//        if(params == null || params.size() == 0){
//            date = "2010-01-02T20:16:15.144+0000";
//        }else{
//            date = params.get(0);
//        }
//
//        graph = JanusGraphFactory.open("conf/baadal/janusgraph-cassandra-es.properties");
//        GraphTraversalSource g = graph.traversal();
//
//        String indexName = "post_creationDate_index";
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//        String dateVar1 = "2010-01-3T20:16:15.144+0000";
//        String initDateVar = "2007-00-00T00:00:00.000+0000";
//
//        Date dateMin = dateFormat.parse(initDateVar);
//        Date dateMax = dateFormat.parse(date);
//
//        Vertex tripleIndexRoot = g.V().has("id", "0").hasLabel("INDEX").has("name", "tripleIndex").toList().get(0);
//
//        long startTime = System.currentTimeMillis();
//
//        Stack<Vertex> vStack = new Stack<>();
//        vStack.push(tripleIndexRoot);
//
//        ArrayList<Vertex> result = new ArrayList<>();
//        Iterator<Edge> edges;
//        while(!vStack.empty()){
//            Vertex v = vStack.pop();
//
//            edges = v.edges(Direction.OUT,"INDEX_DATA_EDGE");
//            if (!edges.hasNext()) {
//                edges = v.edges(Direction.OUT, "INDEX_EDGE");
//                List<Pair<Vertex, Long>> children = new ArrayList<>();
//                while (edges.hasNext()) {
//                    Edge e = edges.next();
//
//                    Vertex tmp = e.inVertex();
//                    children.add(new Pair(tmp, tmp.property("id").value()));
//                }
//                Collections.sort(children, Comparator.comparing(p -> -p.getValue()));
//                for (Pair outV : children) {
//                    vStack.push((Vertex) outV.getKey());
//                }
//                continue;
//            }
//            result.addAll(new IndexRangeQueryPre(v).searchRange(dateMin, dateMax));
//        }
//
//        long endTime   = System.currentTimeMillis();
//        long totalTime = endTime - startTime;
//
//
//        System.out.println("==========================================");
//        System.out.println("TotalTime: "+ totalTime);
//
//
//        for(Vertex map:result){
//            System.out.println("map: "+map);
//        }
//
//        graph.close();
//
//        QueryResult queryResult = new QueryResult();
//        queryResult.setQueryName("Q1: ("+date+")");
//        queryResult.setResultCount(result.size());
//        queryResult.setTimeToRun(totalTime);
//        queryResult.setResults(result);
//        return queryResult;
//    }
//
//}
