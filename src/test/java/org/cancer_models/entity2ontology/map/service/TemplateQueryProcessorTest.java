package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.map.model.QueryTemplate;
import org.cancer_models.entity2ontology.map.model.SearchQueryItem;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateQueryProcessorTest {

    private TemplateQueryProcessor templateQueryProcessor;
    private SourceEntity mockEntity;

    @BeforeEach
    void setUp() {
        templateQueryProcessor = new TemplateQueryProcessor();
        // Initialize mock entity with some test data
        Map<String, String> data = new HashMap<>();
        data.put("TumorType", "Metastatic");
        data.put("SampleDiagnosis", "Malignant Neoplasm");
        data.put("OriginTissue", "Larynx");

        mockEntity = new SourceEntity();
        mockEntity.setData(data);
    }

    @Test
    void testExtractSearchQueryItems_validTemplate() {
        QueryTemplate template = new QueryTemplate("${TumorType} ${SampleDiagnosis} ${OriginTissue}");
        Map<String, Double> weightsMap = new HashMap<>();
        weightsMap.put("TumorType", 1.0);
        weightsMap.put("SampleDiagnosis", 2.0);
        weightsMap.put("OriginTissue", 1.0);

        List<SearchQueryItem> result = templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);

        assertNotNull(result);
        assertEquals(3, result.size());

        // Verify each SearchQueryItem's properties
        assertEquals("TumorType", result.get(0).getField());
        assertEquals("metastatic", result.get(0).getValue());
        assertEquals(1.0, result.get(0).getWeight());

        assertEquals("SampleDiagnosis", result.get(1).getField());
        assertEquals("malignant neoplasm", result.get(1).getValue());
        assertEquals(2.0, result.get(1).getWeight());

        assertEquals("OriginTissue", result.get(2).getField());
        assertEquals("larynx", result.get(2).getValue());
        assertEquals(1.0, result.get(2).getWeight());
    }

    @Test
    void testExtractSearchQueryItems_overlappingDataTemplate() {
        QueryTemplate template = new QueryTemplate("${TumorType} ${OriginTissue} ${SampleDiagnosis}");
        Map<String, Double> weightsMap = new HashMap<>();
        weightsMap.put("TumorType", 0.5);
        weightsMap.put("SampleDiagnosis", 1.5);
        weightsMap.put("OriginTissue", 0.5);

        // Initialize entity with attributes that contain overlapping information (like OriginTissue=lung and
        // the diagnosis also containing the word "lung")
        Map<String, String> data = new HashMap<>();
        data.put("TumorType", "Recurrent");
        data.put("SampleDiagnosis", "Lung Carcinoma");
        data.put("OriginTissue", "Lung");

        mockEntity = new SourceEntity();
        mockEntity.setData(data);

        List<SearchQueryItem> result = templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);

        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify each SearchQueryItem's properties
        assertEquals("TumorType", result.get(0).getField());
        assertEquals("recurrent", result.get(0).getValue());
        assertEquals(0.5, result.get(0).getWeight());

        assertEquals("SampleDiagnosis", result.get(1).getField());
        assertEquals("lung carcinoma", result.get(1).getValue());
        assertEquals(1.5, result.get(1).getWeight());

    }

    @Test
    void testExtractSearchQueryItems_overlappingDataTemplate2() {
        QueryTemplate template = new QueryTemplate("${TumorType} ${OriginTissue} ${SampleDiagnosis}");
        Map<String, Double> weightsMap = new HashMap<>();
        weightsMap.put("TumorType", 0.5);
        weightsMap.put("SampleDiagnosis", 1.5);
        weightsMap.put("OriginTissue", 0.5);

        // Initialize entity with attributes that contain overlapping information (like OriginTissue=lung and
        // the diagnosis also containing the word "lung")
        Map<String, String> data = new HashMap<>();
        data.put("TumorType", "Recurrent");
        data.put("SampleDiagnosis", "Lung Carcinoma");
        data.put("OriginTissue", "Lung");

        mockEntity = new SourceEntity();
        mockEntity.setData(data);

        List<SearchQueryItem> result = templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);

        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify each SearchQueryItem's properties
        assertEquals("TumorType", result.get(0).getField());
        assertEquals("recurrent", result.get(0).getValue());
        assertEquals(0.5, result.get(0).getWeight());

        assertEquals("SampleDiagnosis", result.get(1).getField());
        assertEquals("lung carcinoma", result.get(1).getValue());
        assertEquals(1.5, result.get(1).getWeight());

    }

    @Test
    void testExtractSearchQueryItems_templateNull() {
        QueryTemplate template = null;
        Map<String, Double> weightsMap = new HashMap<>();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);
        });
        assertEquals("Template cannot be null", exception.getMessage());
    }

    @Test
    void testExtractSearchQueryItems_missingKeyInEntity() {
        QueryTemplate template = new QueryTemplate("${TumorType} ${MissingKey}");
        Map<String, Double> weightsMap = new HashMap<>();
        weightsMap.put("TumorType", 1.0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);
        });

        assertEquals("Key 'MissingKey' not found in the map 'entity values'.", exception.getMessage());
    }

    @Test
    void testExtractSearchQueryItems_missingKeyInWeights() {
        QueryTemplate template = new QueryTemplate("${TumorType} ${SampleDiagnosis}");
        Map<String, Double> weightsMap = new HashMap<>();
        weightsMap.put("TumorType", 1.0);
        // Missing weight for SampleDiagnosis

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);
        });

        assertEquals("Key 'SampleDiagnosis' not found in the map 'weights'.", exception.getMessage());
    }

    @Test
    void testExtractSearchQueryItems_emptyEntityData() {
        QueryTemplate template = new QueryTemplate("${TumorType}");
        mockEntity.setData(new HashMap<>());  // Empty data map
        Map<String, Double> weightsMap = new HashMap<>();
        weightsMap.put("TumorType", 1.0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);
        });

        assertEquals("Source entity data cannot be empty", exception.getMessage());
    }

    @Test
    void testExtractSearchQueryItems_emptyWeightsMap() {
        QueryTemplate template = new QueryTemplate("${TumorType}");
        Map<String, Double> weightsMap = new HashMap<>();  // Empty weights map

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);
        });

        assertEquals("Weights map cannot be empty", exception.getMessage());
    }

}