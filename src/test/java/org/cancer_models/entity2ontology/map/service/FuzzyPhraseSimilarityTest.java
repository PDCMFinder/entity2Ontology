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
}