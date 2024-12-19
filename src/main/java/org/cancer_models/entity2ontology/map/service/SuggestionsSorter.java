package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.map.model.Suggestion;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class SuggestionsSorter {

    // Suppress default constructor for non-instantiability
    private SuggestionsSorter() {
        throw new AssertionError();
    }

    /**
     * Sorts a list of suggestions using the {@code score} attribute, in descendent order.
     * @param suggestions List of suggestions
     * @return Sorted list of suggestions
     */
    public static List<Suggestion> sortSuggestionsByScoreDesc(List<Suggestion> suggestions) {
        return suggestions.stream()
            .sorted(Comparator.comparingDouble(Suggestion::getScore).reversed())
            .collect(Collectors.toList());
    }
}
