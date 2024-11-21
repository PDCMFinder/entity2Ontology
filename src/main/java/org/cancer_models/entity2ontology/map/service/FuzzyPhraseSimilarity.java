package org.cancer_models.entity2ontology.map.service;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility to perform a set-based comparison with two phrases in Java for similarity without considering the word order.
 * Using Jaccard similarity index as the metric. Useful for instances like "lung cancer" and "cancer in lung",
 * which should be scored as almost identical
 */
class FuzzyPhraseSimilarity {

    // Suppress default constructor for non-instantiability
    private FuzzyPhraseSimilarity() {
        throw new AssertionError();
    }

    // The similarity between two strings will be calculated with the Levenshtein distance algorithm.
    private static final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    // List of stop words to ignore
    private static final List<String> STOP_WORDS = Arrays.asList("in", "on", "the", "a", "an", "is", "at", "by");

    // Method to calculate fuzzy Jaccard similarity
    public static double fuzzyJaccardSimilarity(String phrase1, String phrase2, double fuzzinessThreshold) {
        // Convert the phrases to sets of words, filtering out stop words
        Set<String> set1 = new HashSet<>(filterStopWords(phrase1.toLowerCase().split("\\s+")));
        Set<String> set2 = new HashSet<>(filterStopWords(phrase2.toLowerCase().split("\\s+")));

        // Calculate fuzzy intersection and adjust the second set accordingly
        int intersectionSize = fuzzyIntersection(set1, set2, fuzzinessThreshold);

        // Union size is now the size of the first set plus remaining words in the second set
        int unionSize = set1.size() + set2.size();

        // Calculate and return fuzzy Jaccard similarity
        return (double) intersectionSize / unionSize;
    }

    // Method to calculate fuzzy intersection based on Levenshtein distance
    private static int fuzzyIntersection(Set<String> set1, Set<String> set2, double fuzzinessThreshold) {
        int count = 0;
        Set<String> toRemove = new HashSet<>(); // Track words to remove from set2 after fuzzy match

        for (String word1 : set1) {
            for (String word2 : set2) {
                // Use Levenshtein distance to check similarity
                if (levenshteinDistance.apply(word1, word2) <= fuzzinessThreshold) {
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
