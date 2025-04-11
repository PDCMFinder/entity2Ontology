package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.exceptions.MappingException;
import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link SuggestionsFinder} interface.
 * This class provides a search strategy that prioritizes matches in rules and then tries to find good
 * matches in ontologies.
 * In this implementation, only suggestions whose score is greater than 50% are considered.
 */

@Component
@Qualifier("defaultSuggestionsFinder")
class DefaultSuggestionsFinder implements SuggestionsFinder {

    private final RulesSearcher rulesSearcher;
    private final OntologiesSearcher ontologiesSearcher;
    private static final double MINIMUM_ACCEPTABLE_SCORE = 50;

    DefaultSuggestionsFinder(RulesSearcher rulesSearcher, OntologiesSearcher ontologiesSearcher) {
        this.rulesSearcher = rulesSearcher;
        this.ontologiesSearcher = ontologiesSearcher;
    }

    /**
     * <p>
     * Retrieves a list of suggestions for a given entity based on the provided configuration.
     * This implementation uses a 4-step search strategy to find matches:
     * <ul>
     *  <li> Search already existing rules (exact match)</li>
     *  <li> Search similar rules (fuzzy match)</li>
     *  <li> Search ontologies  (exact match: label or synonyms)</li>
     *  <li> Search similar ontologies (fuzzy match: label or synonyms)</li>
     *  </ul>
     * </p>
     * <p>
     * Only suggestions with {@code score} equal or greater than 50% are considered as valid results.
     * </p>
     *
     * @param entity            the source entity to be mapped
     * @param indexPath         the path of the index to use for the mapping
     * @param maxNumSuggestions the maximum number of suggestions to retrieve
     * @param config            information about how to build the queries to find matches
     * @return a list of suggestions for the source entity
     * @throws MappingException if an error occurs during the search
     */
    @Override
    public List<Suggestion> findSuggestions(SourceEntity entity,
        String indexPath,
        int maxNumSuggestions,
        MappingConfiguration config) throws MappingException {

        boolean done = false;

        List<Suggestion> suggestions = new ArrayList<>();

        // Check if there are enough exact matches in rules
        done = SuggestionCollector.addSuggestionsUntilLimitReached(
            suggestions,
            rulesSearcher.findExactMatchingRules(entity, indexPath, config),
            maxNumSuggestions,
            MINIMUM_ACCEPTABLE_SCORE);

        // Check if there are enough similar matches in rules
        if (!done) {
            done = SuggestionCollector.addSuggestionsUntilLimitReached(
                suggestions,
                rulesSearcher.findSimilarRules(entity, indexPath, config),
                maxNumSuggestions,
                MINIMUM_ACCEPTABLE_SCORE);
        }

        // Check if there are enough exact matches in ontologies
        if (!done) {
            done = SuggestionCollector.addSuggestionsUntilLimitReached(
                suggestions,
                ontologiesSearcher.findExactMatchingOntologies(entity, indexPath, config),
                maxNumSuggestions,
                MINIMUM_ACCEPTABLE_SCORE);
        }

        // Check if there are enough similar matches in ontologies
        if (!done) {
            SuggestionCollector.addSuggestionsUntilLimitReached(
                suggestions,
                ontologiesSearcher.findSimilarMatchingOntologies(entity, indexPath, config),
                maxNumSuggestions,
                MINIMUM_ACCEPTABLE_SCORE);
        }
        // Suggestions need to be sorted (descending order) by 'score'
        return SuggestionsSorter.sortSuggestionsByScoreDesc(suggestions);

    }

}
