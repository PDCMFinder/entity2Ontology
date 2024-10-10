package org.cancerModels.entity2ontology.map.service;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class RulesSearcher {

    // This allows us to create the different Lucene queries
    private final QueryBuilder queryBuilder;

    // This allows us to execute Lucene queries
    private final Searcher searcher;

    // This allows us to process the result of Lucene queries
    private final QueryResultProcessor queryResultProcessor;

    private final ScoreCalculator scoreCalculator;

    public RulesSearcher(
        QueryBuilder queryBuilder,
        Searcher searcher,
        QueryResultProcessor queryResultProcessor,
        ScoreCalculator scoreCalculator) {

        this.queryBuilder = queryBuilder;
        this.searcher = searcher;
        this.queryResultProcessor = queryResultProcessor;
        this.scoreCalculator = scoreCalculator;
    }

    public List<Suggestion> findExactMatchingRules(
        SourceEntity entity, String indexPath, MappingConfiguration config) throws IOException {
        Query query = queryBuilder.buildExactMatchRulesQuery(entity, config);
        List<Suggestion> suggestions = executeQuery(query, indexPath);

        // Assign a `score` of 100 as results are perfect matches
        suggestions.forEach(suggestion -> suggestion.setScore(100));

        return suggestions;
    }

    public List<Suggestion> findSimilarRules(
        SourceEntity entity, String indexPath, MappingConfiguration config) throws IOException {
        Query query = queryBuilder.buildSimilarMatchRulesQuery(entity, config);
        List<Suggestion> suggestions = executeQuery(query, indexPath);

        Map<String, Double> fieldsWeightsByEntityType = config.getFieldsWeightsByEntityType(entity.getType());

        suggestions.forEach(suggestion -> {
            double score = scoreCalculator.calculateScoreAsPercentage(suggestion, entity, fieldsWeightsByEntityType);
            suggestion.setScore(score);
        });

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
