package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.IndexTestCreator;
import org.cancerModels.entity2ontology.index.service.AnalyzerProvider;
import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class OntologiesSearcherTest {

    private static final String CONFIGURATION_FILE =
        "src/test/resources/mappingConfigurations/pdcmMappingConfiguration.json";

    private static final String INDEX_DATA_DIR = "ontologiesSearcher/";

    private final QueryBuilder queryBuilder = new QueryBuilder();
    private final TemplateQueryProcessor templateQueryProcessor = new TemplateQueryProcessor();
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();

    private final Searcher searcher = new Searcher(new AnalyzerProvider());
    private final QueryProcessor queryProcessor = new QueryProcessor(searcher);

    private OntologiesSearcher instance;

    @BeforeEach
    public void setup() {
        instance = new OntologiesSearcher(queryBuilder, templateQueryProcessor, queryProcessor, scoreCalculator);
    }

    @Test
    public void testFindExactMatchingOntologies_exactMatchLabel() throws IOException {

        String fileName = "smallNumberDiagnosisOntology.json";
        String indexLocation = IndexTestCreator.createIndex(INDEX_DATA_DIR + fileName);

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Fusion Negative Alveolar Rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);
        // Override templates to use a simple single one
        List<String> templates = List.of("${SampleDiagnosis}");

        mappingConfiguration.getConfigurationByEntityType("diagnosis").setOntologyTemplates(templates);

        List<Suggestion> suggestions = instance.findExactMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("ontology_1", suggestion.getTargetEntity().getId());
        assertEquals(100.0, suggestion.getScore());
    }

    @Test
    public void testFindExactMatchingOntologies_exactMatchSynonym() throws IOException {

        String fileName = "smallNumberDiagnosisOntology.json";
        String indexLocation = IndexTestCreator.createIndex(INDEX_DATA_DIR + fileName);

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_2");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Skull Osteoma");
        data.put("OriginTissue", "skull");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);
        // Override templates to use a simple single one
        List<String> templates = List.of("${SampleDiagnosis}");

        mappingConfiguration.getConfigurationByEntityType("diagnosis").setOntologyTemplates(templates);

        List<Suggestion> suggestions = instance.findExactMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("ontology_2", suggestion.getTargetEntity().getId());
        assertEquals(1, suggestions.size());
        assertEquals(100.0, suggestions.get(0).getScore());
    }

    @Test
    public void testFindSimilarMatchingOntologies_similarMatchLabel() throws IOException {

        String fileName = "smallNumberDiagnosisOntology.json";
        String indexLocation = IndexTestCreator.createIndex(INDEX_DATA_DIR + fileName);

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Fusion POSITIVE Alveolar Rhabdomyosarcoma");
        data.put("OriginTissue", "orbit");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);
        // Override templates to use a simple single one
        List<String> templates = List.of("${SampleDiagnosis}");

        mappingConfiguration.getConfigurationByEntityType("diagnosis").setOntologyTemplates(templates);

        List<Suggestion> suggestions = instance.findSimilarMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("ontology_1", suggestion.getTargetEntity().getId());
        assertTrue(suggestion.getScore() <= 60.0);
    }


    @Test
    public void testFindSimilarMatchingOntologies_similarMatchSynonym() throws IOException {

        String fileName = "smallNumberDiagnosisOntology.json";
        String indexLocation = IndexTestCreator.createIndex(INDEX_DATA_DIR + fileName);

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> data = new HashMap<>();
        data.put("SampleDiagnosis", "Head Osteoma");
        data.put("OriginTissue", "skull");
        data.put("TumorType", "primary");
        sourceEntity.setData(data);

        MappingConfiguration mappingConfiguration = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);
        // Override templates to use a simple single one
        List<String> templates = List.of("${SampleDiagnosis}");

        mappingConfiguration.getConfigurationByEntityType("diagnosis").setOntologyTemplates(templates);

        List<Suggestion> suggestions = instance.findSimilarMatchingOntologies(
            sourceEntity, indexLocation, mappingConfiguration);

        Suggestion suggestion = suggestions.getFirst();

        assertEquals(1, suggestions.size());
        assertEquals("ontology_2", suggestion.getTargetEntity().getId());
        assertTrue(suggestion.getScore() <= 60.0);
    }

}