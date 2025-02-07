package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.common.model.OntologyEntityDataFieldName;
import org.cancer_models.entity2ontology.common.model.TargetEntityDataFields;
import org.cancer_models.entity2ontology.common.model.TargetEntityType;
import org.cancer_models.entity2ontology.map.model.*;

import java.util.*;

public class DefaultSuggestionScoreCalculator implements SuggestionScoreCalculator {

    // List of words to skip when converting text to sets
    private static final List<String> STOP_WORDS = Arrays.asList("in", "on", "the", "of", "is", "at", "by", "the");
    // If a search query item contains one of these words, it can be ignored
    private static final List<String> NON_MEANINGFUL_WORDS = List.of("unknown");
    // A pattern to help when splitting text to words
    private static final String WORDS_SEPARATOR_REGEXP = "[\\s/\\-]+";

    private static final double SIMILARITY_THRESHOLD = 50;

    // A record to help in intermediate calculations of score
    private record ItemScoreResult(double score, List<String> remainingTargetWords) {}

    /**
     * Calculates the score of a {@code suggestion} based on how similar it is respect to a {@code sourceEntity},
     * when the suggestion was obtained using an exact match (so it's more "strict" when
     * evaluating the similarity). {@code configuration} is used to provide additional information like the
     * relevance of fields.
     *
     * @param suggestion    The suggestion found in the mapping process
     * @param sourceEntity  The entity for which the suggestion was found
     * @param configuration A configuration object with additional information about the mapping process
     * @return a number between 0 and 100 indicating how good the suggestion is.
     */
    @Override
    public double computeScoreExactMatch(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration) {
        double score = 0;
        Objects.requireNonNull(suggestion);
        Objects.requireNonNull(sourceEntity);
        Objects.requireNonNull(configuration);
        if (suggestion.getTargetEntity().targetType().equals(TargetEntityType.RULE)) {
            score = computeScoreRule(suggestion, sourceEntity, configuration);
        } else {
            score = computeScoreOntology(suggestion, sourceEntity, configuration);
        }
        return score;
    }

    /**
     * Calculates the suggestion (a rule) score as a percentage, based on how similar the suggestion and the sourceEntity are.
     * A string similarity comparison is used.
     * Note that {@link Suggestion} has a `rawScore` assigned by Lucene. We are not using it as that's a value that helps
     * in sorting results but doesn't say anything about how good the result by its own is.
     *
     * @param suggestion   The suggestion for the mapping
     * @param sourceEntity The entity we are trying to map
     * @param configuration A configuration object with additional information about the mapping process
     * @return A number (percentage) representing how similar the suggestion and the source entity are.
     */
    private double computeScoreRule(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration) {
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
        double stringsSimilarityPercentage = calculateScoreWeightedItems(
            Collections.singletonList(sourceEntityFieldValue), List.of(1.0), suggestionFieldValue);
        System.out.println("NEW stringsSimilarityPercentage:" + stringsSimilarityPercentage);

        if (stringsSimilarityPercentage < SIMILARITY_THRESHOLD) {
            return 0;
        }

        // Calculate how important the field is in the global calculation of the score
        double fieldRelevance = fieldWeight / totalWeight;

        // Get the field score by multiplying the similarity percentage and the relevance of the field
        return stringsSimilarityPercentage * fieldRelevance;
    }

    private double computeScoreOntology(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration) {
        double score = 0.0;

        TargetEntityDataFields dataFields = suggestion.getTargetEntity().dataFields();

        // First we check the score of the label
        String suggestionLabel = suggestion.getTermLabel();

        List<SearchQueryItem> items = null;
        MappingDetails mappingDetails = suggestion.getMappingDetails();
        if (mappingDetails != null) {
            items = mappingDetails.getSearchQueryItems();
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Error obtaining mapping details from suggestion: " + suggestion);
        }
        items = cleanSearchQueryItems(suggestion.getMappingDetails().getSearchQueryItems());
        List<String> itemsTexts = new ArrayList<>();
        List<Double> itemsWeights = new ArrayList<>();
        items.forEach(i -> {
            itemsTexts.add(i.getValue());
            itemsWeights.add(i.getWeight());
        });

        score = calculateScoreWeightedItems(itemsTexts, itemsWeights, suggestionLabel);
        System.out.println("LABEL SCORE:" + score);
        return score;
    }

    /**
     * Calculates the score of a {@code suggestion} based on how similar it is respect to a {@code sourceEntity},
     * when the suggestion was obtained using a similar match (so it's less "strict" when
     * evaluating the similarity). {@code configuration} is used to provide additional information like the
     * relevance of fields.
     *
     * @param suggestion    The suggestion found in the mapping process
     * @param sourceEntity  The entity for which the suggestion was found
     * @param configuration A configuration object with additional information about the mapping process
     * @return a number between 0 and 100 indicating how good the suggestion is.
     */
    @Override
    public double computeScoreSimilarMatch(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration) {
        return 0;
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
    @Override
    public double computeScore(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration) {
        double score = 0;
        Objects.requireNonNull(suggestion);
        Objects.requireNonNull(sourceEntity);
        Objects.requireNonNull(configuration);

        TargetEntityType targetEntityType = suggestion.getTargetEntity().targetType();

        if (targetEntityType.equals(TargetEntityType.RULE)) {
            score = computeScoreRule(suggestion, sourceEntity, configuration);
        } else {
            score = computeScoreOntology(suggestion, sourceEntity, configuration);
        }
        return score;
    }

    /**
     * Calculates the similarity between a collection of {@code SearchQueryItem} and a text, which usually
     * represents the {@code label} or a {@code synonym} on an ontology term.
     * This comparison method uses a combination of ways to determine how similar both elements are:
     * - Literal matches (like text contained in the other one) generate the highest score (per attribute/SearchQueryItem)
     * - When no literal match, a set-based comparison is done. If matches are found, text shrinks
     * - If there are no common words, then LevenshteinDistance is used to see how different the words are
     * @param searchQueryItems List of {@code SearchQueryItem} which were used in the query
     * @param targetText Label or synonym in the ontology term
     * @return a number between 0 and 100 indicating the similarity.
     */
    double calculateSearchQueryItemsAndTextSimilarity(List<SearchQueryItem> searchQueryItems, String targetText) {
        double score = 0.0;
        List<SearchQueryItem> items = cleanSearchQueryItems(searchQueryItems);

        // A collection of all words in the items to use when comparing how much extra information text has
        Set<String> allItemsWords = new HashSet<>();

        // The total weight of the items
        double totalWeight = items.stream().map(SearchQueryItem::getWeight).reduce(0.0, Double::sum);

        // A list version of the targetText to use in comparisons. Using list because order is
        // important when converting back to string
        List<String> targetTextAsList = textToList(targetText);
        // A "cleaned" version of the target text
        String targetTextFormatted = listToText(targetTextAsList);
        int originalTargetTextSize = targetTextFormatted.length();

        for (SearchQueryItem item : items) {
            score += processItem(item, totalWeight, allItemsWords, targetTextAsList);
            System.out.println("Score: " + score);
        }

        targetTextFormatted = listToText(targetTextAsList);

        String allWordsText = listToText(allItemsWords);
        double penalty = (double) (targetTextFormatted.length() * 100) /originalTargetTextSize;

        score -= penalty;
        return score;
    }

    public double processItem(
        SearchQueryItem item,
        double totalWeight,
        Set<String> allItemsWords,
        List<String> targetTextAsList) {

        double itemScore = 0;
        double itemRelevance = item.getWeight() / totalWeight;
        String targetTextFormatted = listToText(targetTextAsList);

        List<String> itemWords = textToList(item.getValue());
        System.out.println("itemWords: " + itemWords);
        allItemsWords.addAll(itemWords);
        String itemTextFormatted = listToText(itemWords);

        Set<String> toRemove = new HashSet<>();

        for (String element : itemWords) {
            for (String targetWord : targetTextAsList) {
                double similarity = StringsSimilarityScoreCalculator.calculateSimilarityScore(element, targetWord);

                if (similarity > 0.8) {
                    toRemove.add(targetWord);
                    double p = similarity*100/itemWords.size();
                    itemScore += p * itemRelevance;
                    break;
                }
            }
        }

        targetTextAsList.removeAll(toRemove);
        return itemScore;
    }

    public double calculateScoreWeightedItems(String itemsText, String targetText) {
        List<String> itemsTexts = new ArrayList<>();
        itemsTexts.add(itemsText);
        List<Double> itemWeightedTexts = new ArrayList<>();
        itemWeightedTexts.add(1.0);
        return calculateScoreWeightedItems(itemsTexts, itemWeightedTexts, targetText);
    }


        public double calculateScoreWeightedItems(List<String> itemsTexts, List<Double> itemsWeights, String targetText) {
        double score = 0;
        if (itemsTexts.size() != itemsWeights.size()) {
            throw new IllegalArgumentException("Error calculating mapping score: number of weights does not" +
                "match number of items");
        }

        List<String> targetWords = textToList(targetText);

        //List<String> allIItemsWords = new ArrayList<>();
        List<String> remainingTargetWords = new ArrayList<>();

        // The total weight of the items
        double totalWeight = itemsWeights.stream().reduce(0.0, Double::sum);

        for (int i = 0; i < itemsTexts.size(); i++) {
            String itemText = itemsTexts.get(i);
            List<String> itemWords = textToList(itemText);


           // allIItemsWords.addAll(itemWords);
            double weight = itemsWeights.get(i);
            double itemRelevance = weight / totalWeight;
            ItemScoreResult result = calculate2(itemWords, targetWords);
            score += result.score * itemRelevance;
            remainingTargetWords.addAll(result.remainingTargetWords);
        }

        if (score > 0) {
            System.out.println("Calculating penalty");
            int remainingTargetTextSize = remainingTargetWords.stream().mapToInt(String::length).sum();
            int targetWordsSize = targetWords.stream().mapToInt(String::length).sum();
            double penalty = (double) (remainingTargetTextSize * 100) /targetWordsSize;
            System.out.println("penalty: " + penalty);
            score -= penalty;
        }

        return score;
    }


    ItemScoreResult calculate2(List<String> itemWords, List<String> targetWords) {
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

    private static String listToText(Collection<String> list) {
        return String.join(" ", list);
    }
}
