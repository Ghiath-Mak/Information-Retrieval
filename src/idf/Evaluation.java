/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idf;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author
 */
public class Evaluation {

    public static Map<String, Double> calculatePrecision(Map<String, Double> docSimilarities,
            List<String> relevantDocuments) {

        Map<String, Double> sortedSimilarities = sortByValueThenKey(docSimilarities);
        Map<String, Double> precisions = new LinkedHashMap<>();

        var relevant = 0D;
        var retrieved = 0D;
        for (String key : sortedSimilarities.keySet()) {
            retrieved++;
            if (relevantDocuments.contains(key)) {
                relevant++;
            }
            precisions.put(key, relevant / retrieved);
        }

        return precisions;
    }

    public static Map<String, Double> calculateRecall(Map<String, Double> docSimilarities,
            List<String> relevantDocuments) {

        Map<String, Double> sortedSimilarities = sortByValueThenKey(docSimilarities);
        Map<String, Double> recalls = new LinkedHashMap<>();

        var relevant = (double) relevantDocuments.size();
        var relevantRetrieved = 0D;
        for (String key : sortedSimilarities.keySet()) {
            if (relevantDocuments.contains(key)) {
                relevantRetrieved++;
            }
            recalls.put(key, relevantRetrieved / relevant);
        }

        return recalls;
    }

    /**
     * Sorts the entities in a Map backwards according to thier value and then
     * forwards according to thier keys if the values were equal.
     *
     * @param map
     * @return a sorted Map
     */
    public static Map<String, Double> sortByValueThenKey(Map<String, Double> map) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double>> list
                = new LinkedList<>(map.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> obj1,
                    Map.Entry<String, Double> obj2) {
                int comparision = obj2.getValue().compareTo(obj1.getValue());
                if (comparision != 0) {
                    return comparision;
                } else {
                    return obj1.getKey().compareTo(obj2.getKey());
                }
            }
        });

        // put data from sorted list to hashmap
        Map<String, Double> tempHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            tempHashMap.put(entry.getKey(), entry.getValue());
        }
        return tempHashMap;
    }
}
