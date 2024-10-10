package org.cancerModels.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SearchQueryItem;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    public OntologiesSearcher(QueryBuilder queryBuilder, TemplateQueryProcessor templateQueryProcessor, Searcher searcher, QueryResultProcessor queryResultProcessor, ScoreCalculator scoreCalculator) {
        this.queryBuilder = queryBuilder;
        this.templateQueryProcessor = templateQueryProcessor;
        this.searcher = searcher;
        this.queryResultProcessor = queryResultProcessor;
        this.scoreCalculator = scoreCalculator;
    }

    public List<Suggestion> findExactMatchingOntologies(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config) throws IOException {
        MappingConfiguration.ConfigurationPerType confByType = config.getConfigurationByEntityType(entity.getType());
        List<String> ontologyTemplates = confByType.getOntologyTemplates();

        for (String ontologyTemplate : ontologyTemplates) {
            processTemplate(ontologyTemplate, entity, confByType.getWeightsMap(), indexPath);
        }
        List<Suggestion> suggestions = new ArrayList<>();
        System.out.println("findExactMatchingOntologies==> " + suggestions.size());
        return suggestions;
    }

    private List<Suggestion> processTemplate(String template, SourceEntity entity, Map<String, Double> weightsMap, String indexPath) throws IOException {
        // The template is expected to have the format "${key1} ${key1} ${keyN}"
        // Example: ${TumorType} ${SampleDiagnosis} ${OriginTissue}
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
        System.out.println("query for template " + template);
        System.out.println(query);
        suggestions = executeQuery(query, indexPath);
        System.out.println("$$$$$");
        System.out.println(suggestions);
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
