package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.common.model.OntologyEntityDataFieldName;
import org.cancer_models.entity2ontology.common.model.TargetEntityDataFields;
import org.cancer_models.entity2ontology.map.model.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
class DefaultSuggestionScoreCalculator implements SuggestionScoreCalculator {

    // List of words to skip when converting text to sets
    private static final List<String> STOP_WORDS = Arrays.asList("in", "on", "the", "of", "is", "at", "by", "the");

    // If a search query item contains one of these words, it can be ignored
    private static final List<String> NON_MEANINGFUL_WORDS = List.of("unknown");

    // A pattern to help when splitting text to words
    private static final String WORDS_SEPARATOR_REGEXP = "[\\s/\\-]+";

    private static final double SIMILARITY_THRESHOLD = 50;

    // Maximum score possible (percentage)
    private static final double MAX_SCORE = 100;

    // This parameter allows to control the score for matches in synonyms, giving it a slightly lower value than a label
    // match
    private static final double SYNONYM_MATCH_MULTIPLIER = 0.99;

    // Method to filter out stop words
    private static List<String> filterStopWords(String[] words) {
        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (!STOP_WORDS.contains(word)) {
                filteredWords.add(word);
            }
        }
        return filteredWords;
    }

    private static List<String> textToList(String text) {
        return new ArrayList<>(filterStopWords(text.toLowerCase().split(WORDS_SEPARATOR_REGEXP)));
    }

    /**
     * Calculates the score of a {@code suggestion} based on how similar it is respect to a {@code sourceEntity}.
     * The object {@code configuration} is used to provide additional information like the
     * relevance of fields.
     *
     * @param suggestion    The suggestion found in the mapping process
     * @param sourceEntity  The entity for which the suggestion was found
     * @param configuration A configuration object with additional information about the mapping process
     * @return a number between 0 and 100 indicating how good the suggestion is.
     */
//    @Override
//    public double computeScore(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration) {
//        double score = 0;
//        Objects.requireNonNull(suggestion);
//        Objects.requireNonNull(sourceEntity);
//        Objects.requireNonNull(configuration);
//
//        TargetEntityType targetEntityType = suggestion.getTargetEntity().targetType();
//
//        if (targetEntityType.equals(TargetEntityType.RULE)) {
//            score = computeScoreRule(suggestion, sourceEntity, configuration);
//        } else {
//            score = computeScoreOntology(suggestion);
//        }
//        return score;
//    }

    /**
     * Calculates the suggestion (a rule) score as a percentage, based on how similar the suggestion and the sourceEntity are.
     * A string similarity comparison is used.
     * Note that {@link Suggestion} has a `rawScore` assigned by Lucene. We are not using it as that's a value that helps
     * in sorting results but doesn't say anything about how good the result by its own is.
     *
     * @param suggestion    The suggestion for the mapping
     * @param sourceEntity  The entity we are trying to map
     * @param configuration A configuration object with additional information about the mapping process
     * @return A number (percentage) representing how similar the suggestion and the source entity are.
     */
    @Override
    public double computeScoreRule(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration) {
        double score = 0.0;

        var fieldsWeights = configuration.getFieldsWeightsByEntityType(sourceEntity.getType());

        double totalWeight = fieldsWeights.values().stream().reduce(0.0, Double::sum);

        for (Map.Entry<String, Double> entry : fieldsWeights.entrySet()) {
            String fieldValueSourceEntity = sourceEntity.getData().get(entry.getKey());
            String fieldValueSuggestion = suggestion.getTargetEntity().dataFields().getStringField(entry.getKey());
            double scorePerField = calculateScorePerField(
                fieldValueSourceEntity, fieldValueSuggestion, entry.getValue(), totalWeight);
            score += scorePerField;
        }

        return score;
    }

    private double calculateScorePerField(
        String sourceEntityFieldValue, String suggestionFieldValue, double fieldWeight, double totalWeight) {

        // Calculate the similarity between the value of the source entity field vs the one in the suggestion (0 - 100).
        double stringsSimilarityPercentage = calculateScoreWeightedItems(sourceEntityFieldValue, suggestionFieldValue);

        // A field contributes only if its similarity with the value is greater than a specified threshold
        if (stringsSimilarityPercentage < SIMILARITY_THRESHOLD) {
            return 0;
        }

        // Calculate how important the field is in the global calculation of the score
        double fieldRelevance = fieldWeight / totalWeight;

        // Get the field score by multiplying the similarity percentage and the relevance of the field
        return stringsSimilarityPercentage * fieldRelevance;
    }

    /**
     * Calculates the suggestion (an ontology term) score as a percentage, based on how similar the suggestion and the
     * sourceEntity are.
     * A string similarity comparison is used.
     * Note that {@link Suggestion} has a `rawScore` assigned by Lucene. We are not using it as that's a value that helps
     * in sorting results but doesn't say anything about how good the result by its own is.
     * @param suggestion  The suggestion for the mapping
     * @return A number (percentage) representing how similar the suggestion and the source entity are.
     */
    @Override
    public double computeScoreOntology(Suggestion suggestion) {
        double score;

        List<SearchQueryItem> items = getCleanedItemsFromSuggestion(suggestion);
        List<String> itemsTexts = new ArrayList<>();
        List<Double> itemsWeights = new ArrayList<>();
        TargetEntityDataFields dataFields = suggestion.getTargetEntity().dataFields();
        items.forEach(i -> {
            itemsTexts.add(i.getValue());
            itemsWeights.add(i.getWeight());
        });

        String mappingDetailNote = "";
        String suggestionLabel = suggestion.getTermLabel();
        List<String> suggestionSynonyms = new ArrayList<>();
        if (dataFields.hasListField(OntologyEntityDataFieldName.SYNONYMS.getValue())) {
            suggestionSynonyms = dataFields.getListField(OntologyEntityDataFieldName.SYNONYMS.getValue());
        }

        double highestScore;

        // First we check the score of the label
        double labelScore = calculateScoreWeightedItems(itemsTexts, itemsWeights, suggestionLabel);

        highestScore = labelScore;
        mappingDetailNote = "Matched label:[" + suggestionLabel + "]";

        // If needed, search for a good score in the synonyms
        if (labelScore < MAX_SCORE && !suggestionSynonyms.isEmpty()) {

            for (String synonym : suggestionSynonyms) {
                double synonymScore = calculateScoreWeightedItems(itemsTexts, itemsWeights, synonym);
                synonymScore *= SYNONYM_MATCH_MULTIPLIER;
                if (synonymScore > highestScore) {
                    mappingDetailNote = "Matched synonym:[" + synonym + "]";
                    highestScore = synonymScore;
                }
            }
        }
        suggestion.getScoringDetails().setNote(mappingDetailNote);
        score = highestScore;
        return score;
    }

    private List<SearchQueryItem> getCleanedItemsFromSuggestion(Suggestion suggestion) {
        List<SearchQueryItem> items = null;
        ScoringDetails scoringDetails = suggestion.getScoringDetails();
        if (scoringDetails != null) {
            items = scoringDetails.getSearchQueryItems();
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Error obtaining mapping details from suggestion: " + suggestion);
        }
        return cleanSearchQueryItems(suggestion.getScoringDetails().getSearchQueryItems());
    }

    private double calculateScoreWeightedItems(String itemsText, String targetText) {
        List<String> itemsTexts = new ArrayList<>();
        itemsTexts.add(itemsText);
        List<Double> itemWeightedTexts = new ArrayList<>();
        itemWeightedTexts.add(1.0);
        return calculateScoreWeightedItems(itemsTexts, itemWeightedTexts, targetText);
    }

    private double calculateScoreWeightedItems(List<String> itemsTexts, List<Double> itemsWeights, String targetText) {
        double score = 0;
        if (itemsTexts.size() != itemsWeights.size()) {
            throw new IllegalArgumentException("Error calculating mapping score: number of weights does not" +
                "match number of items");
        }

        if (targetText == null || targetText.isEmpty()) {
            throw new IllegalArgumentException("Error calculating mapping score: target text is null or empty");
        }

        List<String> targetWords = textToList(targetText);

        int initialTargetWordsSize = targetWords.stream().mapToInt(String::length).sum();

        List<String> remainingTargetWords = new ArrayList<>();

        // The total weight of the items
        double totalWeight = itemsWeights.stream().reduce(0.0, Double::sum);

        for (int i = 0; i < itemsTexts.size(); i++) {
            String itemText = itemsTexts.get(i);
            List<String> itemWords = textToList(itemText);

            double weight = itemsWeights.get(i);
            double itemRelevance = weight / totalWeight;
            ItemScoreResult result = calculateItemScore(itemWords, targetWords);
            score += result.score * itemRelevance;
            remainingTargetWords = result.remainingTargetWords;
            targetWords = remainingTargetWords;
        }

        // Calculate the penalty: how much of the target words were not matched
        int remainingTargetTextSize = remainingTargetWords.stream().mapToInt(String::length).sum();
        double penalty = (double) (remainingTargetTextSize * 100) / initialTargetWordsSize;
        if (penalty < score) {
            score -= penalty;
        }
        return score;
    }

    private ItemScoreResult calculateItemScore(List<String> itemWords, List<String> targetWords) {
        double itemScore = 0;
        Set<String> matchedTargetWords = new HashSet<>();
        List<String> remainingTargetWords = new ArrayList<>(targetWords);
        int itemWordsContentSize = itemWords.stream().mapToInt(String::length).sum();

        for (String element : itemWords) {
            for (String targetWord : targetWords) {
                double similarity = StringsSimilarityScoreCalculator.calculateSimilarityScore(element, targetWord);

                if (similarity > 0.8) {
                    matchedTargetWords.add(targetWord);
                    double itemRelevanceInPhrase = (double) (element.length()) / itemWordsContentSize;
                    itemScore += similarity * itemRelevanceInPhrase * 100;
                    break;
                }
            }
        }
        remainingTargetWords.removeAll(matchedTargetWords);
        return new ItemScoreResult(itemScore, remainingTargetWords);
    }

    // Return a copy of searchQueryItems without overlapping information or non-meaningful words
    private List<SearchQueryItem> cleanSearchQueryItems(List<SearchQueryItem> searchQueryItems) {
        List<SearchQueryItem> items = SearchQueryItemUtil.removeNonMeaningItems(searchQueryItems, NON_MEANINGFUL_WORDS);
        items = SearchQueryItemUtil.removeOverlappingTerms(items);
        return items;
    }

    // A record to help in intermediate calculations of score
    private record ItemScoreResult(double score, List<String> remainingTargetWords) {
    }
}
