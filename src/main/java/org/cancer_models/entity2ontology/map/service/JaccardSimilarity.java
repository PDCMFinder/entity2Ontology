package org.cancer_models.entity2ontology.map.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaccardSimilarity {

    // List of words to ignore
    private static final List<String> STOP_WORDS = Arrays.asList("in", "on", "the", "of", "is", "at", "by", "the");

    // Regexp to be used to split phrases by spaces or "/" symbol ("/" is frequently found in the labels of ontologies)
    private static final String WORDS_SEPARATOR_REGEXP = "[\\s/]+";

    public static double calculate(String phrase1, String phrase2) {
        // Convert the phrases to sets of words, filtering out stop words
        Set<String> set1 = new HashSet<>(filterStopWords(phrase1.toLowerCase().split(WORDS_SEPARATOR_REGEXP)));
        Set<String> set2 = new HashSet<>(filterStopWords(phrase2.toLowerCase().split(WORDS_SEPARATOR_REGEXP)));

        // Calculate fuzzy intersection and adjust the second set accordingly
        int intersectionSize = intersection(set1, set2);

        // Union size is now the size of the first set plus remaining words in the second set
        int unionSize = set1.size() + set2.size();

        // Calculate and return fuzzy Jaccard similarity
        return (double) intersectionSize / unionSize;
    }

    // Method to calculate intersection based on Levenshtein distance
    private static int intersection(Set<String> set1, Set<String> set2) {
        int count = 0;
        Set<String> toRemove = new HashSet<>(); // Track words to remove from set2 after fuzzy match

        for (String word1 : set1) {
            for (String word2 : set2) {
                if (word1.equalsIgnoreCase(word2)) {
                    count++;
                    toRemove.add(word2);  // Mark for removal to avoid duplicate counting
                    break;
                }
            }
        }
        set2.removeAll(toRemove);  // Remove fuzzy-matched words from set2
        return count;
    }

    // Method to filter out stop words
    private static Set<String> filterStopWords(String[] words) {
        Set<String> filteredWords = new HashSet<>();
        for (String word : words) {
            if (!STOP_WORDS.contains(word)) {
                filteredWords.add(word);
            }
        }
        return filteredWords;
    }
}
