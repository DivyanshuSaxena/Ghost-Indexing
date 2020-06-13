package main;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphException;
import org.janusgraph.core.JanusGraphFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PerformanceTester {

    public static void main(String[] argv) throws Exception {

        if (argv.length < 5) {
            System.out.println(
                    "Usage: java a.out <queryClassName(only the endclassname)> <param_number> <skiplines> <dataset> <distributed or not> [<confFile>]");
            System.out.println("Eg: java a.out IndexQuery1 1 1 1000 1 [baadal/local/aryabhata]");
            return;
        }

        String queryClassName = argv[0];
        int query_numeric = Integer.parseInt(argv[1]);
        int skipLines = Integer.parseInt(argv[2]);
        int dataset = Integer.parseInt(argv[3]);
        String distributed = argv[4];

        Class queryClass = Class.forName("Queries." + queryClassName);
        Queries.BaseQuery query = (Queries.BaseQuery) queryClass.newInstance();
        if (argv.length > 5) {
            query.confFile = argv[5];
        }

        PerformanceTester p = new PerformanceTester();
        p.getPerformance(query, query_numeric, skipLines, dataset, distributed);

    }

    public void getPerformance(Queries.BaseQuery query, int param_file, int skipParamsLines, int dataset,
            String distributed) throws Exception {
        String paramsDelimiter = "\\|";

        // create query file path and result file path
        String csvFile = "../substitution_parameters/bi_" + param_file + "_param.txt";
        String resultFile = "../queryresults/" + query.getClass().getSimpleName() + "_" + dataset + ".csv";
        clearFiles(resultFile);

        PerformanceTester pt = new PerformanceTester();
        ArrayList<ArrayList<String>> parameters = pt.readParameters(csvFile, paramsDelimiter, skipParamsLines);

        QueryResult queryResult;
        int paramNum = 1;
        int averageOver = 2;
        int isDistributed = Integer.parseInt(distributed);

        for (ArrayList<String> params : parameters) {
            String numTries = "3";
            params.add(numTries);
            ArrayList<QueryResult> allResults = new ArrayList<>();

            for (int iters = 0; iters < averageOver; iters++) {
                JanusGraph graph;
                if (isDistributed == 1) {
                    System.out.println("DISTRIBUTED SETTING");
                    graph = JanusGraphFactory.build().set("storage.backend", "cassandrathrift")
                            .set("storage.hostname", "10.17.5.53").set("storage.cassandra.frame-size-mb", 60)
                            .set("index.search.backend", "elasticsearch")
                            .set("index.search.hostname", "10.17.5.53:9210").open();
                } else {
                    graph = JanusGraphFactory.open("../conf/janusgraph-cassandra-es.properties");
                }
                GraphTraversalSource g = graph.traversal();

                try {
                    queryResult = query.runQuery(g, params);
                    System.out.println("a run: " + paramNum + "/" + iters + ": " + queryResult.getWarmCacheTime());
                    allResults.add(queryResult);
                } catch (JanusGraphException e) {
                    System.out.println("[EXCEPTION]: " + e.getMessage());
                    query.graph.close();
                    iters--;
                }

                graph.close();
            }
            paramNum++;

            // write averaged results for this set of parameters
            pt.writeResultsPartial(resultFile, allResults);
        }
    }

    // Deletes the old result files
    private static void clearFiles(String resultFile) {
        File r = new File(resultFile);
        r.delete();
    }

    /*
     * Format:
     * Count-meanColdStartTime-meanQueryTime-stdDevQueryTime-medianQueryTime-
     * meanIndexRunTime- stddevIndexRunTime-medianIndexRunTime
     */
    public void writeResultsPartial(String resultFile, ArrayList<QueryResult> results) throws IOException {
        FileWriter writer = new FileWriter(resultFile, true);
        // writer.write(result.resultCount+" "+result.getWarmCacheTime()+"\n");

        Statistics stats = new Statistics(results);
        if (!results.isEmpty()) {
            writer.write(
                    results.get(0).resultCount + " " + stats.getMeanQueryRunTime() + " " + stats.getStdDevQueryRunTime()
                            + " " + stats.getMedianQueryRunTime() + " " + stats.getMeanIndexRunTime() + " "
                            + stats.getStdDevIndexRunTime() + " " + stats.getMedianIndexRunTime() + " " + "\n");
        }
        writer.close();
    }

    public void writeResults(String resultFile, List<QueryResult> results) throws IOException {
        FileWriter writer = new FileWriter(resultFile);

        for (QueryResult result : results) {
            writer.write(result.resultCount + " " + result.getWarmCacheTime() + "\n");
        }
        writer.close();
    }

    public ArrayList<ArrayList<String>> readParameters(String csvFile, String delimiter, int skipLines) {

        BufferedReader br = null;
        String line = "";

        ArrayList<ArrayList<String>> parameters = new ArrayList<>();

        int skipped = 0;
        // int hop = 25;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            // int curline = 0;
            while ((line = br.readLine()) != null) {
                if (skipped < skipLines) {
                    skipped++;
                } else {
                    // if(curline%hop != 0) {
                    // continue;
                    // }
                    String[] params = line.split(delimiter);
                    parameters.add(new ArrayList<String>(Arrays.asList(params)));
                    // curline++;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return parameters;
    }
}
