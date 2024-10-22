package org.cancerModels.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.cancerModels.entity2ontology.map.model.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class OntologiesSearcher {

    private static final Logger logger = LogManager.getLogger(OntologiesSearcher.class);
    // This allows us to create the different Lucene queries
    private final QueryBuilder queryBuilder;
    private final TemplateQueryProcessor templateQueryProcessor;
    // This allows us to execute Lucene queries
    private final Searcher searcher;
    // This allows us to process the result of Lucene queries
    private final QueryResultProcessor queryResultProcessor;
    private final ScoreCalculator scoreCalculator;


    public OntologiesSearcher(
        QueryBuilder queryBuilder,
        TemplateQueryProcessor templateQueryProcessor,
        Searcher searcher, QueryResultProcessor queryResultProcessor,
        ScoreCalculator scoreCalculator) {
        this.queryBuilder = queryBuilder;
        this.templateQueryProcessor = templateQueryProcessor;
        this.searcher = searcher;
        this.queryResultProcessor = queryResultProcessor;
        this.scoreCalculator = scoreCalculator;
    }

    public List<Suggestion> findExactMatchingOntologies(
        SourceEntity entity, String indexPath, MappingConfiguration config) throws IOException {

        Set<Suggestion> uniqueSuggestions = new HashSet<>();

        // Fields and weights to use according to the entity type
        MappingConfiguration.ConfigurationPerType confByType = config.getConfigurationByEntityType(entity.getType());

        // All the templates that were configured to use in ontology search
        List<String> ontologyTemplatesAsText = confByType.getOntologyTemplates();

        // Each template should bring some suggestions. We use all of them
        for (String ontologyTemplateAsText : ontologyTemplatesAsText) {
            QueryTemplate queryTemplate = new QueryTemplate(ontologyTemplateAsText);

            // We get the suggestions for the specific template
            List<Suggestion> suggestionsPerTemplate = processTemplate(
                queryTemplate, entity, confByType.getWeightsMap(), indexPath);

            // Using all the found suggestions. This might need to be sorted first
            uniqueSuggestions.addAll(suggestionsPerTemplate);
        }
        System.out.println("findExactMatchingOntologies==> " + uniqueSuggestions.size());
        return new ArrayList<>(uniqueSuggestions);
    }

    /**
     * Searches suggestions using a template as a guide to create the query, and assigns the respective scores
     * by calculating the similarity between each suggestion and the phrase that the template represents.
     *
     * @param template   {@link QueryTemplate} with the fields to use in the query.
     * @param entity     {@link SourceEntity} with the values to use in the query.
     * @param weightsMap The weights for the fields.
     * @param indexPath  Location of the lucene index.
     * @return A list of {@link Suggestion} which are the documents that are more similar to the phrase that the
     * template represents.
     * @throws IOException
     */
    private List<Suggestion> processTemplate(
        QueryTemplate template, SourceEntity entity, Map<String, Double> weightsMap, String indexPath) throws IOException {
        System.out.println("\nprocessTemplate==> " + template.getText());

        List<Suggestion> suggestions;
        List<SearchQueryItem> searchQueryItems;

        try {
            searchQueryItems = templateQueryProcessor.extractSearchQueryItems(
                template, entity, weightsMap);
        } catch (IllegalArgumentException exception) {
            logger.error("Error processing template [{}]: ", template, exception);
            throw new IllegalArgumentException(exception.getMessage());
        }

        Query query = queryBuilder.buildExactMatchOntologiesQuery(searchQueryItems);

        suggestions = executeQuery(query, indexPath);
        // Calculate the score for each suggestion
        for (Suggestion suggestion : suggestions) {
            double score = scoreCalculator.calculateScoreInOntologySuggestion(searchQueryItems, suggestion);
            System.out.println("--->>"+score);
            suggestion.setScore(score);
        }

        return suggestions;
    }

    private List<Suggestion> executeQuery(Query query, String indexPath) throws IOException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        TopDocs topDocs = searcher.search(query, indexPath);
        return queryResultProcessor.processQueryResponse(topDocs, searcher.getIndexSearcher(indexPath));
    }

}
