package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.map.model.MappingConfiguration;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;
import org.cancerModels.entity2ontology.map.model.TargetEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        Map<String, Double> fieldsWeights = getFieldConfigurations();

        double got = instance.calculateScoreAsPercentage(suggestion, sourceEntity, fieldsWeights);
        assertEquals(100.0, got);
    }

    private static Map<String, Double> getFieldConfigurations() {
        List<MappingConfiguration.FieldConfiguration> fields = new ArrayList<>();

        MappingConfiguration.FieldConfiguration sampleDiagnosisField = new MappingConfiguration.FieldConfiguration();
        sampleDiagnosisField.setName("SampleDiagnosis");
        sampleDiagnosisField.setWeight(1);

        MappingConfiguration.FieldConfiguration originTissueField = new MappingConfiguration.FieldConfiguration();
        originTissueField.setName("OriginTissue");
        originTissueField.setWeight(1);

        MappingConfiguration.FieldConfiguration tumorTypeField = new MappingConfiguration.FieldConfiguration();
        tumorTypeField.setName("TumorType");
        tumorTypeField.setWeight(1);

        fields.add(sampleDiagnosisField);
        fields.add(originTissueField);
        fields.add(tumorTypeField);

        Map<String, Double> fieldsWeights = new HashMap<>();
        for (MappingConfiguration.FieldConfiguration field : fields) {
            fieldsWeights.put(field.getName(), field.getWeight());
        }
        return fieldsWeights;
    }

    @Test
    void calculateSimilarity() {
        double similarity = instance.calculateStringsSimilarityPercentage("a", "b");
//        var r = instance.calculateScorePerField("a", "a", 1, 3);
//        System.out.println(r);
       System.out.println(similarity);
    }
}