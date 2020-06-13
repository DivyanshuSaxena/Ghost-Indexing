package Queries;

import main.QueryResult;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;

import java.util.List;

public abstract class BaseQuery {
    public String confFile = "local";
    public JanusGraph graph;
    public abstract QueryResult runQuery(GraphTraversalSource g, List<String> params) throws Exception ;
}
