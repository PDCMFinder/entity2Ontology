package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.map.model.SearchQueryItem;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
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
        data.put("TumorType", "Carcinoma");
        data.put("SampleDiagnosis", "Positive");
        data.put("OriginTissue", "Lung");

        mockEntity = new SourceEntity();
        mockEntity.setData(data);
    }

    @Test
    void testExtractSearchQueryItems_validTemplate() {
        String template = "${TumorType} ${SampleDiagnosis} ${OriginTissue}";
        Map<String, Double> weightsMap = new HashMap<>();
        weightsMap.put("TumorType", 1.0);
        weightsMap.put("SampleDiagnosis", 0.5);
        weightsMap.put("OriginTissue", 2.0);

        List<SearchQueryItem> result = templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);

        assertNotNull(result);
        assertEquals(3, result.size());

        // Verify each SearchQueryItem's properties
        assertEquals("TumorType", result.get(0).getField());
        assertEquals("Carcinoma", result.get(0).getValue());
        assertEquals(1.0, result.get(0).getWeight());

        assertEquals("SampleDiagnosis", result.get(1).getField());
        assertEquals("Positive", result.get(1).getValue());
        assertEquals(0.5, result.get(1).getWeight());

        assertEquals("OriginTissue", result.get(2).getField());
        assertEquals("Lung", result.get(2).getValue());
        assertEquals(2.0, result.get(2).getWeight());
    }

    @Test
    void testExtractSearchQueryItems_templateNull() {
        String template = null;
        Map<String, Double> weightsMap = new HashMap<>();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);
        });
        assertEquals("Template cannot be null", exception.getMessage());
    }

    @Test
    void testExtractSearchQueryItems_templateEmpty() {
        String template = "";
        Map<String, Double> weightsMap = new HashMap<>();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);
        });

        assertEquals("Template cannot be empty", exception.getMessage());
    }

    @Test
    void testExtractSearchQueryItems_missingKeyInEntity() {
        String template = "${TumorType} ${MissingKey}";
        Map<String, Double> weightsMap = new HashMap<>();
        weightsMap.put("TumorType", 1.0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);
        });

        assertEquals("Key 'MissingKey' not found in the map 'entity values'.", exception.getMessage());
    }

    @Test
    void testExtractSearchQueryItems_missingKeyInWeights() {
        String template = "${TumorType} ${SampleDiagnosis}";
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
        String template = "${TumorType}";
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
        String template = "${TumorType}";
        Map<String, Double> weightsMap = new HashMap<>();  // Empty weights map

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            templateQueryProcessor.extractSearchQueryItems(template, mockEntity, weightsMap);
        });

        assertEquals("Weights map cannot be empty", exception.getMessage());
    }

}