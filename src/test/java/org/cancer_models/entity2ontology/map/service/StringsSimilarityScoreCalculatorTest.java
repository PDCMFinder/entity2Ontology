package org.cancer_models.entity2ontology.map.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringsSimilarityScoreCalculatorTest {

    @Test
    void testCalculateSimilarityScore_shortWords_same() {
        String text1 = "w_a";
        String text2 = "w_a";
        double score = StringsSimilarityScoreCalculator.calculateSimilarityScore(text1, text2);

        assertEquals(1, score, "Both texts are equal, so score should be 1");
    }

    @Test
    void testCalculateSimilarityScore_shortWords_different() {
        String text1 = "w_a";
        String text2 = "w_b";
        double score =
            StringsSimilarityScoreCalculator.calculateSimilarityScore(text1, text2);

        assertNotEquals(
            1,
            score,
            "Both texts are different, so score cannot be 1");
    }

    @Test
    void testCalculateSimilarityScore_phrases_same() {
        String text1 = "w_a w_b";
        String text2 = "w_a w_b";
        double score = StringsSimilarityScoreCalculator.calculateSimilarityScore(text1, text2);

        assertEquals(1, score, "Both phrases are equal, so score should be 1");
    }

    @Test
    void testCalculateSimilarityScore_phrases_equivalent() {
        String text1 = "w_a w_b";
        String text2 = "w_b w_a";
        double score = StringsSimilarityScoreCalculator.calculateSimilarityScore(text1, text2);

        assertEquals(1, score, "Both phrases are equivalent, so score should be 1");
    }
}
