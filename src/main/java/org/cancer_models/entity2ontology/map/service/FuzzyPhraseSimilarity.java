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

    // Regexp to be used to split phrases by spaces or "/" symbol ("/" is frequently found in the labels of ontologies)
    private static final String WORDS_SEPARATOR_REGEXP = "[\\s/]+";

    // For short words, applying fuzziness is problematic as can make 2 different words the same. This value reduces
    // that risk
    private static final int MINIMUM_WORD_LENGTH_TO_APPLY_FUZZINESS = 6;

    // Suppress default constructor for non-instantiability
    private FuzzyPhraseSimilarity() {
        throw new AssertionError();
    }

    // The similarity between two strings will be calculated with the Levenshtein distance algorithm.
    private static final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    // List of stop words to ignore
    private static final List<String> STOP_WORDS = Arrays.asList("in", "on", "the", "of", "is", "at", "by", "the");

    // Method to calculate fuzzy Jaccard similarity
    public static double fuzzyJaccardSimilarity(String phrase1, String phrase2, double fuzzinessThreshold) {
        // Convert the phrases to sets of words, filtering out stop words
        Set<String> set1 = new HashSet<>(filterStopWords(phrase1.toLowerCase().split(WORDS_SEPARATOR_REGEXP)));
        Set<String> set2 = new HashSet<>(filterStopWords(phrase2.toLowerCase().split(WORDS_SEPARATOR_REGEXP)));

        // Calculate fuzzy intersection and adjust the second set accordingly
        int intersectionSize = fuzzyIntersection(set1, set2, fuzzinessThreshold);
        System.out.println("intersectionSize: " + intersectionSize);

        // Union size is now the size of the first set plus remaining words in the second set
        int unionSize = set1.size() + set2.size();
        System.out.println("unionSize: " + unionSize);

        // Calculate and return fuzzy Jaccard similarity
        return (double) intersectionSize / unionSize;
    }

    // Get adjusted fuzziness threshold, so it does not apply to short words
    public static double getAdjustedFuzzinessThreshold(String word1, String word2, double fuzzinessThreshold) {
        double adjustedFuzzinessThreshold = fuzzinessThreshold;
        if (word1.length() < MINIMUM_WORD_LENGTH_TO_APPLY_FUZZINESS
            || word2.length() < MINIMUM_WORD_LENGTH_TO_APPLY_FUZZINESS) {
            adjustedFuzzinessThreshold = 0;
        }
        return adjustedFuzzinessThreshold;
    }

    // Method to calculate fuzzy intersection based on Levenshtein distance
    private static int fuzzyIntersection(Set<String> set1, Set<String> set2, double fuzzinessThreshold) {
        System.out.println("in");
        System.out.println(set1);
        System.out.println(set2);
        int count = 0;
        Set<String> toRemove = new HashSet<>(); // Track words to remove from set2 after fuzzy match

        for (String word1 : set1) {
            for (String word2 : set2) {
                System.out.println("checking word1: " + word1 + " word2: " + word2);
                double adjustedFuzzinessThreshold = getAdjustedFuzzinessThreshold(word1, word2, fuzzinessThreshold);
                // Use Levenshtein distance to check similarity
                if (levenshteinDistance.apply(word1, word2) <= adjustedFuzzinessThreshold) {
                    count++;
                    toRemove.add(word2);  // Mark for removal to avoid duplicate counting
                    break;
                }
            }
        }
        set2.removeAll(toRemove);  // Remove fuzzy-matched words from set2
        System.out.println("out: " + count);
        System.out.println(set1);
        System.out.println(set2);
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
