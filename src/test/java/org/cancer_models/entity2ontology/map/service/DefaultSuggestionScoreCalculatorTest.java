package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.common.EntityCreatorUtil;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.cancer_models.entity2ontology.map.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSuggestionScoreCalculatorTest {

    private final DefaultSuggestionScoreCalculator instance = new DefaultSuggestionScoreCalculator();

    private final TemplateQueryProcessor templateQueryProcessor = new TemplateQueryProcessor();

    private static final String DIAGNOSIS_TEMPLATE_TEXT = "${TumorType} ${OriginTissue} ${SampleDiagnosis}";
    private static final QueryTemplate DIAGNOSIS_TEMPLATE = new QueryTemplate(DIAGNOSIS_TEMPLATE_TEXT);

    private static final String TREATMENT_TEMPLATE_TEXT = "${TreatmentName}";
    private static final QueryTemplate TREATMENT_TEMPLATE = new QueryTemplate(TREATMENT_TEMPLATE_TEXT);

    private static final String CONFIGURATION_FILE =
        "src/test/resources/mappingConfigurations/pdcmMappingConfiguration.json";

    private final MappingConfiguration config;

    DefaultSuggestionScoreCalculatorTest() throws IOException {
        config = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);
    }

    // Section 1: Testing score for diagnosis with rule suggestions

    @Test
    void testComputeScore_ruleDiagnosisWithPerfectMatch_perfectScore() {
        String key = "key_1";

        String sourceSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String sourceOriginTissue = "orbit";
        String sourceTumorType = "primary";

        String targetSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String targetOriginTissue = "orbit";
        String targetTumorType = "primary";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createDiagnosisSourceEntity(key, sourceSampleDiagnosis, sourceOriginTissue, sourceTumorType);
        String label = "";
        String url = "";
        TargetEntity targetEntity = EntityCreatorUtil.createDiagnosisRuleTargetEntity(
            key, targetSampleDiagnosis, targetOriginTissue, targetTumorType, label, url);

        Suggestion suggestion = new Suggestion(targetEntity);

        double score = instance.computeScoreRule(suggestion, sourceEntity, config);
        assertEquals(100, score);
    }

    @Test
    void testComputeScore_ruleDiagnosisNoMatchOriginTissue_lessPerfectScore() {
        String key = "key_1";

        String sourceSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String sourceOriginTissue = "orbit";
        String sourceTumorType = "primary";

        String targetSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String targetOriginTissue = "dummy";
        String targetTumorType = "primary";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createDiagnosisSourceEntity(key, sourceSampleDiagnosis, sourceOriginTissue, sourceTumorType);
        String label = "";
        String url = "";
        TargetEntity targetEntity = EntityCreatorUtil.createDiagnosisRuleTargetEntity(
            key, targetSampleDiagnosis, targetOriginTissue, targetTumorType, label, url);

        Suggestion suggestion = new Suggestion(targetEntity);

        // 25 is the relevance of OriginTissue, for which we expect a contribution of 0 in this case
        double expectedScore = 100 - 25;

        double score = instance.computeScoreRule(suggestion, sourceEntity, config);
        assertEquals(expectedScore, score);
    }

    @Test
    void testComputeScore_ruleDiagnosisNoMatchTumorType_lessPerfectScore() {
        String key = "key_1";

        String sourceSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String sourceOriginTissue = "orbit";
        String sourceTumorType = "primary";

        String targetSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String targetOriginTissue = "orbit";
        String targetTumorType = "metastatic";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createDiagnosisSourceEntity(key, sourceSampleDiagnosis, sourceOriginTissue, sourceTumorType);
        String label = "";
        String url = "";
        TargetEntity targetEntity = EntityCreatorUtil.createDiagnosisRuleTargetEntity(
            key, targetSampleDiagnosis, targetOriginTissue, targetTumorType, label, url);

        Suggestion suggestion = new Suggestion(targetEntity);

        // 25 is the relevance of TumorType, for which we expect a contribution of 0 in this case
        double expectedScore = 100 - 25;

        double score = instance.computeScoreRule(suggestion, sourceEntity, config);
        assertEquals(expectedScore, score);
    }

    @Test
    void testComputeScore_ruleDiagnosisNoMatchSampleDiagnosis_lessPerfectScore() {
        String key = "key_1";

        String sourceSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String sourceOriginTissue = "orbit";
        String sourceTumorType = "primary";

        String targetSampleDiagnosis = "dummy";
        String targetOriginTissue = "orbit";
        String targetTumorType = "primary";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createDiagnosisSourceEntity(key, sourceSampleDiagnosis, sourceOriginTissue, sourceTumorType);
        String label = "";
        String url = "";
        TargetEntity targetEntity = EntityCreatorUtil.createDiagnosisRuleTargetEntity(
            key, targetSampleDiagnosis, targetOriginTissue, targetTumorType, label, url);

        Suggestion suggestion = new Suggestion(targetEntity);

        // 50 is the relevance of SampleDiagnosis, for which we expect a contribution of 0 in this case
        double expectedScore = 100 - 50;

        double score = instance.computeScoreRule(suggestion, sourceEntity, config);
        assertEquals(expectedScore, score);
    }

    // Section 2: Testing score for treatments with rule suggestions

    @Test
    void testComputeScore_ruleTreatmentWithPerfectMatch_perfectScore() {
        String key = "key_1";

        String sourceTreatmentName = "cisplatin";

        String targetTreatmentName = "cisplatin";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createTreatmentSourceEntity(key, sourceTreatmentName);
        String label = "";
        String url = "";
        TargetEntity targetEntity = EntityCreatorUtil.createTreatmentRuleTargetEntity(
            key, targetTreatmentName, label, url);

        Suggestion suggestion = new Suggestion(targetEntity);

        double score = instance.computeScoreRule(suggestion, sourceEntity, config);

        assertEquals(100, score);
    }

    @Test
    void testComputeScore_ruleTreatmentWithAlmostPerfectMatch_almostPerfectScore() {
        String key = "key_1";

        String sourceTreatmentName = "cisplatin";

        String targetTreatmentName = "cisplatine";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createTreatmentSourceEntity(key, sourceTreatmentName);
        String label = "";
        String url = "";
        TargetEntity targetEntity = EntityCreatorUtil.createTreatmentRuleTargetEntity(
            key, targetTreatmentName, label, url);

        Suggestion suggestion = new Suggestion(targetEntity);

        double score = instance.computeScoreRule(suggestion, sourceEntity, config);

        assertEquals(90, score);
    }

    @Test
    void testComputeScore_ruleTreatmentWithAlmostPerfectMatchExtraWord_almostPerfectScore() {
        String key = "key_1";

        String sourceTreatmentName = "chemotherapy nos";

        String targetTreatmentName = "Chemotherapy";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createTreatmentSourceEntity(key, sourceTreatmentName);
        String label = "";
        String url = "";
        TargetEntity targetEntity = EntityCreatorUtil.createTreatmentRuleTargetEntity(
            key, targetTreatmentName, label, url);

        Suggestion suggestion = new Suggestion(targetEntity);

        double score = instance.computeScoreRule(suggestion, sourceEntity, config);

        assertEquals(80, score);
    }

    // Section 3: Testing score for diagnosis with ontology suggestions

    @Test
    void testComputeScore_ontologyDiagnosisWithPerfectMatchLabel_perfectScore() {
        String key = "key_1";

        String sourceSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String sourceOriginTissue = "orbit";
        String sourceTumorType = "primary";

        String targetLabel = "primary orbit fusion negative rhabdomyosarcoma";
        List<String> targetSynonyms = Arrays.asList("");
        String targetUrl = "url";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createDiagnosisSourceEntity(key, sourceSampleDiagnosis, sourceOriginTissue, sourceTumorType);

        TargetEntity targetEntity = EntityCreatorUtil.createDiagnosisOntologyTargetEntity(
            key, targetLabel, targetSynonyms, targetUrl);

        Suggestion suggestion = new Suggestion(targetEntity);
        List<SearchQueryItem> items = templateQueryProcessor.extractSearchQueryItems(
            DIAGNOSIS_TEMPLATE, sourceEntity, config.getFieldsWeightsByEntityType("diagnosis"));
        ScoringDetails scoringDetails = new ScoringDetails();
        scoringDetails.setSearchQueryItems(items);
        suggestion.setScoringDetails(scoringDetails);
        suggestion.setTermLabel(targetLabel);

        double score = instance.computeScoreOntology(suggestion);

        String expectedScoringDetailsNote = "Matched label:[primary orbit fusion negative rhabdomyosarcoma]";
        String obtainedScoringDetailsNote = suggestion.getScoringDetails().getNote();

        assertEquals(100, score);
        assertEquals(expectedScoringDetailsNote, obtainedScoringDetailsNote);
    }

    @Test
    void testComputeScore_ontologyDiagnosisWithPerfectMatchSynonym_perfectScore() {
        String key = "key_1";

        String sourceSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String sourceOriginTissue = "orbit";
        String sourceTumorType = "primary";

        String targetLabel = "-";
        List<String> targetSynonyms = Arrays.asList("primary orbit fusion negative rhabdomyosarcoma");
        String targetUrl = "url";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createDiagnosisSourceEntity(key, sourceSampleDiagnosis, sourceOriginTissue, sourceTumorType);

        TargetEntity targetEntity = EntityCreatorUtil.createDiagnosisOntologyTargetEntity(
            key, targetLabel, targetSynonyms, targetUrl);

        Suggestion suggestion = new Suggestion(targetEntity);
        List<SearchQueryItem> items = templateQueryProcessor.extractSearchQueryItems(
            DIAGNOSIS_TEMPLATE, sourceEntity, config.getFieldsWeightsByEntityType("diagnosis"));
        ScoringDetails scoringDetails = new ScoringDetails();
        scoringDetails.setSearchQueryItems(items);
        suggestion.setScoringDetails(scoringDetails);
        suggestion.setTermLabel(targetLabel);

        double score = instance.computeScoreOntology(suggestion);

        String expectedScoringDetailsNote = "Matched synonym:[primary orbit fusion negative rhabdomyosarcoma]";
        String obtainedScoringDetailsNote = suggestion.getScoringDetails().getNote();

        assertEquals(99, score);
        assertEquals(expectedScoringDetailsNote, obtainedScoringDetailsNote);
    }

    @Test
    void testComputeScore_ontologyDiagnosisWithAlmostPerfectMatchLabel_lessPerfectScore() {
        String key = "key_1";

        String sourceSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String sourceOriginTissue = "orbit";
        String sourceTumorType = "primary";

        String targetLabel = "primary orbit fusion positive rhabdomyosarcoma";
        List<String> targetSynonyms = Arrays.asList("s1");
        String targetUrl = "url";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createDiagnosisSourceEntity(key, sourceSampleDiagnosis, sourceOriginTissue, sourceTumorType);

        TargetEntity targetEntity = EntityCreatorUtil.createDiagnosisOntologyTargetEntity(
            key, targetLabel, targetSynonyms, targetUrl);

        Suggestion suggestion = new Suggestion(targetEntity);
        List<SearchQueryItem> items = templateQueryProcessor.extractSearchQueryItems(
            DIAGNOSIS_TEMPLATE, sourceEntity, config.getFieldsWeightsByEntityType("diagnosis"));
        ScoringDetails scoringDetails = new ScoringDetails();
        scoringDetails.setSearchQueryItems(items);
        suggestion.setScoringDetails(scoringDetails);
        suggestion.setTermLabel(targetLabel);

        double score = instance.computeScoreOntology(suggestion);

        String expectedScoringDetailsNote = "Matched label:[primary orbit fusion positive rhabdomyosarcoma]";
        String obtainedScoringDetailsNote = suggestion.getScoringDetails().getNote();

        assertTrue(score > 65.0 && score < 100);
        assertEquals(expectedScoringDetailsNote, obtainedScoringDetailsNote);
    }

    @Test
    void testComputeScore_ontologyDiagnosisNonMeaningfulContent_perfectScore() {
        String key = "key_1";

        String sourceSampleDiagnosis = "fusion negative rhabdomyosarcoma";
        String sourceOriginTissue = "orbit";
        String sourceTumorType = "unknown";

        String targetLabel = "orbit fusion negative rhabdomyosarcoma";
        List<String> targetSynonyms = Arrays.asList("s1");
        String targetUrl = "url";

        SourceEntity sourceEntity =
            EntityCreatorUtil.createDiagnosisSourceEntity(key, sourceSampleDiagnosis, sourceOriginTissue, sourceTumorType);

        TargetEntity targetEntity = EntityCreatorUtil.createDiagnosisOntologyTargetEntity(
            key, targetLabel, targetSynonyms, targetUrl);

        Suggestion suggestion = new Suggestion(targetEntity);
        List<SearchQueryItem> items = templateQueryProcessor.extractSearchQueryItems(
            DIAGNOSIS_TEMPLATE, sourceEntity, config.getFieldsWeightsByEntityType("diagnosis"));
        ScoringDetails scoringDetails = new ScoringDetails();
        scoringDetails.setSearchQueryItems(items);
        suggestion.setScoringDetails(scoringDetails);
        suggestion.setTermLabel(targetLabel);

        double score = instance.computeScoreOntology(suggestion);

        String expectedScoringDetailsNote = "Matched label:[orbit fusion negative rhabdomyosarcoma]";
        String obtainedScoringDetailsNote = suggestion.getScoringDetails().getNote();

        assertTrue(score > 99.9);
        assertEquals(expectedScoringDetailsNote, obtainedScoringDetailsNote);
    }

}
