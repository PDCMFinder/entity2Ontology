package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.map.model.Suggestion;

import java.util.List;

public class SuggestionCollector {

    // Suppress default constructor for non-instantiability
    private SuggestionCollector() {
        throw new AssertionError();
    }

    /**
     * Adds a list of additional suggestions to the existing list of current suggestions,
     * stopping early if the maximum number of suggestions has been reached.
     * <p>
     * Only suggestions with a score equal to or greater than the specified minimum score
     * are considered. Suggestions are added in the order they appear in the additional list.
     * If the maximum number of allowed suggestions is reached during the process,
     * the method returns {@code true} to signal that no further suggestions should be collected.
     * Otherwise, it returns {@code false}.
     *
     * @param currentSuggestions the list to which valid suggestions will be added
     * @param additionalSuggestions the new suggestions to evaluate and potentially add
     * @param maxSuggestions the maximum total number of suggestions desired
     * @param minimumScore the minimum score a suggestion must meet to be considered
     * @return {@code true} if the maximum number of suggestions has been reached; {@code false} otherwise
     */
    static boolean addSuggestionsUntilLimitReached(
        List<Suggestion> currentSuggestions,
        List<Suggestion> additionalSuggestions,
        int maxSuggestions,
        double minimumScore) {
        boolean done = false;
        int found = currentSuggestions.size();

        if (!additionalSuggestions.isEmpty()) {
            for (Suggestion suggestion : additionalSuggestions) {
                double score = suggestion.getScore();
                if (!currentSuggestions.contains(suggestion) && score >= minimumScore) {
                    currentSuggestions.add(suggestion);
                    found++;
                    done = found == maxSuggestions;
                    if (done) {
                        break;
                    }
                }
            }
        }
        return done;
    }
}
