/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stopwordsfilter;

import idf.InverseDocumentFrequency;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class StopWordsFilter {

    private static final String QUERY_FILE_PATH = "resources/outputSFX/query";

    public static void main(String[] args) throws IOException {

        File stopWordsFile = new File("resources/inforet/stopwords.txt");
        List<String> stopWords = StopWordsUtilities.readStopWordsFile(stopWordsFile);

//        File inputFiles = new File("resources/inforet/shorttexts");
        File inputFiles = new File("resources/inforet/test_2");
        File outputSTPDirectory = new File("resources/outputSTP");

        File[] dataFiles = inputFiles.listFiles();
        for (File f : dataFiles) {
            System.out.println("File " + f.getName());
        }

        for (File file : outputSTPDirectory.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        for (File file : dataFiles) {
            List<String> filteredText = StopWordsUtilities.fileToListWithoutStopWords(file, stopWords);
            StopWordsUtilities.printToFile(filteredText,
                    outputSTPDirectory.getPath() + "/" + file.getName());
        }

        System.out.println("--------------------- Project 1  STOP WORDS ---------------------------");
        // File inputFiles2 = new File("C:/Users/user/Desktop/outputSTP");
        File outputSFXDirectory = new File("resources/outputSFX");
        for (File file : outputSFXDirectory.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        File[] stpFiles = outputSTPDirectory.listFiles();

        for (File file : stpFiles) {
            //   System.out.println(" 2 "+file.getName());
            List<String> tempList = StopWordsUtilities.fileToList("resources/outputSTP/" + file.getName());

            List<String> sfxResult = completeStem(tempList);
            StopWordsUtilities.printToFile(sfxResult,
                    outputSFXDirectory.getPath() + "/" + file.getName());
        }

        System.out.println("-------------------------- Project 2 Stemmer---------------------------");
        /*
        if (inputFileOrDirectory != null && inputFileOrDirectory.exists()) {
            if (inputFileOrDirectory.isDirectory() && inputFileOrDirectory.listFiles().length > 0) {
            	
                for (File inputFile : inputFileOrDirectory.listFiles()) {
                    List<String> filteredText = StopWordsUtilities.fileToListWithoutStopWords(inputFile, stopWords);
                    StopWordsUtilities.printToFile(filteredText,
                            outputSTPDirectory.getPath() + "/filtered_" + inputFile.getName());
                }
            } else {
                List<String> filteredText = StopWordsUtilities.fileToListWithoutStopWords(inputFileOrDirectory, stopWords);
                StopWordsUtilities.printToFile(filteredText,
                        outputSTPDirectory.getPath() + "/filtered_" + inputFileOrDirectory.getName());
            }
        }
         */

        InverseDocumentFrequency idf = new InverseDocumentFrequency(outputSTPDirectory.getAbsolutePath());
        System.out.println("The files under test are:");
        Arrays.asList(inputFiles.listFiles())
                .forEach(doc -> System.out.println(doc.getName()));
        System.out.println();

        idf.printMe();
        System.out.println();

        idf.printTfIDF();

        System.out.println("\nCalculating the cosine-similarty between a file and a usre's query:");
        System.out.println("-------------------------------------------------------------------");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter your query: ");
        String query = scanner.nextLine();

        idf.calculateCosineSimilarity(query);
    }

    public static List<String> completeStem(List<String> tokens1) {
        //Porter Algorithm
        Stemmer pa = new Stemmer();
        List<String> arrString = new ArrayList<>();
        for (String i : tokens1) {
            String s1 = pa.step1(i);
            String s2 = pa.step2(s1);
            String s3 = pa.step3(s2);
            String s4 = pa.step4(s3);
            String s5 = pa.step5(s4);
            arrString.add(s5);
        }

        return arrString;
    }

    public static void createQueryFile(String query, List<String> stopWords) throws IOException {
        if (query != null && !query.isBlank()) {
            List<String> tokens = StopWordsUtilities.removeStopWords(query, stopWords);
            tokens = completeStem(tokens);
            StopWordsUtilities.printToFile(tokens, QUERY_FILE_PATH);
        }
    }
}
