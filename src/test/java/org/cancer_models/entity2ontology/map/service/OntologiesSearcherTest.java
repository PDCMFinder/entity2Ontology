package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.IndexTestCreator;
import org.cancer_models.entity2ontology.common.utils.FileUtils;
import org.cancer_models.entity2ontology.exceptions.MappingException;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OntologiesSearcherTest {

    // File with the configuration for the search
    private static final String CONFIGURATION_FILE =
        "src/test/resources/mappingConfigurations/pdcmMappingConfiguration.json";

    // Directory with the index data
    private static final String INDEX_DATA_DIR = "ontologiesSearcher/";

    // Name of the JSON file with the index to use in these tests
    private static final String INDEX_DATA_FILE = "smallOntologiesIndex.json";

    private static MappingConfiguration mappingConfiguration;

    // Location of the Lucene Index
    private static String indexLocation;

    private final QueryBuilder queryBuilder = new QueryBuilder();
    private final TemplateQueryProcessor templateQueryProcessor = new TemplateQueryProcessor();
    private final SuggestionScoreCalculator scoreCalculator = new DefaultSuggestionScoreCalculator();

    private final Searcher searcher = new Searcher(new AnalyzerProvider());
    private final QueryProcessor queryProcessor = new QueryProcessor(searcher);

    private OntologiesSearcher instance;

    @BeforeAll
    static void init() throws IOException {
        // Create the index and keep the name to delete it at the end
        indexLocation = IndexTestCreator.createIndex(INDEX_DATA_DIR + INDEX_DATA_FILE);
        mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);
    }

    @AfterAll
    static void tearDown() {
        // Delete the index
        FileUtils.deleteRecursively(new File(indexLocation));
    }

    @BeforeEach
    void setup() {
        instance = new OntologiesSearcher(queryBuilder, templateQueryProcessor, queryProcessor, scoreCalculator);
    }

    @Test
    void testFindExactMatchingOntologies_exactMatchLabel() throws MappingException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Fusion Negative Alveolar Rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        List<Suggestion> suggestions = instance.findExactMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("ontology_1", suggestion.getTargetEntity().id());
        assertEquals(100.0, suggestion.getScore());
    }

    @Test
    void testFindExactMatchingOntologies_exactMatchSynonym() throws MappingException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_2");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Skull Osteoma");
        data.put("OriginTissue", "skull");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        List<Suggestion> suggestions = instance.findExactMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("ontology_2", suggestion.getTargetEntity().id());
        assertEquals(1, suggestions.size());
        assertEquals(99.0, suggestions.get(0).getScore());
    }

    @Test
    void testFindExactMatchingOntologies_noMatch() throws MappingException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "noMatch");
        data.put("OriginTissue", "noMatch");
        data.put("TumorType", "noMatch");
        sourceEntity.setData(data);

        List<Suggestion> suggestions = instance.findExactMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        assertTrue(suggestions.isEmpty(), "The suggestion list should be empty");
    }

    @Test
    void testFindSimilarMatchingOntologies_similarMatchLabel() throws MappingException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Fusion POSITIVE Alveolar Rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        List<Suggestion> suggestions = instance.findSimilarMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("ontology_1", suggestion.getTargetEntity().id());
        assertTrue(suggestion.getScore() <= 60.0);
    }

    @Test
    void testFindSimilarMatchingOntologies_similarMatchLabelWithSlash() throws MappingException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("treatment");
        Map<String, String> data = new HashMap<>();
        data.put("TreatmentName", "Temozolomide");
        sourceEntity.setData(data);

        List<Suggestion> suggestions = instance.findSimilarMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertTrue(suggestion.getScore() > 0.0);
    }

    @Test
    void testFindSimilarMatchingOntologies_similarMatchSynonym() throws MappingException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Head Osteoma");
        data.put("OriginTissue", "skull");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        List<Suggestion> suggestions = instance.findSimilarMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("ontology_2", suggestion.getTargetEntity().id());
        System.out.println(suggestion.getScore());
        assertTrue(suggestion.getScore() <= 75.0);
    }

    @Test
    void testFindSimilarMatchingOntologies_noMatch() throws MappingException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "noMatch");
        data.put("OriginTissue", "noMatch");
        data.put("TumorType", "noMatch");
        sourceEntity.setData(data);

        List<Suggestion> suggestions = instance.findSimilarMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        assertTrue(suggestions.isEmpty(), "The suggestion list should be empty");
    }

    @Test
    void testFindSimilarMatchingOntologies_longListSynonymsPriorityLabelCase1() throws MappingException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("treatment");
        Map<String, String> data = new HashMap<>();
        data.put("TreatmentName", "Carboplatine");
        sourceEntity.setData(data);

        List<Suggestion> suggestions = instance.findSimilarMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);
        suggestions = SuggestionsSorter.sortSuggestionsByScoreDesc(suggestions);
        Suggestion best = suggestions.getFirst();

        assertEquals("Carboplatin", best.getTermLabel());
    }

    @Test
    void testFindSimilarMatchingOntologies_longListSynonymsPriorityLabelCase2() throws MappingException {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("treatment");
        Map<String, String> data = new HashMap<>();
        data.put("TreatmentName", "Sorafenib");
        sourceEntity.setData(data);

        List<Suggestion> suggestions = instance.findExactMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);
        suggestions = SuggestionsSorter.sortSuggestionsByScoreDesc(suggestions);
        Suggestion best = suggestions.getFirst();
        assertEquals("Sorafenib", best.getTermLabel());
        suggestions.forEach(x-> System.out.println(x.getTermLabel()+" " + x.getRawScore() + " " + x.getScore()));

    }

}
