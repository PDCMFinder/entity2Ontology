package org.cancerModels.entity2ontology.map.service;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.cancerModels.entity2ontology.common.utils.GeneralUtils;
import org.cancerModels.entity2ontology.common.utils.MapUtils;
import org.cancerModels.entity2ontology.map.model.SearchQueryItem;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            System.out.println("maxDistancePossible: " + maxDistancePossible);
            System.out.println("distanceValue: " + distanceValue);
            similarity = 1 - (distanceValue / maxDistancePossible);
        }
        return similarity;
    }

    /**
     * Calculates the score of a {@link Suggestion} that represents an ontology.
     * @param searchQueryItems List of {@link SearchQueryItem} representing each term in the query (field, value, weight).
     * @param suggestion A {@link Suggestion} which contains an ontology term. Returned by the searcher after executing a query based on `searchQueryItems`.
     * @return A number representing the percentage of similarity for the suggestion. 100 represents a perfect suggestion.
     */

    /**
     * Calculates the similarity score for a {@link Suggestion} that represents an ontology term. It compares the
     * string representation of `searchQueryItems` against the label and synonyms in the suggestion. The highest
     * score is returned.
     *
     * @param searchQueryItems List of {@link SearchQueryItem} objects, where each item represents a field, value,
     *                         and weight from the query. These are used to assess the relevance of the suggestion.
     * @param suggestion A {@link Suggestion} that contains the ontology term returned by the searcher. This term is evaluated
     *                   against a string representation `searchQueryItems` to determine how well it matches.
     * @return A score between 0 and 100, where 100 represents a perfect match between the {@link Suggestion} and
     *         the search query items, and 0 represents no similarity.
     */
    public double calculateScoreInOntologySuggestion(List<SearchQueryItem> searchQueryItems, Suggestion suggestion) {
        // To calculate the score of the suggestions, we need to compare them with an approximation of the
        // "phrase" that was used in the query. As it was actually a list of values, we will replicate
        // the phrase by concatenating the keys from the template
        String phrase = searchQueryItems.stream().map(SearchQueryItem::getValue).collect(Collectors.joining(" "));



        int fuzzinessThreshold = 2;

        double highestScore = 0;

        // Get the label from the data map
        String label = MapUtils.getValueOrThrow(
            suggestion.getTargetEntity().getData(), "label", "suggestion data").toString();

        System.out.println("phrase: [" + phrase + "]");
        System.out.println("label:  [" + label + "]");

        List<String> synonyms = GeneralUtils.castList( MapUtils.getValueOrThrow(
            suggestion.getTargetEntity().getData(), "synonyms", "suggestion data"), String.class);

        // First let's calculate the similarity between `phrase` and the ontology label
        double labelScore = FuzzyPhraseSimilarity.fuzzyJaccardSimilarity(phrase, label, fuzzinessThreshold);
        highestScore = labelScore;

        // Only look for similarity in synonyms if there is need to
        if (highestScore < 1.0) {
            if (!synonyms.isEmpty()) {
                for (String synonym : synonyms) {
                    double synonymScore = FuzzyPhraseSimilarity.fuzzyJaccardSimilarity(phrase, synonym, fuzzinessThreshold);
                    if (synonymScore > highestScore) {
                        highestScore = synonymScore;
                    }
                }
            }
        }



        return highestScore * 100;
    }
}

