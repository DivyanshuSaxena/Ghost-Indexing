//package main;
//
////import com.opencsv.CSVReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class ParamsCSVReader {
//
//    public static void main(String[] args) {
//        String csvFile = "/home/prakhar0409/random/Ghost-Index-in-Graph-DB/GhostIndex/substitution_parameters/bi_17_param.txt";
//
////        ArrayList<ArrayList> res= readcsv(csvFile);
//        PerformanceTester pt = new PerformanceTester();
//        ArrayList<ArrayList<String>> res = pt.readParameters(csvFile,"\\|",1);
//        for (ArrayList<String> temp :
//                res) {
//            for (String s :
//                    temp) {
//                System.out.print(s+" ");
//            }
//            System.out.println();
//        }
//
//    }
//
//    public static ArrayList<ArrayList> readcsv(String csvFile) {
//        CSVReader reader = null;
//        ArrayList<ArrayList> substitutionParams = new ArrayList<>();
//        try {
//            reader = new CSVReader(new FileReader(csvFile), '|');
//            String[] line;
//
//            if ((line = reader.readNext()) != null) {
//                while ((line = reader.readNext()) != null) {
//                    ArrayList<String> temp = new ArrayList<>(Arrays.asList(line));
//                    substitutionParams.add(temp);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return substitutionParams;
//    }
//
//}
