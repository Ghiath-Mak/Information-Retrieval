/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idf;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author gh_ma
 */
public class FileUtilities {

    /**
     * Reads an input file and return a set of the words (no multiple occurance)
     * it contains.
     *
     * @param file
     * @return a set of the words found in the file
     */
    public static Set<String> getWordsFromFile(File file) {
        return new HashSet<>(getWordsListFromFile(file));
    }

    /**
     * Reads an input file and return a list of all words it contains ordered
     * according to thier appearance.
     * <i>The word can occure multiple times in the resulting list!</i>
     *
     * @param file
     * @return a list of the words found in the file
     */
    public static List<String> getWordsListFromFile(File file) {
        try {
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            List<String> words = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                words.addAll(getWordsListFromLine(scanner.nextLine()));
            }
            return words;
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR! - " + ex.getMessage());
            return null;
        }
    }

    private static List<String> getWordsListFromLine(String line) {
        List<String> words = new ArrayList<>();
        String[] wordsArray = line.split(" ");
        for (String word : wordsArray) {
            word = trimWord(word);
            if (!"".equals(word) && !"-".equals(word)) {
                words.add(word);
            }
        }
        return words;
    }

    private static String trimWord(String word) {
        return word.trim()
                .replace(",", "")
                .replace(".", "")
                .replace("\"", "")
                .replace(";", "")
                .replace("(", "")
                .replace(")", "")
                .replace("?", "")
                .replace("!", "");
    }
}
