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
 * This implementation of {@link SuggestionsFinder} looks for suggestions only in the documents that represent
 * ontology terms.
 */
@Component
@Qualifier("OntologySuggestionsFinder")
public class OntologySuggestionsFinder implements SuggestionsFinder {

    private final OntologiesSearcher ontologiesSearcher;
    private static final double MINIMUM_ACCEPTABLE_SCORE = 50;

    OntologySuggestionsFinder(OntologiesSearcher ontologiesSearcher) {
        this.ontologiesSearcher = ontologiesSearcher;
    }

    /**
     * Retrieves a list of suggestions for a given {@code entity} based on the provided {@code config}.
     *
     * @param entity            the source entity to be mapped
     * @param indexPath         the path of the index to use for the mapping
     * @param maxNumSuggestions the maximum number of suggestions to retrieve
     * @param config            information about how to build the queries to find matches
     * @return a list of suggestions for the source entity
     * @throws MappingException if an error occurs during the search
     */
    @Override
    public List<Suggestion> findSuggestions(
        SourceEntity entity,
        String indexPath,
        int maxNumSuggestions,
        MappingConfiguration config) throws MappingException {

        List<Suggestion> suggestions = new ArrayList<>();

        // Check if there are enough exact matches in ontologies
        boolean done = SuggestionCollector.addSuggestionsUntilLimitReached(
            suggestions,
            ontologiesSearcher.findExactMatchingOntologies(entity, indexPath, config),
            maxNumSuggestions,
            MINIMUM_ACCEPTABLE_SCORE);


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
