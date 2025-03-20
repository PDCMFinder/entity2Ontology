package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.map.model.SearchQueryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class to execute tasks, especially on collections of {@code SearchQueryItem} such as
 * cleaning data, creating copies without redundant data, ect.
 */
public class SearchQueryItemUtil {

    // Suppress default constructor for non-instantiability
    private SearchQueryItemUtil() {
        throw new AssertionError();
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
    static List<SearchQueryItem> removeOverlappingTerms(List<SearchQueryItem> searchQueryItems) {
        List<SearchQueryItem> cleanedSearchQueryItems = new ArrayList<>();
        Map<SearchQueryItem, String[]> wordsByItem = new HashMap<>();

        // Get the words that compose each item
        for (SearchQueryItem searchQueryItem : searchQueryItems) {
            wordsByItem.put(searchQueryItem, searchQueryItem.getValue().toLowerCase().trim().split(" "));
        }

        // Find in which terms each word appears
        Map<String, SearchQueryItem> highestWeightItemByWords = findHighestWeightItemByWords(wordsByItem);

        // Rebuild the list of items but removing repeated words. This could lead to have fewer items than at the beginning
        for (SearchQueryItem searchQueryItem : searchQueryItems) {
            processWordsByItem(searchQueryItem, wordsByItem, highestWeightItemByWords, cleanedSearchQueryItems);
        }
        return cleanedSearchQueryItems;
    }

    /**
     * Return a new collection of {@code SearchQueryItem} where items with unmeaning words are removed.
     * A typical case is a term with the text "unknown".
     * @param searchQueryItems Original list of {@code SearchQueryItem}
     * @param nonMeaningWords List of words that are not meaningful in the mapping process
     * @return A new list of {@code SearchQueryItem} where terms with non-meaningful content are removed
     */
    public static List<SearchQueryItem> removeNonMeaningItems(
        List<SearchQueryItem> searchQueryItems, List<String> nonMeaningWords) {
        List<SearchQueryItem> cleanedSearchQueryItems = new ArrayList<>();
        for (SearchQueryItem searchQueryItem : searchQueryItems) {
            if (!nonMeaningWords.contains(searchQueryItem.getValue())) {
                cleanedSearchQueryItems.add(searchQueryItem);
            }
        }
        return cleanedSearchQueryItems;
    }

    // Finds the SearchQueryItem with the highest score where each word is present.
    private static Map<String, SearchQueryItem> findHighestWeightItemByWords(Map<SearchQueryItem, String[]> wordsByItem) {
        Map<String, SearchQueryItem> highestWeightItemByWords = new HashMap<>();
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
        return highestWeightItemByWords;
    }

    // Rebuilds a list of {@code SearchQueryItem} by ignoring repeated words
    private static void processWordsByItem(
        SearchQueryItem searchQueryItem,
        Map<SearchQueryItem, String[]> wordsByItem,
        Map<String, SearchQueryItem> highestWeightItemByWords,
        List<SearchQueryItem> cleanedSearchQueryItems) {

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

}
