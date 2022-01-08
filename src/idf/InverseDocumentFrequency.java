/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idf;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author
 */
public class InverseDocumentFrequency {

    private List<FileWordFrequency> fileWordFrequencies;
    private Set<String> wordsSet;

    public InverseDocumentFrequency(List<File> documents) {
        fileWordFrequencies = new ArrayList<>();
        wordsSet = new HashSet<>();

        if (documents != null && !documents.isEmpty()) {
            wordsSet = getWordsSetFromDocuments(documents);
            documents.forEach(document -> {
                wordsSet.stream().forEach(word
                        -> fileWordFrequencies.add(new FileWordFrequency(document, word)));
            });
        }
    }

    public InverseDocumentFrequency(String documentsPath) {
        this(Arrays.asList(new File(documentsPath).listFiles()));
    }

    private Set<String> getWordsSetFromDocuments(List<File> documents) {
        Set<String> wordsInDocuments = new LinkedHashSet<>();
        documents.stream().forEach(file
                -> wordsInDocuments.addAll(FileUtilities.getWordsFromFile(file)));
        return wordsInDocuments;
    }

    /**
     * Add a new document to the existing ones. In this case the set of words
     * wil be extended with the new words.
     *
     * The <i>FileWordFrequency</i> list will be as well extended and new
     * objects will be added containing the new file and the new document.
     *
     * @param document
     */
    public void addNewDocument(File document) {
        if (!getFiles().contains(document)) {
            Set<String> newWordSet = FileUtilities.getWordsFromFile(document);
            newWordSet.removeAll(wordsSet);
            getFiles().forEach(file
                    -> newWordSet.stream().forEach(word
                            -> fileWordFrequencies.add(new FileWordFrequency(file, word))));

            wordsSet.addAll(newWordSet);
            wordsSet.stream().forEach(word
                    -> fileWordFrequencies.add(new FileWordFrequency(document, word)));
        }
    }

    public void addNewDocument(String documentPath) {
        File document = new File(documentPath);
        addNewDocument(document);

    }

    /**
     *
     * @param document
     */
    public void removeDocument(File document) {
        List<File> documents = getFiles().stream()
                .filter(doc -> !doc.getName().equals(document.getName()))
                .collect(Collectors.toList());
        document.delete();
        wordsSet.clear();
        fileWordFrequencies.clear();
        wordsSet = getWordsSetFromDocuments(documents);
        documents.forEach(doc -> {
            wordsSet.stream().forEach(word
                    -> fileWordFrequencies.add(new FileWordFrequency(doc, word)));
        });
    }

    public void removeDocument(String documentPath) {
        File document = new File(documentPath);
        removeDocument(document);
    }

    public List<FileWordFrequency> getFileWordFrequencies() {
        return fileWordFrequencies;
    }

    public Set<String> getWordsSet() {
        return wordsSet;
    }

    /**
     * Calculates how many times does the word appear in a document (Frequency)
     *
     * @param word
     * @param file
     * @return the frequency of the word in the given document
     */
    public int getTermFrequency(String word, File file) {
        return fileWordFrequencies.stream()
                .filter(fwf -> (fwf.getFileName().equals(file.getName()) && fwf.getWord().equals(word)))
                .findFirst()
                .map(FileWordFrequency::getFrequency)
                .orElse(0);
    }

    /**
     * Calculate (Document Frequency - DFt,d), i.e., the number of documents
     * containing the specified term (word).
     *
     * @param word
     * @return the number of the documents containing the word
     */
    public int calculateDocumentFrequency(String word) {
        return (int) fileWordFrequencies.stream()
                .filter(fwf -> (fwf.getWord().equalsIgnoreCase(word)
                && fwf.wordExists()))
                .count();
    }

    public List<File> getFiles() {
        if (fileWordFrequencies != null && !fileWordFrequencies.isEmpty()) {
            List<File> filesList = fileWordFrequencies.stream()
                    .map(fwf -> fwf.getFile())
                    .distinct()
                    .collect(Collectors.toList());
            return filesList;
        } else {
            return null;
        }
    }

    public double calculateIDF(String word) {

        final double D = (double) getFiles().size();
        final double DFt = (double) calculateDocumentFrequency(word);

        return Math.log10(D / DFt);
    }

    /**
     * Returns the binary term frquency of a term (word) in a document.
     *
     * @param word
     * @param document
     * @return 1 if the words exists in the document, 0 otherwise.
     */
    private int getBinaryTermFrequency(String word, File document) {
        return (getTermFrequency(word, document) > 0) ? 1 : 0;
    }

    public double calculateTfIdf(String word, File document) {
        final double NUMBER_OF_FILES = (double) getFiles().size();
        final double DOC_FREQUENCY = (double) calculateDocumentFrequency(word);
        final int TERM_FREQUENCY = getTermFrequency(word, document);

        return TERM_FREQUENCY * Math.log10(NUMBER_OF_FILES / DOC_FREQUENCY);
    }

    public double calculateTfIdf(String word, String query) {
        List<String> queryWords = Arrays.asList(query.split(" "));
        final int TERM_FREQUENCY = Collections.frequency(queryWords, word);
        return TERM_FREQUENCY * calculateIDF(word);
    }

    /**
     * Calculate the cosine-similarity of all files depending on the words they
     * contain.
     *
     * @return the cosine-similarity of all files
     */
    public double calculateCosineSimilarity() {
        return calculateCosineSimilarity(getWordsSet(), getFiles());
    }

    public double calculateCosineSimilarity(List<File> documents) {
        Set<String> documentsWords = getWordsSetFromDocuments(documents);
        return calculateCosineSimilarity(documentsWords, documents);
    }

    /**
     * Calculate the cosine-similarity of a list of specified files depending on
     * a set of specified words.
     *
     * @param wordSet
     * @param documents
     * @return
     */
    public double calculateCosineSimilarity(Set<String> wordSet, List<File> documents) {
        double numerator = 0;
        double production = 1;
        for (String word : wordSet) {
            for (File document : documents) {
                production *= calculateTfIdf(word, document);
            }
            numerator += production;
            production = 1;
        }

        double denominator = 1;
        double sumOfSquers = 0;

        for (File document : documents) {
            for (String word : wordSet) {
                if ((getTermFrequency(word, document) > 0)) {
                    sumOfSquers += Math.pow(calculateTfIdf(word, document), 2);
                }
            }
            denominator *= sumOfSquers;
            sumOfSquers = 0;
        }
        double cosine = numerator / Math.sqrt(denominator);
        return cosine;
    }

    /**
     * Calculate the cosine-similarity of a query against the current documents
     * and prints the results.
     *
     * @param query
     */
    public void calculateCosineSimilarity(String query) {

        for (File document : getFiles()) {
            double numerator = 0;
            double denominator = 0;
            double sumOfDocTfIdf = 0;
            double sumOfQueryTfIdf = 0;

            for (String word : wordsSet) {
                numerator += calculateTfIdf(word, query) * calculateTfIdf(word, document);

                sumOfDocTfIdf += Math.pow(calculateTfIdf(word, document), 2);
                sumOfQueryTfIdf += Math.pow(calculateTfIdf(word, query), 2);
            }

            denominator = Math.sqrt(sumOfQueryTfIdf * sumOfDocTfIdf);
            System.out.format("%s:\t%.4f\n",
                    document.getName(),
                    numerator / denominator);
        }
    }

    /**
     * Prints the current object of this class as a table.
     */
    public void printMe() {
        System.out.println("Printing Files-Words-Frequency Matrix:");
        System.out.print("\t");
        getWordsSet().forEach(word -> System.out.print(word + "\t"));
        System.out.println();
        getFiles().forEach(document -> {
            System.out.print(document.getName() + "\t");
            getWordsSet().forEach(word
                    -> System.out.print(getTermFrequency(word, document) + "\t"));
            System.out.println();
        });

        System.out.print("DOCF:\t");
        getWordsSet().forEach(word
                -> System.out.print(calculateDocumentFrequency(word) + "\t"));
        System.out.println();

    }

    public void printTfIDF() {
        System.out.println("Printing Files-Words-TFxIDF Matrix:");
        System.out.print("\t");
        getWordsSet().forEach(word -> System.out.print(word + "\t"));
        System.out.println();

        getFiles().forEach(document -> {
            System.out.print(document.getName() + "\t");
            getWordsSet().forEach(word
                    -> System.out.format("%.4f\t", calculateTfIdf(word, document)));
            System.out.println();
        });

    }
}
