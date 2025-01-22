package org.cancer_models.entity2ontology.map.service;

import org.apache.lucene.search.Query;
import org.cancer_models.entity2ontology.exceptions.MappingException;
import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * A component responsible for searching rules using Lucene queries.
 */
@Component
class RulesSearcher {

    /**
     * Builds Lucene queries for exact and similar match searches.
     */
    private final QueryBuilder queryBuilder;

    /**
     * Processes the results of Lucene queries into structured suggestions.
     */
    private final QueryProcessor queryProcessor;

    /**
     * Calculates a match score for each suggestion, used to rank results.
     */
    private final ScoreCalculator scoreCalculator;

    /**
     * Constructs a new RulesSearcher with dependencies for building, executing,
     * processing queries, and scoring results.
     *
     * @param queryBuilder          the query builder for creating Lucene queries
     * @param queryProcessor        the processor for handling query results
     * @param scoreCalculator       the calculator for scoring suggestion relevance
     */
    public RulesSearcher(
        QueryBuilder queryBuilder,
        QueryProcessor queryProcessor,
        ScoreCalculator scoreCalculator) {

        this.queryBuilder = queryBuilder;
        this.queryProcessor = queryProcessor;
        this.scoreCalculator = scoreCalculator;
    }

    /**
     * Searches for rules that exactly match the specified entity and configuration.
     *
     * <p>The results are assigned a score of 100 to indicate a perfect match.
     *
     * @param entity    the entity to match against rules
     * @param indexPath the path to the Lucene index to search
     * @param config    the mapping configuration to use
     * @return a list of suggestions that match exactly
     * @throws MappingException if an error occurs during the search
     */
    public List<Suggestion> findExactMatchingRules(
        SourceEntity entity, String indexPath, MappingConfiguration config) throws MappingException {
        Query query = queryBuilder.buildExactMatchRulesQuery(entity, config);
        List<Suggestion> suggestions = queryProcessor.executeQuery(query, indexPath);

        // Assign a `score` of 100 as results are perfect matches
        suggestions.forEach(suggestion -> suggestion.setScore(100));

        return suggestions;
    }

    /**
     * Searches for rules that are similar to the specified entity, using a configurable
     * similarity measure.
     *
     * <p>The results are scored based on field-specific weights defined in the configuration.
     *
     * @param entity    the entity to find similar rules for
     * @param indexPath the path to the Lucene index to search
     * @param config    the mapping configuration to use
     * @return a list of suggestions that closely match the entity
     * @throws MappingException if an error occurs during the search
     */
    public List<Suggestion> findSimilarRules(
        SourceEntity entity, String indexPath, MappingConfiguration config) throws MappingException {
        Query query = queryBuilder.buildSimilarMatchRulesQuery(entity, config);
        List<Suggestion> suggestions = queryProcessor.executeQuery(query, indexPath);

        Map<String, Double> fieldsWeightsByEntityType = config.getFieldsWeightsByEntityType(entity.getType());

        suggestions.forEach(suggestion -> {
            double score = scoreCalculator.calculateRuleSuggestionScoreAsPercentage(
                suggestion, entity, fieldsWeightsByEntityType);
            suggestion.setScore(score);
        });

        // Sort the suggestions before returning
        suggestions = SuggestionsSorter.sortSuggestionsByScoreDesc(suggestions);

        return suggestions;
    }
}
