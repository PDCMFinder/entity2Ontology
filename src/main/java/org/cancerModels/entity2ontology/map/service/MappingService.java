package org.cancerModels.entity2ontology.map.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
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

        // For now let's assign a list with a dummy result
        /*List<Suggestion> dummy = new ArrayList<>();
        Suggestion suggestion1 = new Suggestion();
        suggestion1.setTargetId("reference_1");
        suggestion1.setType("type_1");
        //suggestion1.setSourceEntity(entity);
        suggestion1.setScore(5);
        suggestion1.setTermLabel("label_term_1");
        suggestion1.setTermUrl("term_url_1");
        dummy.add(suggestion1);

        Suggestion suggestion2 = new Suggestion();
        suggestion2.setTargetId("reference_2");
        suggestion2.setType("type_2");
        //suggestion2.setSourceEntity(entity);
        suggestion2.setScore(0);
        suggestion2.setTermLabel("label_term_2");
        suggestion2.setTermUrl("term_url_2");
        dummy.add(suggestion2);
        return dummy;*/
    }

    private List<Suggestion> queryRuleFieldToField(
        SourceEntity entity, String indexPath, int maxNumSuggestions, MappingConfiguration config) throws IOException {
        System.out.println("Field to field query");
        System.out.println("Entity: " + entity);

        String sentence1 = entity.getData().get("SampleDiagnosis");
        Query query1 = buildPhraseQuery("rule.SampleDiagnosis", sentence1, 0);
        System.out.println("query1: " + query1);
        AnalyzerProvider analyzerProvider = new AnalyzerProvider();
        Searcher searcher = new Searcher(analyzerProvider);
        TopDocs topDocs = searcher.search(query1, indexPath);
        QueryResultProcessor queryResultProcessor = new QueryResultProcessor();
        List<Suggestion> suggestions = queryResultProcessor.processTopDocs(topDocs, searcher.getIndexSearcher(indexPath));
        return suggestions;
    }

    private Query buildPhraseQuery(String field, String phrase, int maxEdits) {
        String[] words = phrase.split(" ");
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        for (String word : words) {
            FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, word), maxEdits);
            builder.add(fuzzyQuery, BooleanClause.Occur.MUST); // Allow fuzzy matching on each word
        }
        BooleanQuery booleanQuery = builder.build();
        return booleanQuery;
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
