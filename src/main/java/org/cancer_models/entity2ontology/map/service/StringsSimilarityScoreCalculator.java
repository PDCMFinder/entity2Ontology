package org.cancer_models.entity2ontology.map.service;

import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * A class that calculates the similarity score (as percentage) between strings, taking into consideration
 * the possibility of allowing small variations (fuzziness) and other conditions like being more strict with shorter
 * text or numeric comparisons.
 */
public class StringsSimilarityScoreCalculator {

    private static final LevenshteinDistance LEVENSHTEIN_DISTANCE = new LevenshteinDistance();

    // Suppress default constructor for non-instantiability
    private StringsSimilarityScoreCalculator() {
        throw new AssertionError();
    }

    /**
     * Calculates a similarity score between 2 strings: {@code textA} and  {@code textB}. The value of
     * {@code fuzziness} determines the allowed variation between the texts (number of characters that can be different).
     * @param textA A string with text to compare
     * @param textB A string with text to compare
     * @return A number representing a similarity percentage
     */
    public static double calculateSimilarityScore(String textA, String textB) {
        double similarityScore = 0;
        if (isAWord(textA) && isAWord(textB)) {
            similarityScore = calculateSimilarityScoreWords(textA, textB);
        } else {
            similarityScore = JaccardSimilarity.calculate(textA, textB);
        }
        return similarityScore;
    }

    /**
     * Calculate the similarity (0 to 1) between 2 words `word1` and `word2`.
     * A value of 1 indicates the strings are the identical. The lower the value, the more different the strings are.
     *
     * @param word1 The first word to compare.
     * @param word2 The second word to compare.
     * @return A value between 0 and 1 representing how similar the words are.
     */
    private static double calculateSimilarityScoreWords(String word1, String word2) {
        double similarity = 0.0;

        if (word1 == null || word2 == null) {
            similarity = 0.0;
        } else if (word1.equalsIgnoreCase(word2)) {
            similarity = 1;
        } else {
            double maxDistancePossible = Math.max(word1.length(), word2.length());
            int distanceValue = LEVENSHTEIN_DISTANCE.apply(word1, word2);
            System.out.println(distanceValue);
            similarity = 1 - (distanceValue / maxDistancePossible);
        }
        return similarity;
    }

    /**
     * Checks if a text contains only a word.
     * @param text Text to check
     * @return true if the text is just a word (as opposed to a "phrase" containing multiple words)
     */
    public static boolean isAWord(String text) {
        return text.split(" ").length == 1;
    }
}
