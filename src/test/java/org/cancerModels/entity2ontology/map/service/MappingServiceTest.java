package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.DiagnosisMappingInputFileEntry;
import org.cancerModels.entity2ontology.DiagnosisMappingInputReader;
import org.cancerModels.entity2ontology.IndexTestCreator;
import org.cancerModels.entity2ontology.index.service.AnalyzerProvider;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MappingServiceTest {

    private static final String DATA_DIR = "src/test/resources/mappings/";

    private static final String CONFIGURATION_FILE =
        "src/test/resources/mappingConfigurations/pdcmMappingConfiguration.json";

    private final MappingConfiguration config;

    private final Searcher searcher = new Searcher(new AnalyzerProvider());

    private final QueryBuilder queryBuilder = new QueryBuilder();

    private final QueryProcessor queryProcessor = new QueryProcessor(searcher);

    private final ScoreCalculator scoreCalculator = new ScoreCalculator();

    private final RulesSearcher rulesSearcher = new RulesSearcher(queryBuilder, queryProcessor, scoreCalculator);

    private final TemplateQueryProcessor templateQueryProcessor = new TemplateQueryProcessor();

    private final OntologiesSearcher ontologiesSearcher =
        new OntologiesSearcher(queryBuilder, templateQueryProcessor, queryProcessor, scoreCalculator);

    private final SuggestionsFinder suggestionsFinder = new SuggestionsFinder(rulesSearcher, ontologiesSearcher);

    private final MappingService instance = new MappingService(suggestionsFinder);

    public MappingServiceTest() throws IOException {
        config = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);
    }

    @Test
    public void shouldFailIfNullEntity() {
        // When we try to map an entity that is null
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            instance.mapEntity(null, "", 0, config);
        });

        // Then we get an IOException
        assertEquals("Entity cannot be null", exception.getMessage());
    }

    @Test
    public void shouldFailIfNullEntityId() {
        // When we try to map an entity that is null
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            SourceEntity sourceEntity = new SourceEntity();
            instance.mapEntity(sourceEntity, "", 0, config);
        });

        // Then we get an IllegalArgumentException
        assertTrue(
            exception.getMessage().contains("Entity id cannot be null"),
            "Expected exception message to contain 'Entity id cannot be null'");
    }

    @Test
    public void shouldFailIfNullEntityType() {
        // When we try to map an entity that is null
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            SourceEntity sourceEntity = new SourceEntity();
            sourceEntity.setId("1");
            instance.mapEntity(sourceEntity, "", 0, config);
        });

        // Then we get an IllegalArgumentException
        assertTrue(
            exception.getMessage().contains("Entity type cannot be null"),
            "Expected exception message to contain 'Entity type cannot be null'");
    }

    @Test
    public void shouldFailIfNullEntityData() {
        // When we try to map an entity that is null
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            SourceEntity sourceEntity = new SourceEntity();
            sourceEntity.setId("1");
            sourceEntity.setType("type");
            instance.mapEntity(sourceEntity, "", 0, config);
        });

        // Then we get an IllegalArgumentException
        assertTrue(
            exception.getMessage().contains("Entity data cannot be null"),
            "Expected exception message to contain 'Entity data cannot be null'");
    }

    @Test
    public void shouldFailIfNullIndexName() {
        // When we try to map an entity without specifying the index
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            SourceEntity sourceEntity = new SourceEntity();
            sourceEntity.setId("1");
            sourceEntity.setType("type");
            sourceEntity.setData(new HashMap<>());
            instance.mapEntity(sourceEntity, null, 0, config);
        });

        // Then we get an IOException
        assertEquals("Index cannot be null", exception.getMessage());
    }

    @Test
    public void shouldFailIfIndexNotExists() {
        // When we try to map an entity using an invalid index
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            SourceEntity sourceEntity = new SourceEntity();
            sourceEntity.setId("1");
            sourceEntity.setType("type");
            sourceEntity.setData(new HashMap<>());
            instance.mapEntity(sourceEntity, "unknown", 0, config);
        });

        // Then we get an IOException
        assertEquals("Index [unknown] is not a valid lucene index", exception.getMessage());
    }

    @Test
    public void shouldGeExpectedMappingsForDiagnosisSet() throws IOException {
        // Given we have an index with diagnosis at src/test/output/small_diagnosis_index
        String smallDiagnosisIndexLocation = IndexTestCreator.createIndex(
            "input_data_small_diagnosis_index/data.json");

        // And we read a tsv file which contains a list of diagnosis to map and the expected results
        List<DiagnosisMappingInputFileEntry> entries =
            DiagnosisMappingInputReader.parseTSV(DATA_DIR + "set_1/diagnosis.tsv");

        // When we map each one of them, Then we expect to get the Expected Label and the Minimum Score stated in the file
        for (DiagnosisMappingInputFileEntry entry : entries) {
            testExpectedDiagnosisMapping(entry);
        }

        // Delete the index
        //FileUtils.deleteRecursively(new File(smallDiagnosisIndexLocation));
    }

    private void testExpectedDiagnosisMapping(DiagnosisMappingInputFileEntry entry) {
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("id_" + entry.getEntryId());
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", entry.getSampleDiagnosis());
        data.put("OriginTissue", entry.getOriginTissue());
        data.put("TumorType", entry.getTumorType());
        sourceEntity.setData(data);

        // `Index Path` in the file only contains the directory name. Adding here the path where the index was created
        String indexName = "src/test/output/" + entry.getIndexPath();

        String label = "";
        double score = 0;

        Suggestion bestSuggestion = getTopSuggestion(sourceEntity, indexName, 1);
        label = bestSuggestion.getTermLabel();
        score = bestSuggestion.getScore();

        try {
            // Gets the expected mapping
            assertEquals(entry.getExpectedLabel(), label);
            assertTrue(
                score >= entry.getMinimumScore(),
                String.format(
                    "Obtained score %s expected to be equal or greater than %s", score, entry.getMinimumScore()));
        } catch (AssertionFailedError e) {
            System.err.println("Assertion failed for entry: " + entry.getEntryId());
            throw e;
        }
    }

    // Executes the mapping process for a sourceEntity and gets the top (best) suggestion
    private Suggestion getTopSuggestion(SourceEntity sourceEntity, String indexName, int numSuggestions) {
        Suggestion bestSuggestion = null;
        try {
            List<Suggestion> suggestions = instance.mapEntity(sourceEntity, indexName, numSuggestions, config);
            bestSuggestion = suggestions.get(0);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return bestSuggestion;
    }
}
