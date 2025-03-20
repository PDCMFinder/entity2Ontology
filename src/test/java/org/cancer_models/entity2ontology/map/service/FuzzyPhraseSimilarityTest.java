package org.cancer_models.entity2ontology.map.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuzzyPhraseSimilarityTest {
    @Test
    void testFuzzyJaccardSimilarity_sameData_noFuzziness() {
        String phrase1 = "text1";
        String phrase2 = "text1";
        double similarity = FuzzyPhraseSimilarity.fuzzyJaccardSimilarity(phrase1, phrase2, 0);
        assertEquals(1.0, similarity);
    }

    @Test
    void testFuzzyJaccardSimilarity_similarData_noFuzziness() {
        String phrase1 = "text1";
        String phrase2 = "text2";
        double similarity = FuzzyPhraseSimilarity.fuzzyJaccardSimilarity(phrase1, phrase2, 0);
        assertNotEquals(1.0, similarity);
    }

    @Test
    void testFuzzyJaccardSimilarity_similarDataExtraWord_noFuzziness() {
        String phrase1 = "interferon alpha";
        String phrase2 = "Interferon Alpha a";
        double similarity = FuzzyPhraseSimilarity.fuzzyJaccardSimilarity(phrase1, phrase2, 0);
        assertNotEquals(1.0, similarity);
    }

    @Test
    void testFuzzyJaccardSimilarity_shortWords_withFuzziness() {
        String phrase1 = "d5w";
        String phrase2 = "d53";
        double similarity = FuzzyPhraseSimilarity.fuzzyJaccardSimilarity(phrase1, phrase2, 2);
        System.out.println("similarity "+similarity);
        assertNotEquals(1.0, similarity);
    }

    @Test
    void dummy() {
        String phrase1 = "word_a word_b";
        String phrase2 = "word_a word_c";
        double similarity = FuzzyPhraseSimilarity.fuzzyJaccardSimilarity(phrase1, phrase2, 0);
        System.out.println("similarity "+similarity);
        assertNotEquals(1.0, similarity);
    }
}