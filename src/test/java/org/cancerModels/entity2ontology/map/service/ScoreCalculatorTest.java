package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.cancerModels.entity2ontology.map.model.TargetEntity;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculatorTest {

    private final ScoreCalculator instance = new ScoreCalculator();

    @Test
    void shouldGetMaxScoreWhenPerfectSuggestion() {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> sourceEntityData = new HashMap<>();
        sourceEntityData.put("SampleDiagnosis", "fusion negative rhabdomyosarcoma");
        sourceEntityData.put("OriginTissue", "orbit");
        sourceEntityData.put("TumorType", "primary");
        sourceEntity.setData(sourceEntityData);

        TargetEntity targetEntity = new TargetEntity();
        Map<String, Object> targetEntityEntityData = new HashMap<>();
        targetEntityEntityData.put("SampleDiagnosis", "fusion negative rhabdomyosarcoma");
        targetEntityEntityData.put("OriginTissue", "orbit");
        targetEntityEntityData.put("TumorType", "primary");
        targetEntity.setData(targetEntityEntityData);
        Suggestion suggestion = new Suggestion(targetEntity);

        Map<String, Double> fieldsWeights = new HashMap<>();
        fieldsWeights.put("SampleDiagnosis", 1.0);
        fieldsWeights.put("OriginTissue", 1.0);
        fieldsWeights.put("TumorType", 1.0);

        double got = instance.calculateScoreAsPercentage(suggestion, sourceEntity, fieldsWeights);

        assertEquals(100.0, got);
    }

    @Test
    void shouldGetHighScoreWhenGoodSuggestion() {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("key_1");
        sourceEntity.setType("diagnosis");
        Map<String, String> sourceEntityData = new HashMap<>();
        sourceEntityData.put("SampleDiagnosis", "fusion - rhabdomyosarcoma");
        sourceEntityData.put("OriginTissue", "orbit");
        sourceEntityData.put("TumorType", "primary");
        sourceEntity.setData(sourceEntityData);

        TargetEntity targetEntity = new TargetEntity();
        Map<String, Object> targetEntityEntityData = new HashMap<>();
        targetEntityEntityData.put("SampleDiagnosis", "fusion negative rhabdomyosarcoma");
        targetEntityEntityData.put("OriginTissue", "orbit");
        targetEntityEntityData.put("TumorType", "primary");
        targetEntity.setData(targetEntityEntityData);
        Suggestion suggestion = new Suggestion(targetEntity);

        Map<String, Double> fieldsWeights = new HashMap<>();
        fieldsWeights.put("SampleDiagnosis", 0.8);
        fieldsWeights.put("OriginTissue", 0.2);
        fieldsWeights.put("TumorType", 0.2);

        double got = instance.calculateScoreAsPercentage(suggestion, sourceEntity, fieldsWeights);

        assertTrue(got < 85);
    }
}