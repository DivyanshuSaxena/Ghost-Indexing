package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class SortByQueryTime implements Comparator<Object> {
    public int compare(Object a1, Object b1) {
        QueryResult a = (QueryResult) a1;
        QueryResult b = (QueryResult) b1;
        if (a.getWarmCacheTime() - b.getWarmCacheTime() > 0) {
            return 1;
        } else if (a.getWarmCacheTime() - b.getWarmCacheTime() == 0)
            return 0;
        return -1;
    }
}

class SortByIndexTime implements Comparator<Object> {
    public int compare(Object a1, Object b1) {
        QueryResult a = (QueryResult) a1;
        QueryResult b = (QueryResult) b1;
        if (a.getTimeToTraverseIndex() - b.getTimeToTraverseIndex() > 0) {
            return 1;
        } else if (a.getTimeToTraverseIndex() - b.getTimeToTraverseIndex() == 0)
            return 0;
        return -1;
    }
}

public class Statistics {
    ArrayList<QueryResult> data;
    int size;

    public Statistics(ArrayList<QueryResult> data) {
        this.data = data;
        size = data.size();
    }

    double getMeanColdStartTime() {
        double sum = 0.0;
        for (QueryResult a : data)
            sum += a.getColdStartTime();
        return sum / size;
    }

    double getMeanQueryRunTime() {
        double sum = 0.0;
        for (QueryResult a : data)
            sum += a.getWarmCacheTime();
        return sum / size;
    }

    double getMeanIndexRunTime() {
        double sum = 0.0;
        for (QueryResult a : data)
            sum += a.getTimeToTraverseIndex();
        return sum / size;
    }

    double getVarianceQueryRunTime() {
        double mean = getMeanQueryRunTime();
        double temp = 0;
        for (QueryResult a : data)
            temp += (a.getWarmCacheTime() - mean) * (a.getWarmCacheTime() - mean);
        return temp / (size - 1);
    }

    double getVarianceIndexRunTime() {
        double mean = getMeanIndexRunTime();
        double temp = 0;
        for (QueryResult a : data)
            temp += (a.getTimeToTraverseIndex() - mean) * (a.getTimeToTraverseIndex() - mean);
        return temp / (size - 1);
    }

    double getStdDevQueryRunTime() {
        return Math.sqrt(getVarianceQueryRunTime());
    }

    double getStdDevIndexRunTime() {
        return Math.sqrt(getVarianceIndexRunTime());
    }

    public double getMedianQueryRunTime() {
        Arrays.sort(data.toArray(), new SortByQueryTime());

        if (data.size() % 2 == 0) {
            return (data.get((data.size() / 2) - 1).getWarmCacheTime() + data.get(data.size() / 2).getWarmCacheTime())
                    / 2.0;
        }
        return data.get(data.size() / 2).getWarmCacheTime();
    }

    public double getMedianIndexRunTime() {
        Arrays.sort(data.toArray(), new SortByIndexTime());

        if (data.size() % 2 == 0) {
            return (data.get((data.size() / 2) - 1).getTimeToTraverseIndex()
                    + data.get(data.size() / 2).getTimeToTraverseIndex()) / 2.0;
        }
        return data.get(data.size() / 2).getTimeToTraverseIndex();
    }

}
