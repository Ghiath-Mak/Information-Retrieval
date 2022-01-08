package stopwordsfilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author
 */
class StopWordsUtilities {

    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    public static List<String> readStopWordsFile(File stopWordsFile) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(stopWordsFile);
        List<String> stopWords = new ArrayList<>();

        while (fileScanner.hasNextLine()) {
            String data = fileScanner.nextLine();
            stopWords.add(data);
        }

        fileScanner.close();
        return stopWords;
    }

    public static List<String> fileToListWithoutStopWords(File inputFile, List<String> stopWords) throws FileNotFoundException {
        List<String> filteredText = new ArrayList<>();
        Scanner fileScanner = new Scanner(inputFile);

        while (fileScanner.hasNextLine()) {
            String data = fileScanner.nextLine();
            String[] foundWordsList = data.split("[\\n\\s+]"); //split on new line and space/s
            for (String word : foundWordsList) {
                if (!stopWords.contains(word.toLowerCase())) { //check if word is not a stopWord && it is an email 
                    //handle @ 
                    if (word.contains("@")) {
                        if (isAValidEmail(word)) {
                            filteredText.add(word.toLowerCase());
                        }

                    } else if (word.contains(".")) {
                        filteredText.add(removeDot(word.toLowerCase()));

                    } else {

                        filteredText.add(word.toLowerCase());
                    }
                }
            }
        }

        fileScanner.close();

        return filteredText;
    }

    public static List<String> removeStopWords(String data, List<String> stopWords) {
        List<String> newText = new ArrayList<>();
        String[] foundWordsList = data.split("[\\n\\s+]"); //split on new line and space/s
        for (String word : foundWordsList) {
            if (!stopWords.contains(word.toLowerCase())) { //check if word is not a stopWord && it is an email 
                //handle @ 
                if (word.contains("@")) {
                    if (isAValidEmail(word)) {
                        newText.add(word.toLowerCase());
                    }

                } else if (word.contains(".")) {
                    newText.add(removeDot(word.toLowerCase()));

                } else {

                    newText.add(word.toLowerCase());
                }
            }
        }
        return newText;
    }
    
    //This method removes stop words and stems words in the sentence
    public static String sentenceNoStopWords(String sentence, List<String> stopWords) {
        if (sentence == null || sentence.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            String delim = " ";
            List<String> list;
            list = (removeStopWords(sentence, stopWords));
            list = StopWordsFilter.completeStem(list);
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i));
                sb.append(delim);
            }
            return sb.toString();
        }

    }
    
    public static List<String> fileToList(String inputFile) throws FileNotFoundException {
        String tempString;

        Scanner scanner = new Scanner(new File(inputFile));

        List<String> wordsList = new ArrayList<>();

        while (scanner.hasNext()) {
            // find next line
            tempString = scanner.next();
            wordsList.add(tempString);
        }
        scanner.close();
        return wordsList;
    }

    public static void printToFile(List<String> filteredText, String outputFilePath) throws IOException {
        PrintWriter writer = new PrintWriter(outputFilePath, "UTF-8");
        String filter = "";

        for (String temp : filteredText) {
            filter = filter + temp + "\n";

        }
        writer.println(filter);
        writer.close();
    }

    private static boolean isAValidEmail(String emailString) {
        if (emailString.length() < 9) // handle if @ or @.com ...etc and some cases that passes through regex but its not email
        {
            return false;
        }

        // Pattern pattern = Pattern.compile(EMAIL_REGEX);
        // System.out.println(" #### email Result "+emailString+" valid::: "+ pattern.matcher(emailString).matches());
        return Pattern.matches(EMAIL_REGEX, emailString);
    }

    private static String removeDot(String word) {//this method removes . if exist at the end of the word 

        if (word.charAt(word.length() - 1) == '.') {
            return word.substring(0, word.length() - 1);
        }

        return word;
    }
}
