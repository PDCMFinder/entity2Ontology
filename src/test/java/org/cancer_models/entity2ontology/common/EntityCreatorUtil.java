package org.cancer_models.entity2ontology.common;

import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.cancer_models.entity2ontology.common.model.TargetEntityDataFields;
import org.cancer_models.entity2ontology.common.model.TargetEntityType;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;

import java.util.HashMap;
import java.util.Map;

public class EntityCreatorUtil {

    public static SourceEntity createDiagnosisSourceEntity(
        String key, String sampleDiagnosis, String originTissue, String tumorType) {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId(key);
        sourceEntity.setType("diagnosis");
        Map<String, String> sourceEntityData = new HashMap<>();
        sourceEntityData.put("SampleDiagnosis", sampleDiagnosis);
        sourceEntityData.put("OriginTissue", originTissue);
        sourceEntityData.put("TumorType", tumorType);
        sourceEntity.setData(sourceEntityData);

        return sourceEntity;
    }

    public static SourceEntity createTreatmentSourceEntity(String key, String treatmentName) {

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId(key);
        sourceEntity.setType("treatment");
        Map<String, String> sourceEntityData = new HashMap<>();
        sourceEntityData.put("TreatmentName", treatmentName);
        sourceEntity.setData(sourceEntityData);

        return sourceEntity;
    }

    public static TargetEntity createDiagnosisRuleTargetEntity(
        String key, String sampleDiagnosis, String originTissue, String tumorType, String label, String url) {
        TargetEntityDataFields dataFields = new TargetEntityDataFields();
        dataFields.addStringField("SampleDiagnosis", sampleDiagnosis);
        dataFields.addStringField("OriginTissue", originTissue);
        dataFields.addStringField("TumorType", tumorType);

        return new TargetEntity(
            key, "diagnosis", TargetEntityType.RULE, dataFields, label, url);
    }

    public static TargetEntity createTreatmentRuleTargetEntity(String key, String treatmentName, String label, String url) {
        TargetEntityDataFields dataFields = new TargetEntityDataFields();
        dataFields.addStringField("TreatmentName", treatmentName);

        return new TargetEntity(
            key, "treatment", TargetEntityType.RULE, dataFields, label, url);
    }
}
