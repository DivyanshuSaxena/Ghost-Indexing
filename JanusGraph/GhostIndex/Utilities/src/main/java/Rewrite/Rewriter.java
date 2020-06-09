package Rewrite;

import groovy.transform.Sortable;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GroovyTranslator;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.HasStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.NotStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import javax.script.SimpleBindings;
import java.util.*;

public class Rewriter {
    public static void main(String[] args) throws Exception{
        String inputQuery = "g.V().has('po_creationDate', lte(111111111)).has('po_creationDate', gte(22222222222222))";

        System.out.print(">> ");
        Scanner sc = new Scanner(System.in);
        inputQuery = sc.nextLine();
        System.out.println("   " + inputQuery);

        Traversal.Admin modifiedTraversal = rewriter(inputQuery);
        System.out.println("<< " + GroovyTranslator.of("g").translate(modifiedTraversal.asAdmin().getBytecode()));
    }

    public static Traversal.Admin rewriter(String inputQuery) throws Exception{
        //convert the string into traversal
        GremlinGroovyScriptEngine gremlinGroovyScriptEngine = new GremlinGroovyScriptEngine();
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("g", EmptyGraph.instance().traversal());
        GraphTraversal traversal = (GraphTraversal)gremlinGroovyScriptEngine.eval(inputQuery, bindings);

        System.out.println(traversal);
        //get steps from the traversal (NOTE THIS IS JUST A REFERENCE AND SO CHANGING STEPS WILL ALSO CHANGE TRAVERSAL)

        Traversal.Admin newTraversal = rewriteTraversal(traversal.asAdmin());
        return newTraversal;
    }

    public static Traversal.Admin rewriteTraversal(Traversal.Admin traversal) throws Exception{
        System.out.println(traversal);
        List<Step> steps = traversal.asAdmin().getSteps();

        LinkedList<Step> newTraversalSteps = new LinkedList<>();
        while (steps.size() > 0) {
            Step step = steps.get(0);

            if(newTraversalSteps.size() > 0) {
                System.out.println("Graph: " + newTraversalSteps.getLast());
            }

            // This internally checks if there are more has steps on the same attribute in front of this guy.
            if(step instanceof HasStep && newTraversalSteps.getLast() instanceof GraphStep && ((GraphStep)newTraversalSteps.getLast()).getIds().length == 0){
                List<Step> hasStepList = new LinkedList();
                while (step instanceof HasStep) {
                    List<HasContainer> hasContainers = ((HasStep) step).getHasContainers();
                    traversal.asAdmin().removeStep(0);

                    // the query parser only supports one hasContainer anyway, so chill
                    HasContainer hasContainer = hasContainers.get(0);

                    String hasStepKey = hasContainer.getKey();
                    HashMap<String, String> indexInfo = MetaInfo.getIndexInfo(hasStepKey);
                    if(indexInfo != null) {
                        String indexName = indexInfo.get("name");
                        String indexType = indexInfo.get("type");
                    
                        System.out.println(hasContainer.getValue());
                        Object value = hasContainer.getValue();

                        //lte/gte/eq
                        String typeOfPred = hasContainer.getBiPredicate().toString();
                        Object minVal = MetaInfo.getMinVal(value), maxVal = MetaInfo.getMaxVal(value);
                        //long minVal = -1, maxVal = 999999999l;
                        System.out.println("type: "+typeOfPred);
                        if (!typeOfPred.equals("gte") && !typeOfPred.equals("gt"))
                            maxVal = value;
                        if (!typeOfPred.equals("lte") && !typeOfPred.equals("lt"))
                            minVal = value;

                        while (steps.size() > 0 && steps.get(0) instanceof HasStep) {
                            step = steps.get(0);
                            traversal.asAdmin().removeStep(0);
                            HasContainer currHasContainer = ((HasContainer)(((HasStep) step).getHasContainers().get(0)));
                            String key = currHasContainer.getKey();
                            if (!key.equals(hasStepKey)) {
                                hasStepList.add(step);
                                continue;
                            }
                            Object currVal = currHasContainer.getValue();

                            String currPred = currHasContainer.getBiPredicate().toString();
                            System.out.println(minVal + "=-=-=-=-==" + maxVal);
                            if (!currPred.equals("gte") &&  MetaInfo.compare(maxVal, currVal) > 0) //maxVal > currVal)
                                maxVal = currVal;
                            if (!currPred.equals("lte") && MetaInfo.compare(minVal, currVal) < 0 ) //minVal < currVal)
                                minVal = currVal;
                        }

                        System.out.println(minVal + "=-=- FINAL =-=-==" + maxVal);
                        
                        List<Step> newSteps = MetaInfo.BPTraverseSteps(indexName, minVal, maxVal);
                        newTraversalSteps.addAll(newSteps);
                    }
                    else {
                        hasStepList.add(step);
                    }
                    if (steps.size() == 0) {
                        step = null;
                        break;
                    }
                    step = steps.get(0);
                }
                newTraversalSteps.addAll(hasStepList);
                if (step != null) {
                    newTraversalSteps.add(step);
                    traversal.asAdmin().removeStep(0);
                }
            }else {
                newTraversalSteps.add(step);
                traversal.asAdmin().removeStep(0);      //this decreases steps.size() also
            }
        }


        while (newTraversalSteps.size() > 0)
            traversal.asAdmin().addStep(newTraversalSteps.removeFirst());
        System.out.println("newTraversal: "+traversal);

        return traversal;
    }

}
