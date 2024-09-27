package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.IndexTestCreator;
import org.cancerModels.entity2ontology.index.service.AnalyzerProvider;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SuggestionsFinderTest {

    private static final String CONFIGURATION_FILE =
        "src/test/resources/mappingConfigurations/pdcmMappingConfiguration.json";

    private static final String INDEX_DATA_DIR = "suggestionFinder/";

    private QueryBuilder queryBuilder = new QueryBuilder();
    private Searcher searcher = new Searcher(new AnalyzerProvider());
    private QueryResultProcessor queryResultProcessor = new QueryResultProcessor();

    private SuggestionsFinder instance = new SuggestionsFinder(queryBuilder, searcher, queryResultProcessor);

    @Test
    public void shouldGetOnePerfectMatchWhenOneDocumentMatches() throws IOException {

        String fileName = "singleDiagnosis.json";
        String indexLocation = IndexTestCreator.createIndex(INDEX_DATA_DIR + fileName);

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "fusion negative rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        int maxNumberOfSuggestions = 10;
        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);

        List<Suggestion> suggestions = instance.findSuggestions(
            sourceEntity, indexLocation, maxNumberOfSuggestions, mappingConfiguration);

        // We should find one suggestion
        assertEquals(1, suggestions.size());

        // We expect the suggestions to be ordered by score
        assertTrue(isSortedDescending(suggestions));

        // Our index contains exactly the document for the entry we are looking for, so match should be exact (score of 100)
        Suggestion suggestion = suggestions.get(0);
        assertEquals(suggestion.getScore(), 100.0);
    }

    @Test
    public void shouldGetOneSimilarMatchWhenOneDocumentMatchesPartially() throws IOException {

        String fileName = "singleDiagnosis.json";
        String indexLocation = IndexTestCreator.createIndex(INDEX_DATA_DIR + fileName);

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "fusion POSITIVE rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        int maxNumberOfSuggestions = 10;
        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);

        List<Suggestion> suggestions = instance.findSuggestions(
            sourceEntity, indexLocation, maxNumberOfSuggestions, mappingConfiguration);

        // We should find one suggestion
        assertEquals(1, suggestions.size());

        // We expect the suggestions to be ordered by score
        assertTrue(isSortedDescending(suggestions));

        // Our index contains exactly the document for the entry we are looking for, so match should be exact (score of 100)
        Suggestion suggestion = suggestions.get(0);
        System.out.println("Suggestion: " + suggestion);
        //assertEquals(suggestion.getScore(), 100.0);
    }

    private boolean isSortedDescending(List<Suggestion> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).getScore() < list.get(i + 1).getScore()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the suggestion score as a percentage, based on how similar the suggestion and the sourceEntity are.
     * A string similarity comparison is used.
     * Note that {@link Suggestion} has a `rawScore` assigned by Lucene. We are not using it as that's a value that helps
     * in sorting results but doesn't say anything about how good the result by its own is.
     * @param suggestion The suggestion for the mapping
     * @param sourceEntity The entity we are trying to map
     * @return A number (percentage) representing how similar the suggestion and the source entity are.
     */
    private double calculateScoreAsPercentage(Suggestion suggestion, SourceEntity sourceEntity) {
        return 0;
    }
}