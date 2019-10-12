package Rewrite;

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.HasStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleRewriter {
    public static void main(String[] args) throws Exception{
//        String inputQuery = "g.V().has('index_id', -1)";
        String inputQuery = "g.V(123).has('po_creationDate', lte(111111111))";
//        inputQuery = method1(inputQuery);

        GremlinGroovyScriptEngine gremlinGroovyScriptEngine = new GremlinGroovyScriptEngine();
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("g", EmptyGraph.instance().traversal());

        GraphTraversal result = (GraphTraversal)gremlinGroovyScriptEngine.eval(inputQuery, bindings);
//        System.out.println("result = " + result.asAdmin().getBytecode());
        System.out.println("result: "+result);
        System.out.println(result.asAdmin().getSteps());
        System.out.println(result.asAdmin().getSteps().get(0).getClass());
        HasStep hasStep = (HasStep) result.asAdmin().getSteps().get(1);
        System.out.println(hasStep);

        //get the first Has container
        System.out.println(hasStep.getHasContainers().get(0));
        HasContainer hasContainer = (HasContainer) hasStep.getHasContainers().get(0);
        System.out.println("================");
        System.out.println(hasContainer.getKey());
        System.out.println(hasContainer.getValue());
        System.out.println(hasContainer.getPredicate());
        System.out.println(hasContainer.getBiPredicate());
        System.out.println(hasContainer.getBiPredicate().getClass());
        System.out.println(hasContainer.getBiPredicate().toString());

        System.out.println(hasStep.getHasContainers().get(0));


//        hasStep.
//        org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
        System.out.println("resultType: "+result.getClass());
//        System.out.println("resultpath: "+result.path().getClass());

    }

    public static String method1(String inputQuery){
        String[] splits = inputQuery.split(".");

        final String regex = "(g|__)\\.V\\([A-Z0-9'\"]*\\)\\.has\\(";
        final Matcher m = Pattern.compile(regex).matcher(inputQuery);
        final List<String> matches = new ArrayList<>();
        while (m.find()) {
            matches.add(m.group(0));
            System.out.println("Matches:: "+m.group() + "   startsAt:" + m.start() + "--"+m.end());

            int startsAt = m.end();
            int endsAt = inputQuery.indexOf(inputQuery.charAt(startsAt), startsAt+1);
            System.out.println("yo: "+endsAt);
            System.out.println("len: "+inputQuery.length());
            String field = inputQuery.substring(startsAt+1, endsAt);

            System.out.println("Field: "+field);
            HashMap<String, String> indexInfo = MetaInfo.getIndexInfo(field);
            if(indexInfo == null){
                continue;
            }

            //FIXME: lte or gte


//            String indexTraverseCode = MetaInfo.BPTraverse(indexName);

        }


        //FIXME: THIS SEEMS TOO LAME. Also this does not handle more complex queries
        //FIXME: Also need to use index in traversals of the form __.V().has(something)
        if(splits.length > 3 && splits[0]=="g" && splits[1]=="V()" && splits[2].startsWith("has('po_creationDate'")){

        }

        return inputQuery;
    }
}
