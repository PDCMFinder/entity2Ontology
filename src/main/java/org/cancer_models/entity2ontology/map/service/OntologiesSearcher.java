package org.cancer_models.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.cancer_models.entity2ontology.map.model.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * A component responsible for searching ontologies using Lucene queries. The search is based on templates that define
 * how to build queries, with scoring calculated to find the best matching ontologies.
 */
@Component
class OntologiesSearcher {

    private static final Logger logger = LogManager.getLogger(OntologiesSearcher.class);
    private final QueryBuilder queryBuilder;
    private final TemplateQueryProcessor templateQueryProcessor;
    private final QueryProcessor queryProcessor;
    private final ScoreCalculator scoreCalculator;

    /**
     * Constructs an {@code OntologiesSearcher} with the provided components for building, processing, and scoring
     * Lucene queries.
     *
     * @param queryBuilder           The component responsible for building Lucene queries.
     * @param templateQueryProcessor The component responsible for processing query templates.
     * @param queryProcessor         The component responsible for processing queries.
     * @param scoreCalculator        The component responsible for calculating scores for ontology suggestions.
     */
    public OntologiesSearcher(
        QueryBuilder queryBuilder,
        TemplateQueryProcessor templateQueryProcessor,
        QueryProcessor queryProcessor,
        ScoreCalculator scoreCalculator) {
        this.queryBuilder = queryBuilder;
        this.templateQueryProcessor = templateQueryProcessor;
        this.queryProcessor = queryProcessor;
        this.scoreCalculator = scoreCalculator;
    }

    /**
     * Finds the best matching ontologies for a given {@code SourceEntity} by searching against an index of ontologies.
     * The method iterates over ontology search templates and updates the highest score for each suggestion.
     *
     * @param entity    The source entity to use for the query.
     * @param indexPath The path to the Lucene index to search in.
     * @param config    The configuration object providing the templates and weights to use.
     * @return A list of ontology suggestions with updated scores.
     * @throws IOException If an error occurs while accessing the Lucene index.
     */
    public List<Suggestion> findExactMatchingOntologies(
        SourceEntity entity, String indexPath, MappingConfiguration config) throws IOException {
        return findMatchingOntologies(entity, indexPath, config, true);
    }

    /**
     * Finds the similar matching ontologies for a given {@code SourceEntity} by searching against an index of ontologies.
     * The method iterates over ontology search templates and updates the highest score for each suggestion.
     *
     * @param entity    The source entity to use for the query.
     * @param indexPath The path to the Lucene index to search in.
     * @param config    The configuration object providing the templates and weights to use.
     * @return A list of ontology suggestions with updated scores.
     * @throws IOException If an error occurs while accessing the Lucene index.
     */
    public List<Suggestion> findSimilarMatchingOntologies(
        SourceEntity entity, String indexPath, MappingConfiguration config) throws IOException {
        return findMatchingOntologies(entity, indexPath, config, false);
    }

    /**
     * Finds the best matching ontologies for a given {@code SourceEntity} by searching against an index of ontologies.
     * The method iterates over ontology search templates and updates the highest score for each suggestion.
     *
     * @param entity    The source entity to use for the query.
     * @param indexPath The path to the Lucene index to search in.
     * @param config    The configuration object providing the templates and weights to use.
     * @param exactMatch Indicates if the suggestion should be exact matches or not.
     * @return A list of ontology suggestions with updated scores.
     * @throws IOException If an error occurs while accessing the Lucene index.
     */
    private List<Suggestion> findMatchingOntologies(
        SourceEntity entity, String indexPath, MappingConfiguration config, boolean exactMatch) throws IOException {

        // The same suggestion can have different scores if compared against different templates so this structure
        // keeps the best score per suggestion.
        Map<Suggestion, Double> highestScores = new HashMap<>();

        // Fields and weights to use according to the entity type
        MappingConfiguration.ConfigurationPerType confByType = config.getConfigurationByEntityType(entity.getType());

        // All the templates that were configured to use in ontology search
        List<String> ontologyTemplatesAsText = confByType.getOntologyTemplates();

        // Each template should bring some suggestions. We use all of them
        for (String ontologyTemplateAsText : ontologyTemplatesAsText) {
            QueryTemplate queryTemplate = new QueryTemplate(ontologyTemplateAsText);

            // Builds the query terms for that template
            List<SearchQueryItem> searchQueryItems =
                buildSearchQueryItemsFromTemplate(queryTemplate, entity, confByType.getWeightsMap());

            // We get the suggestions for the specific template
            List<Suggestion> suggestionsPerTemplate = processSearchItems(searchQueryItems, indexPath, exactMatch);

            // Keep the highest scoring suggestions
            for (Suggestion suggestion : suggestionsPerTemplate) {
                if (highestScores.containsKey(suggestion)) {
                    double existingScore = highestScores.get(suggestion);
                    if (existingScore < suggestion.getScore()) {
                        highestScores.put(suggestion, suggestion.getScore());
                    }
                } else {
                    highestScores.put(suggestion, suggestion.getScore());
                }
            }
        }
        // Update the scores with the highest values
        highestScores.keySet().forEach(suggestion -> suggestion.setScore(highestScores.get(suggestion)));
        return new ArrayList<>(highestScores.keySet());
    }

    /**
     * Searches for ontology suggestions using a given list of search terms, and calculates the scores.
     *
     * @param searchQueryItems List of {@link SearchQueryItem} to use in the query.
     * @param indexPath  The path to the Lucene index to search in.
     * @return A list of ontology suggestions with calculated scores.
     * @throws IOException If an error occurs while searching or processing the Lucene query.
     */
    private List<Suggestion> processSearchItems(
        List<SearchQueryItem> searchQueryItems, String indexPath, boolean exactMatch) throws IOException {

        List<Suggestion> suggestions;
        Query query;

        if (exactMatch) {
            query = queryBuilder.buildExactMatchOntologiesQuery(searchQueryItems);
        }

       else {
            query = queryBuilder.buildSimilarMatchOntologiesQuery(searchQueryItems);
        }

        suggestions = queryProcessor.executeQuery(query, indexPath);
        // Calculate the score for each suggestion
        for (Suggestion suggestion : suggestions) {
            double score = scoreCalculator.calculateScoreInOntologySuggestion(searchQueryItems, suggestion);
            suggestion.setScore(score);
        }

        return suggestions;
    }

    private List<SearchQueryItem> buildSearchQueryItemsFromTemplate(
        QueryTemplate template, SourceEntity entity, Map<String, Double> weightsMap) {

        List<SearchQueryItem> searchQueryItems;

        try {
            searchQueryItems = templateQueryProcessor.extractSearchQueryItems(
                template, entity, weightsMap);
        } catch (IllegalArgumentException exception) {
            logger.error("Error processing template [{}]: ", template, exception);
            throw new IllegalArgumentException(exception.getMessage());
        }
        return searchQueryItems;
    }

}
