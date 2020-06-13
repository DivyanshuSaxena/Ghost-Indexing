package Queries;

import main.QueryResult;

import org.janusgraph.core.JanusGraph;

import java.util.List;

public abstract class Query {
    public String confFile = "local";
    public JanusGraph graph;
    public abstract QueryResult runQuery(List<String> params) throws Exception ;
}
