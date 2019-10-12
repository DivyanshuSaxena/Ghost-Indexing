package main;

import Queries.*;
import org.janusgraph.core.JanusGraphException;

import javax.management.Query;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PerformanceTester {

    public static void main(String[] argv) throws Exception {

        if(argv.length < 3){
            System.out.println("Usage: java a.out <queryClassName(only the endclassname)> <param_number> <skiplines> [<confFile>]");
            System.out.println("Eg: java a.out IndexQuery1 1 1 [baadal/local/aryabhata]");
            return;
        }

        String queryClassName = argv[0];
        int query_numeric = Integer.parseInt(argv[1]);
        int skipLines = Integer.parseInt(argv[2]);

        Class queryClass = Class.forName("Queries."+queryClassName);
        Queries.Query query = (Queries.Query) queryClass.newInstance();
        if(argv.length > 3){
            query.confFile = argv[3];
        }


        PerformanceTester p = new PerformanceTester();
        p.getPerformance(query,query_numeric, skipLines);

    }

    public void getPerformance(Queries.Query query, int param_file, int skipParamsLines) throws Exception {
        int averageOver = 4;
        int skipFirstX = 2;

        String paramsDelimiter = "\\|";
        String queryParamsFolder = "substitution_parameters/";

        //get the class name to locate the QueryXX.csv file
        String className = query.getClass().getSimpleName();
        if(className.startsWith("Index")){
            className = className.substring(5);
        }

        //create query file path and result file path
        String csvFile = "substitution_parameters/bi_"+param_file+"_param.txt";
        String resultFile = "queryresults/"+query.getClass().getSimpleName()+".csv";
        clearFiles(resultFile, "full"+resultFile);


        PerformanceTester pt = new PerformanceTester();
        ArrayList<ArrayList<String>> parameters = pt.readParameters(csvFile,paramsDelimiter, skipParamsLines);

        QueryResult queryResult;
        long totalIters = averageOver + skipFirstX;
        int paramNum = 1;
        for (ArrayList params : parameters) {
            ArrayList<QueryResult> allResults = new ArrayList<>();

            for(int i=0;i<totalIters;i++) {
                try {
                    queryResult = query.runQuery(params);
                    System.out.println("a run: " + paramNum + "/" + i + ": " + queryResult.getTimeToRun());
                    if(i>=skipFirstX) {
                        allResults.add(queryResult);
                    }
                }catch(JanusGraphException e){
                    System.out.println("[EXCEPTION]: "+e.getMessage());
                    query.graph.close();
                    i--;
                }
            }

            paramNum++;

            //write averaged results for this set of parameters
            pt.writeResultsPartial(resultFile, allResults);
            //write all results for this set of parameters
            pt.writeFullResultsPartial("full"+resultFile, allResults);
        }
    }

    //deletes the old result files
    private static void clearFiles(String resultFile, String s) {
        File r = new File(resultFile);
        r.delete();
        r = new File(s);
        r.delete();
    }

    /*
    * Format: Count-meanQueryTime-stdDevQueryTime-medianQueryTime-meanIndexRunTime-stddevIndexRunTime-medianIndexRunTime
    * */
    public void writeResultsPartial(String resultFile, ArrayList<QueryResult> results) throws IOException {
        FileWriter writer = new FileWriter(resultFile,true);
//        writer.write(result.resultCount+" "+result.getTimeToRun()+"\n");

        Statistics stats = new Statistics(results);
        if(!results.isEmpty()) {
            writer.write(results.get(0).resultCount + " " + stats.getMeanQueryRunTime() + " " + stats.getStdDevQueryRunTime() + " " + stats.getMedianQueryRunTime() + " " +
                    stats.getMeanIndexRunTime() + " " + stats.getStdDevIndexRunTime() + " " + stats.getMedianIndexRunTime() + " "
                    +"\n");
        }
        writer.close();
    }

    public void writeFullResultsPartial(String resultFile, ArrayList<QueryResult> results) throws IOException {
        FileWriter writer = new FileWriter(resultFile, true);
        //RESULT COUNT --------TIME TO RUN1 -------------TIME TO RUN2
        if(results.size()>0){
            writer.write(""+results.get(0).getResultCount());
        }
        for (QueryResult qr : results){
            writer.write(" "+qr.getTimeToRun());
        }
        writer.write("\n");

        writer.close();
    }

    public void writeFullResults(String resultFile, ArrayList<ArrayList<QueryResult>> allResults) throws IOException {
        FileWriter writer = new FileWriter(resultFile);

        for (ArrayList<QueryResult> results: allResults) {
            if(results.size()>0){
                writer.write(""+results.get(0).getResultCount());
            }
            for (QueryResult qr : results){
                writer.write(" "+qr.getTimeToRun());
            }
            writer.write("\n");

        }
        writer.close();
    }

    public void writeResults(String resultFile, List<QueryResult> results) throws IOException {
        FileWriter writer = new FileWriter(resultFile);

        for (QueryResult result: results) {
            writer.write(result.resultCount+" "+result.getTimeToRun()+"\n");
        }
        writer.close();
    }

    public ArrayList<ArrayList<String>> readParameters(String csvFile, String delimiter, int skipLines){

        BufferedReader br = null;
        String line = "";

        ArrayList<ArrayList<String>> parameters = new ArrayList<>();

        int skipped = 0;
//        int hop = 25;
        try {
            br = new BufferedReader(new FileReader(csvFile));
//            int curline = 0;
            while ((line = br.readLine()) != null) {
                if(skipped<skipLines) {
                    skipped++;
                }else {
//                    if(curline%hop != 0) {
//                        continue;
//                    }
                    String[] params = line.split(delimiter);
                    parameters.add(new ArrayList<String>(Arrays.asList(params)));
//                    curline++;
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
