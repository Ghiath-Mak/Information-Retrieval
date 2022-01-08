/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idf;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class FileWordFrequency {

    private File file;
    private String word;
    private int frequency;

    public FileWordFrequency(String filePath, String word) {
        this(new File(filePath), word);
    }

    public FileWordFrequency(File file, String word) {
        this.file = file;
        this.word = word;
        initiateFrequency();
    }

    private void initiateFrequency() {
        this.frequency = 0;
        List<String> wordsList = FileUtilities.getWordsListFromFile(file);
        frequency = Collections.frequency(wordsList, word);
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return file.getName();
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    public boolean wordExists() {
        return getFrequency() > 0;
    }

    public int getTermFrequency() {
        return (frequency == 0) ? 0 : 1;
    }
}
