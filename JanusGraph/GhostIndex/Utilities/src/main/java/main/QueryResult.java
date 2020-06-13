package main;

public class QueryResult extends Object {
    String queryName = "";
    long resultCount = 0;
    Object results;
    long coldStartTime = 0;
    long warmCacheTime = 0;
    long timeToTraverseIndex = -1;

    /*
     * Time in ms that the query spent in traversing through the index
     */
    public long getTimeToTraverseIndex() {
        return timeToTraverseIndex;
    }

    public void setTimeToTraverseIndex(long timeToTraverseIndex) {
        this.timeToTraverseIndex = timeToTraverseIndex;
    }

    /*
     * Time in ms for which the complete query
     * 
     * @deprecated
     */
    public void setTimeToRun(long timeToRun) {
        this.setColdStartTime(timeToRun);
    }

    /*
     * @deprecated
     */
    public long getTimeToRun() {
        return this.getWarmCacheTime();
    }

    public long getColdStartTime() {
        return coldStartTime;
    }

    /*
     * Time in ms for which the complete query
     */
    public void setColdStartTime(long timeToRun) {
        this.coldStartTime = timeToRun;
    }

    public long getWarmCacheTime() {
        return warmCacheTime;
    }

    /*
     * Time in ms for which the complete query
     */
    public void setWarmCacheTime(long timeToRun) {
        this.warmCacheTime = timeToRun;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    /*
     * Number of results returned by the query
     */
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
        return resultCount + " : " + warmCacheTime;
    }
}
