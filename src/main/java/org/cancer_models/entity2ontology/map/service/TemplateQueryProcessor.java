package org.cancer_models.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancer_models.entity2ontology.common.utils.MapUtils;
import org.cancer_models.entity2ontology.map.model.QueryTemplate;
import org.cancer_models.entity2ontology.map.model.SearchQueryItem;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that manipulates templates. A template is a string with placeholders
 * which guide the creation of different phrases to use in Lucene searcher.
 * The main usage of this class is to process a template and return a list of {@link SearchQueryItem},
 * which will contain the information needed to create a query in Lucene.
 */
@Component
public class TemplateQueryProcessor {

    private static final Logger logger = LogManager.getLogger(TemplateQueryProcessor.class);

    /**
     * Extracts search query items from a given template and a source entity, using a map of weights.
     * The template is expected to have the format "${key1} ${key1} ${keyN}".
     * <p>
     * This method processes a template string with placeholders (e.g., "${field}") and extracts the keys
     * from the placeholders. It then retrieves the corresponding values from the provided {@link SourceEntity}
     * and the corresponding weights from the weights map. A list of {@link SearchQueryItem} objects is built
     * using the keys, values, and weights.
     * </p>
     *
     * @param template the template string containing placeholders for the query keys; must not be null or empty
     * @param entity the source entity that contains the data map from which the values are retrieved; must not be null
     * @param weightsMap a map containing weights for each query key; must not be null
     * @return a list of {@link SearchQueryItem} objects constructed from the extracted keys, their values, and their weights
     * @throws IllegalArgumentException if the template is invalid, a key is missing from the source entity's data map,
     *                                  or a key is missing from the weights map
     */
    public List<SearchQueryItem> extractSearchQueryItems(
        QueryTemplate template, SourceEntity entity, Map<String, Double> weightsMap ) {
        List<SearchQueryItem> searchQueryItems = new ArrayList<>();

        try {
            validateInput(template, entity, weightsMap);

            List<String> keys = template.extractKeys();

            for (String key : keys) {
                String value = MapUtils.getValueOrThrow(entity.getData(), key, "entity values");
                double weight = MapUtils.getValueOrThrow(weightsMap, key, "weights");
                SearchQueryItem searchQueryItem = SearchQueryItem.builder()
                    .field(key)
                    .value(value)
                    .weight(weight)
                    .build();
                searchQueryItems.add(searchQueryItem);
            }

        } catch (IllegalArgumentException exception) {
            String error = "Error when extracting search query items from template '" + template + "'";
            logger.error(error);
            logger.error(exception);
            throw new IllegalArgumentException(exception.getMessage());
        }
        searchQueryItems = removeOverlappingTerms(searchQueryItems);

        return searchQueryItems;
    }

    /**
     * Returns a new list of {@link SearchQueryItem} where repeated words, the same word in different terms, are removed.
     * This helps to build queries that make more sense. So instead of having, for instance, a (simplified) query like
     * "recurrent lung lung carcinona", we get "recurrent carcinoma".
     *
     * if a word is present in more than one item/term, the one to keep is the one in the term with more weight.
     *
     * The number of terms in the final list could be less than the original one, if all the words in an item are also
     * in other items with more weight.
     *
     * @param searchQueryItems Original list of {@link SearchQueryItem}.
     * @return The new list of {@link SearchQueryItem} with removed repetition of words.
     */
    private List<SearchQueryItem> removeOverlappingTerms(List<SearchQueryItem> searchQueryItems) {
        List<SearchQueryItem> cleanedSearchQueryItems = new ArrayList<>();
        Map<SearchQueryItem, String[]> wordsByItem = new HashMap<>();

        // Indicates in which items a word is
        Map<String, SearchQueryItem> highestWeightItemByWords = new HashMap<>();

        // Get the words that compose each item
        for (SearchQueryItem searchQueryItem : searchQueryItems) {
            wordsByItem.put(searchQueryItem, searchQueryItem.getValue().toLowerCase().trim().split(" "));
        }

        // Find in which terms each word appears
        wordsByItem.forEach((searchQueryItem, words) -> {
            for (String word : words) {
                if (!highestWeightItemByWords.containsKey(word)) {
                    highestWeightItemByWords.put(word, searchQueryItem);
                } else {
                    // Update if new item has a higher weight
                    SearchQueryItem current = highestWeightItemByWords.get(word);
                    if (searchQueryItem.getWeight() > current.getWeight()) {
                        highestWeightItemByWords.put(word, searchQueryItem);
                    }
                }
            }
        });

        // Rebuild the list of items but removing repeated words. This could lead to have fewer items than at the beginning
        for (SearchQueryItem searchQueryItem : searchQueryItems) {
            StringBuilder newValueBuilder = new StringBuilder();
            // Analyze each word in the item. If it appears in more than one term, leave only the one with
            // the greatest weight.
            String[] words = wordsByItem.get(searchQueryItem);

            for (String word : words) {
                SearchQueryItem highestWeight = highestWeightItemByWords.get(word);
                // Keep the word only if this term is the one with the highest weight
                if (highestWeight == searchQueryItem) {
                    newValueBuilder.append(" ").append(word.trim());
                }
            }

            // Keep the item only if there were words left after the cleaning
            if (!newValueBuilder.isEmpty()) {
                String newValue = newValueBuilder.toString().trim();
                searchQueryItem.setValue(newValue);
                cleanedSearchQueryItems.add(searchQueryItem);
            }
        }
        return cleanedSearchQueryItems;
    }

    private static void validateInput(QueryTemplate template, SourceEntity entity, Map<String, Double> weightsMap) {
        if (template == null) {
            throw new IllegalArgumentException("Template cannot be null");
        }
        if (entity.getData() == null) {
            throw new IllegalArgumentException("Source entity data cannot be null");
        }
        if (entity.getData().isEmpty()) {
            throw new IllegalArgumentException("Source entity data cannot be empty");
        }
        if (weightsMap == null) {
            throw new IllegalArgumentException("Weights map cannot be null");
        }
        if (weightsMap.isEmpty()) {
            throw new IllegalArgumentException("Weights map cannot be empty");
        }
    }

    /**
     * Translates a list of SearchQueryItem into an approximation of the phrase used in a template based query.
     * It is useful to compute the similarity of the obtained suggestions.
     * @param items A list of {@link SearchQueryItem} derived from a template.
     * @return The phrase representing the query
     */
    public String convertItemListToPhrase(List<SearchQueryItem> items) {
        return items.stream().map(SearchQueryItem::getValue).collect(Collectors.joining(" "));
    }
}
