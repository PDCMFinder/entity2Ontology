package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.IndexTestCreator;
import org.cancer_models.entity2ontology.common.utils.FileUtils;
import org.cancer_models.entity2ontology.index.service.AnalyzerProvider;
import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RulesSearcherTest {

    // File with the configuration for the search
    private static final String CONFIGURATION_FILE =
        "src/test/resources/mappingConfigurations/pdcmMappingConfiguration.json";

    // Directory with the index data
    private static final String INDEX_DATA_DIR = "rulesSearcher/";

    // Name of the JSON file with the index to use in these tests
    private static final String INDEX_DATA_FILE = "smallNumberDiagnosisRule.json";

    // Location of the Lucene Index
    private static String indexLocation;

    private final QueryBuilder queryBuilder = new QueryBuilder();
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();

    private final Searcher searcher = new Searcher(new AnalyzerProvider());
    private final QueryProcessor queryProcessor = new QueryProcessor(searcher);

    private RulesSearcher instance;

    @BeforeAll
    static void init() throws IOException {
        // Create the index and keep the name to delete it at the end
        indexLocation = IndexTestCreator.createIndex(INDEX_DATA_DIR + INDEX_DATA_FILE);
    }

    @AfterAll
    static void tearDown() {
        // Delete the index
        FileUtils.deleteRecursively(new File(indexLocation));
    }

    @BeforeEach
    void setup() {
        instance = new RulesSearcher(queryBuilder, queryProcessor, scoreCalculator);
    }

    @Test
    void testFindExactMatchingRules_exactMatch() throws IOException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "fusion negative rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);

        List<Suggestion> suggestions = instance.findExactMatchingRules(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("rule_1", suggestion.getTargetEntity().getId());
        assertEquals(100.0, suggestion.getScore());
    }

    @Test
    void testFindExactMatchingRules_NoMatch() throws IOException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "fusion xyz negative rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);

        List<Suggestion> suggestions = instance.findExactMatchingRules(
            sourceEntity, indexLocation, mappingConfiguration);

        assertTrue(suggestions.isEmpty(), "The suggestion list should be empty");

    }

    @Test
    void testFindSimilarRules_similarMatch() throws IOException {
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "fusion POSITIVE rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);

        List<Suggestion> suggestions = instance.findSimilarRules(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("rule_1", suggestion.getTargetEntity().getId());
        assertTrue(suggestion.getScore() <= 90, "The score should be less than 90");
    }

    @Test
    void testFindSimilarRules_NoMatch() throws IOException {
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "noMatch");
        data.put("OriginTissue", "noMatch");
        data.put("TumorType", "noMatch");
        sourceEntity.setData(data);

        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);

        List<Suggestion> suggestions = instance.findSimilarRules(
            sourceEntity, indexLocation, mappingConfiguration);

        assertTrue(suggestions.isEmpty(), "The suggestion list should be empty");
    }
}