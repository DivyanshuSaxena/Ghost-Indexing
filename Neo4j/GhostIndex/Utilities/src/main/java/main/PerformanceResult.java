package main;

public class PerformanceResult extends QueryResult {
    long timeToRun = 0;


    public long getTimeToRun() {
        return timeToRun;
    }

    public void setTimeToRun(long timeToRun) {
        this.timeToRun = timeToRun;
    }
}
