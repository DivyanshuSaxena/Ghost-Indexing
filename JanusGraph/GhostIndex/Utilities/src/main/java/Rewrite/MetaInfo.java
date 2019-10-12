package Rewrite;

import com.sleepycat.je.tree.IN;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MetaInfo {

    /**
    * @attributeName: is the attribute on which we are looking up a index.
    * Out edges from super index should have the attribute that they are indexing.
    * returns: the index info if any index exists on that attribute else returns null
    * */
    public static HashMap<String, String> getIndexInfo(String attributeName){
        JanusGraph graph = null;
        try{
            graph = JanusGraphFactory.open("conf/local/janusgraph-cassandra-es.properties");
            GraphTraversalSource g = graph.traversal();
            List<Edge> indexes = g.V().has("index_id", -1).outE("SUPER_INDEX_EDGE").has("attribute", attributeName).has("index_type").toList();

            if(indexes.size() > 0){
                //FIXME: Select the best index possible
                String indexType = indexes.get(0).value("index_type");
                String indexName = indexes.get(0).inVertex().value("name");
                HashMap<String, String> indexInfo = new HashMap<>();
                indexInfo.put("name", indexName);
                indexInfo.put("type", indexType);

                graph.close();
                return indexInfo;
            }else{
                graph.close();
            }
        }catch (Exception E){
            graph.close();
            return null;
        }

        return null;
    }

    public static String BPTraverse(String indexName, String typeOfPred, long val){
        //FIXME: Need to handle multiple preicates from multple hasContainers in a HasStep

        if(typeOfPred == "lte") {
            return "g.V().has('index_id', -1).out().has('name', '" + indexName + "' ).repeat(__.outE('INDEX_EDGE').not(__.has('min', P.gte(" + val +") ) ).inV()).until(__.outE('INDEX_EDGE').count().is(0)).outE('INDEX_DATA_EDGE').has('val', P.lte("+ val +")).inV()";
        }else if(typeOfPred == "gte"){
            return "g.V().has('index_id', -1).out().has('name','" + indexName + "').repeat(__.outE('INDEX_EDGE').not(__.has('max', P.lte(" + val +") ) ).inV()).until(__.outE('INDEX_EDGE').count().is(0)).outE('INDEX_DATA_EDGE').has('val', P.gte("+ val +")).inV()";
        }
        return "g.V().has('index_id', -1).out().has('name'," + indexName + ").repeat(__.outE('INDEX_EDGE').not(__.has('max', P.lte(" + val +") ) ).inV()).until(__.outE('INDEX_EDGE').count().is(0)).outE('INDEX_DATA_EDGE').has('val', P.gte("+ val +")).inV()";
//        else{
//            return "g.V().has('index_id', -1).out().has('name'," + indexName + ").repeat(__.outE('INDEX_EDGE').not(__.has('min', P.gte(e_date) ) ).not(__.has('max', P.lte(s_date))).inV()).until(__.outE('INDEX_EDGE').count().is(0)).outE('INDEX_DATA_EDGE').has('val', P.gte(s_date)).has('val', P.lte(e_date)).inV()";
//        }
    }

    public static String BPTraverse(String indexName, Object minVal, Object maxVal){
        return "g.V().has('index_id', -1).out().has('name','" + indexName + "').repeat(__.outE('INDEX_EDGE').not(__.has('min', P.gte(" + maxVal + ") ) ).not(__.has('max', P.lte(" + minVal + "))).inV()).until(__.outE('INDEX_EDGE').count().is(0)).outE('INDEX_DATA_EDGE').has('val', P.gte(" + minVal +")).has('val', P.lte(" + maxVal + ")).inV()";
    }

    public static List<Step> BPTraverseSteps(String indexName, String typeOfPred, long val) throws ScriptException {
        String queryString = BPTraverse(indexName,typeOfPred,val);
        System.out.println("BPTraverse query string: "+queryString);

        GremlinGroovyScriptEngine gremlinGroovyScriptEngine = new GremlinGroovyScriptEngine();
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("g", EmptyGraph.instance().traversal());
        GraphTraversal traversal = (GraphTraversal)gremlinGroovyScriptEngine.eval(queryString, bindings);

        List<Step> steps = traversal.asAdmin().getSteps();
//        System.out.println(steps);
//        System.out.println(steps.get(0));
        List<Step> newSteps = new ArrayList<>();
        for (Step item : steps) {
            newSteps.add(item);
        }
        newSteps.remove(0);
//        System.out.println(newSteps);
        return newSteps;
    }

    public static List<Step> BPTraverseSteps(String indexName, Object minVal, Object maxVal) throws ScriptException {
//        if (minVal > maxVal) {/* T-ODO */}
        if (compare(minVal, maxVal) > 0) {/* TODO */}
        String queryString = BPTraverse(indexName, minVal, maxVal);
        System.out.println("BPTraverse query string: "+queryString);

        GremlinGroovyScriptEngine gremlinGroovyScriptEngine = new GremlinGroovyScriptEngine();
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("g", EmptyGraph.instance().traversal());
        GraphTraversal traversal = (GraphTraversal)gremlinGroovyScriptEngine.eval(queryString, bindings);

        List<Step> steps = traversal.asAdmin().getSteps();
//        System.out.println(steps);
//        System.out.println(steps.get(0));
        List<Step> newSteps = new ArrayList<>();
        newSteps.addAll(steps);
        newSteps.remove(0);
//        System.out.println(newSteps);
        return newSteps;
    }

    public static Object getMinVal(Object value) {
        if(value instanceof Integer || value instanceof Long){
            return -1l;
        }else if(value instanceof Date){
            return new Date(631155661000L); //1990-01-01T01:01:01.000+0000
        }
        return -1l;
    }

    public static Object getMaxVal(Object value) {
        if(value instanceof Integer || value instanceof Long){
            return 999999999l;
        }else if(value instanceof Date){
            return new Date(999999999l);
        }
        return 999999999l;
    }

    public static long compare(Object val1, Object val2) {
        if( (val1 instanceof Long || val1 instanceof Integer) && (val2 instanceof Long || val2 instanceof Integer) ) {
            String v1 = val1.toString(), v2 = val2.toString();
            return Long.parseLong(v1) - Long.parseLong(v2);
        }else if( (val1 instanceof Double || val1 instanceof Float ) && (val2 instanceof Double || val2 instanceof Float) ) {
            String v1 = val1.toString();
            String v2 = val2.toString();
            Double ans = Double.parseDouble(v1) - Double.parseDouble(v2);
            if(ans < 0){return -1;}
            return 1;       //cant compare doubles for equality
        }else if(val1 instanceof Date && val2 instanceof Date)
            return ((Date) val1).getTime() - ((Date) val2).getTime();
        else {
            throw new RuntimeException("Can't compare datatype that is not a number or Date");
        }
    }
}
