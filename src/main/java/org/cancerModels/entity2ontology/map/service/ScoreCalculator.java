package org.cancerModels.entity2ontology.map.service;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * A class that helps with the calculation of the score for a {@link Suggestion}. The score is a percentage that
 * informs about how close a suggestion is respect to the {@link SourceEntity} for which it was created.
 * <p>
 * The similarity between both is calculated using a string similarity algorithm.
 */
@Component
public class ScoreCalculator {

    // The similarity between two strings will be calculated with the Levenshtein distance algorithm.
    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    /**
     * Calculates the suggestion score as a percentage, based on how similar the suggestion and the sourceEntity are.
     * A string similarity comparison is used.
     * Note that {@link Suggestion} has a `rawScore` assigned by Lucene. We are not using it as that's a value that helps
     * in sorting results but doesn't say anything about how good the result by its own is.
     *
     * @param suggestion   The suggestion for the mapping
     * @param sourceEntity The entity we are trying to map
     * @return A number (percentage) representing how similar the suggestion and the source entity are.
     */
    public double calculateScoreAsPercentage(
        Suggestion suggestion,
        SourceEntity sourceEntity,
        Map<String, Double> fieldsWeights) {

        double score = 0.0;

        double totalWeight = fieldsWeights.values().stream().reduce(0.0, Double::sum);

//        TODO: Note, this only make sense when comparing rules with rules

        for (Map.Entry<String, Double> entry : fieldsWeights.entrySet()) {
            String fieldValueSourceEntity = sourceEntity.getData().get(entry.getKey());
            String fieldValueSuggestion = suggestion.getTargetEntity().getData().get(entry.getKey()).toString();
            double scorePerField = calculateScorePerField(
                fieldValueSourceEntity, fieldValueSuggestion, entry.getValue(), totalWeight);
            score += scorePerField;
            System.out.println("scorePerField (" + entry.getKey() + ") ==> " + scorePerField);
        }
        return score;
    }

    double calculateScorePerField(
        String sourceEntityFieldValue, String suggestionFieldValue, double fieldWeight, double totalWeight) {

        // Calculate the similarity between the value of the source entity field vs the one in the suggestion (0 - 100).
        double stringsSimilarityPercentage = calculateStringsSimilarityPercentage(
            sourceEntityFieldValue, suggestionFieldValue);
        System.out.println(
            "stringsSimilarityPercentage between [" + sourceEntityFieldValue + "] and [" + suggestionFieldValue + "] : "
                + stringsSimilarityPercentage);

        // Calculate how important the field is in the global calculation of the score
        double fieldRelevance = fieldWeight * 100 / totalWeight;

        // Get the field score by multiplying the similarity percentage and the relevance of the field
        return stringsSimilarityPercentage * fieldRelevance;
    }

    /**
     * Calculate the similarity percentage (0 to 1) between 2 strings `a` and `b`.
     * A value of 1 indicates the strings are the identical. The lower the value, the more different the strings are.
     *
     * @param a The first string to compare.
     * @param b The second string to compare.
     * @return A value between 0 and 1 representing how similar the strings are.
     */
    double calculateStringsSimilarityPercentage(String a, String b) {
        double similarity = 0.0;

        if (a == null || b == null) {
            similarity = 0.0;
        } else if (a.equalsIgnoreCase(b)) {
            similarity = 1;
        } else {
            double maxDistancePossible = Math.max(a.length(), b.length());
            int distanceValue = levenshteinDistance.apply(a, b);
            similarity = 1 - (distanceValue / maxDistancePossible);
        }
        return similarity;
    }
}

