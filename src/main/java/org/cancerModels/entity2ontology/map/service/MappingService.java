package org.cancerModels.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.TopDocs;
import org.cancerModels.entity2ontology.index.service.AnalyzerProvider;
import org.cancerModels.entity2ontology.index.service.Indexer;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Service class responsible for performing entity mappings.
 */
@Component
public class MappingService {

    private static final Logger logger = LogManager.getLogger(MappingService.class);

    private final QueryBuilder queryBuilder;

    public MappingService(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    /**
     * Generates a list of suggestions (sorted by score) for a given entity.
     *
     * @param entity            the source entity to be mapped
     * @param indexPath         the path of the index to use for the mapping
     * @param maxNumSuggestions the max number of suggestions to get
     * @param config            information about how to build the queries to find matches
     * @return a list of suggestions for the source entity
     */
    public List<Suggestion> mapEntity(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config) throws IOException {
        logger.info("Mapping entity {} using index {}", entity, indexPath);
        logger.info("Using configuration {}", config.getName());

        validateSourceEntity(entity);
        validateIndex(indexPath);

        List<Suggestion> suggestions = queryRuleFieldToField(entity, indexPath, maxNumSuggestions, config);
        return suggestions;
    }

    private List<Suggestion> queryRuleFieldToField(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config) throws IOException {

        AnalyzerProvider analyzerProvider = new AnalyzerProvider();
        Searcher searcher = new Searcher(analyzerProvider);
        TopDocs topDocs = searcher.search(queryBuilder.buildExactMatchRulesQuery(entity, config), indexPath);
        QueryResultProcessor queryResultProcessor = new QueryResultProcessor();
        return queryResultProcessor.processTopDocs(topDocs, searcher.getIndexSearcher(indexPath));
    }

    private void validateSourceEntity(SourceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Entity id cannot be null");
        }
        if (entity.getType() == null) {
            throw new IllegalArgumentException("Entity type cannot be null");
        }
        if (entity.getData() == null) {
            throw new IllegalArgumentException("Entity data cannot be null");
        }
    }

    private void validateIndex(String indexPath) {
        if (indexPath == null) {
            throw new IllegalArgumentException("Index cannot be null");
        }
        if (!Indexer.isValidLuceneIndex(indexPath)) {
            throw new IllegalArgumentException(String.format("Index [%s] is not a valid lucene index", indexPath));
        }
    }
}
