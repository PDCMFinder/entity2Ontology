package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.cancerModels.entity2ontology.map.model.TargetEntity;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculatorTest {

    private ScoreCalculator instance = new ScoreCalculator();

    @Test
    void calculateScoreAsPercentage() {

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

        double got = instance.calculateScoreAsPercentage(suggestion, sourceEntity);
        assertEquals(1, got);
    }
}