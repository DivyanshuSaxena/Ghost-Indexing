package main;

public class QueryResult extends Object{
    String queryName = "";
    long resultCount = 0;
    Object results;
    long timeToRun = 0;
    long timeToTraverseIndex = -1;


    /*
    * Time in ms that the query spent in traversing through the index
    * */
    public long getTimeToTraverseIndex() {
        return timeToTraverseIndex;
    }

    public void setTimeToTraverseIndex(long timeToTraverseIndex) {
        this.timeToTraverseIndex = timeToTraverseIndex;
    }

    /*
    * Time in ms for which the complete query
    * */
    public long getTimeToRun() {
        return timeToRun;
    }

    public void setTimeToRun(long timeToRun) {
        this.timeToRun = timeToRun;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    /*
    * Number of results returned by the query
    * */
    public long getResultCount() {
        return resultCount;
    }

    public void setResultCount(long resultCount) {
        this.resultCount = resultCount;
    }

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return resultCount+" : "+timeToRun;
    }
}
