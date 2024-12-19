package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link SuggestionsFinder} interface.
 * This class provides a search strategy that prioritizes matches in rules and then tries to find good
 * matches in ontologies.
 */

@Component
class DefaultSuggestionsFinder implements SuggestionsFinder {

    private final RulesSearcher rulesSearcher;
    private final OntologiesSearcher ontologiesSearcher;

    DefaultSuggestionsFinder(RulesSearcher rulesSearcher, OntologiesSearcher ontologiesSearcher) {
        this.rulesSearcher = rulesSearcher;
        this.ontologiesSearcher = ontologiesSearcher;
    }

    /**
     * Retrieves a list of suggestions for a given entity based on the provided configuration.
     * This implementation uses a 4-step search strategy to find matches:
     *  - Search already existing rules (exact match)
     *  - Search similar rules (fuzzy match)
     *  - Search ontologies  (exact match: label or synonyms)
     *  - Search similar ontologies (fuzzy match: label or synonyms)
     *
     * @param entity            the source entity to be mapped
     * @param indexPath         the path of the index to use for the mapping
     * @param maxNumSuggestions the maximum number of suggestions to retrieve
     * @param config            information about how to build the queries to find matches
     * @return a list of suggestions for the source entity
     * @throws IOException if an I/O error occurs while reading the index
     */
    @Override
    public List<Suggestion> findSuggestions(SourceEntity entity,
        String indexPath,
        int maxNumSuggestions,
        MappingConfiguration config) throws IOException {

        boolean done = false;

        List<Suggestion> suggestions = new ArrayList<>();

        // Check if there are enough exact matches in rules
        done = collectResults(
            suggestions, rulesSearcher.findExactMatchingRules(entity, indexPath, config), maxNumSuggestions);

        // Check if there are enough similar matches in rules
        if (!done) {
            done = collectResults(
                suggestions, rulesSearcher.findSimilarRules(entity, indexPath, config), maxNumSuggestions);
        }

        // Check if there are enough exact matches in ontologies
        if (!done) {
            done = collectResults(
                suggestions, ontologiesSearcher.findExactMatchingOntologies(entity, indexPath, config),
                maxNumSuggestions);
        }

        // Check if there are enough similar matches in ontologies
        if (!done) {
            collectResults(
                suggestions, ontologiesSearcher.findSimilarMatchingOntologies(entity, indexPath, config),
                maxNumSuggestions);
        }
        // Suggestions need to be sorted (descending order) by 'score'
        return SuggestionsSorter.sortSuggestionsByScoreDesc(suggestions);

    }

    /**
     * Adds obtained suggestions with a specific method to the total of found suggestions.
     * Stops if the wanted number of results is reached, and returns true in order that we don't keep searching
     * for more matches
     */
    private boolean collectResults(List<Suggestion> all, List<Suggestion> newResults, int wanted) {
        boolean done = false;
        int found = all.size();

        if (!newResults.isEmpty()) {
            for (Suggestion suggestion : newResults) {
                // Only add new suggestions
                if (!all.contains(suggestion)) {
                    all.add(suggestion);
                    found++;
                    done = found == wanted;
                    if (done) {
                        break;
                    }
                }
            }
        }
        return done;
    }
}
