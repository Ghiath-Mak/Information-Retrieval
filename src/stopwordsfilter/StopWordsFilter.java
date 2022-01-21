/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stopwordsfilter;

import idf.Evaluation;
import idf.InverseDocumentFrequency;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class StopWordsFilter {

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

    /**
     * @param inverseDocumentFrequency
     * @param query
     */
    private static void printCosineSimilarities(
            InverseDocumentFrequency inverseDocumentFrequency,
            String query) {

        Map<String, Double> cosineSimilarities = inverseDocumentFrequency.calculateCosineSimilarities(query);

        for (Map.Entry<String, Double> entry : Evaluation.sortByValueThenKey(cosineSimilarities).entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            System.out.format("%s:\t%.4f",
                    key,
                    value);
            System.out.println();
        }
    }

    /**
     * Prints the Precision-Recall-Table
     *
     * @param cosineSimilarities the cosine-similarities to be printed
     * @param relevantDocuments  a list of the specified relevant documents
     */
    public static void printPrecisionRecallTable(
            Map<String, Double> cosineSimilarities,
            List<String> relevantDocuments) {

        Map<String, Double> precisions = Evaluation.calculatePrecision(
                Evaluation.sortByValueThenKey(cosineSimilarities),
                relevantDocuments);

        Map<String, Double> recalls = Evaluation.calculateRecall(
                Evaluation.sortByValueThenKey(cosineSimilarities),
                relevantDocuments);
        double rank = 0;
        System.out.println("DOCs\t1R 0N\tRANK\tRECALL\tPRECISION");
        for (Map.Entry<String, Double> entry : precisions.entrySet()) {
            double relevant = (relevantDocuments.contains(entry.getKey()))
                    ? 1
                    : 0;
            ++rank;
            System.out.format("%s:\t%.2f\t%.2f\t%.2f\t%.2f",
                    entry.getKey(),
                    relevant,
                    rank,
                    recalls.get(entry.getKey()),
                    entry.getValue());
            System.out.println();
        }
    }

    public static List<String> chooseRelevantDocuments(List<String> documents) {
        System.out.println("Choose the relevante documents from the following "
                + "list by entering thier numbers separated with a comma `,`:");
        int index = 0;
        for (String document : documents) {
            index++;
            System.out.format("(%d) %s", index, document);
            System.out.println();
        }
        System.out.print("\nPlease type your choices:");
        Scanner scanner = new Scanner(System.in);
        List<Integer> choices = parseInput(scanner.nextLine());
        List<String> relevantDocuments = new ArrayList<>();
        choices.forEach(choice ->
                relevantDocuments.add(documents.get(choice - 1)));
        return relevantDocuments;
    }

    private static List<Integer> parseInput(String input) {
        List<Integer> choices = new ArrayList<>();
        String[] inputs = input.split(",");
        for (String i : inputs) {
            choices.add(Integer.parseInt(i.trim()));
        }
        return choices;
    }

    public static void main(String[] args) throws IOException {

        File stopWordsFile = new File("resources/inforet/stopwords.txt");
        List<String> stopWords = StopWordsUtilities.readStopWordsFile(stopWordsFile);

        // File inputFiles = new File("resources/inforet/shorttexts");
        File inputFiles = new File("resources/inforet/test_3");
        File outputSTPDirectory = new File("resources/outputSTP");

        File[] dataFiles = inputFiles.listFiles();
        for (File f : dataFiles) {
            System.out.println("File " + f.getName());
        }

        for (File file : Objects.requireNonNull(outputSTPDirectory.listFiles())) {
            if (!file.isDirectory()) {
                Files.delete(file.toPath());
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
        for (File file : Objects.requireNonNull(outputSFXDirectory.listFiles())) {
            if (!file.isDirectory()) {
                Files.delete(file.toPath());
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

        Map<String, Double> cosineSimilarities = idf.calculateCosineSimilarities(query);
        System.out.println("\nThe sorted cosine-similarities are:");
        System.out.println("-----------------------------------");
        printCosineSimilarities(idf, query);

        System.out.println();

        List<String> relevantDocuments = chooseRelevantDocuments(idf.getFilesNames());

        System.out.println("\nCalculating the precisions and recalls:");
        System.out.println("-----------------------------------------\n");
        printPrecisionRecallTable(cosineSimilarities, relevantDocuments);

    }

}
