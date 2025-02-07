package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.common.EntityCreatorUtil;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SearchQueryItem;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSuggestionScoreCalculatorTest {

    private final DefaultSuggestionScoreCalculator instance = new DefaultSuggestionScoreCalculator();

    private static final String CONFIGURATION_FILE =
        "src/test/resources/mappingConfigurations/pdcmMappingConfiguration.json";

    private final MappingConfiguration config;

    DefaultSuggestionScoreCalculatorTest() throws IOException {
        config = MappingIO.readMappingConfiguration(CONFIGURATION_FILE);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

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

        double score = instance.computeScore(suggestion, sourceEntity, config);
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

        double score = instance.computeScore(suggestion, sourceEntity, config);
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
        System.out.println(config.getConfigurationByEntityType("diagnosis").getFields());

        double score = instance.computeScore(suggestion, sourceEntity, config);
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

        // 25 is the relevance of SampleDiagnosis, for which we expect a contribution of 0 in this case
        double expectedScore = 100 - 50;
        System.out.println(config.getConfigurationByEntityType("diagnosis").getFields());

        double score = instance.computeScore(suggestion, sourceEntity, config);
        assertEquals(expectedScore, score);
    }



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

        double score = instance.computeScore(suggestion, sourceEntity, config);
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

        double score = instance.computeScore(suggestion, sourceEntity, config);
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

        double score = instance.computeScore(suggestion, sourceEntity, config);
        assertEquals(80, score);
    }

    private List<SearchQueryItem> buildDiagnosisItems(String sampleDiagnosis, String tumorType, String originTissue) {
        List<SearchQueryItem> items = new ArrayList<>();

        SearchQueryItem item1 = SearchQueryItem.builder()
            .field("SampleDiagnosis")
            .value(sampleDiagnosis)
            .weight(10)
            .build();

        SearchQueryItem item2 = SearchQueryItem.builder()
            .field("TumourType")
            .value(tumorType)
            .weight(1)
            .build();

        SearchQueryItem item3 = SearchQueryItem.builder()
            .field("PrimarySite")
            .value(originTissue)
            .weight(5)
            .build();

        items.add(item1);
        items.add(item2);
        items.add(item3);

        return items;
    }

    @Test
    void testCalculateSearchQueryItemsAndTextSimilarity_diagnosis_sameData_perfectScore() {
        String sampleDiagnosis = "non small cell carcinoma";
        String tumourType = "metastatic";
        String primarySite = "lung";
        String targetText = "Lung metastatic Non Small Cell Carcinoma";
        List<SearchQueryItem> queryItems = buildDiagnosisItems(sampleDiagnosis, tumourType, primarySite);

        double score = instance.calculateSearchQueryItemsAndTextSimilarity(queryItems, targetText);

        assertEquals(100, score);
    }

    @Test
    void testCalculateSearchQueryItemsAndTextSimilarity_diagnosis_sameDataWithSeparator_perfectScore() {
        String sampleDiagnosis = "non small cell carcinoma";
        String tumourType = "metastatic";
        String primarySite = "lung";
        String targetText = "Lung metastatic Non-Small Cell Carcinoma";
        List<SearchQueryItem> queryItems = buildDiagnosisItems(sampleDiagnosis, tumourType, primarySite);

        double score = instance.calculateSearchQueryItemsAndTextSimilarity(queryItems, targetText);

        assertEquals(100, score);
    }

    @Test
    void testCalculateSearchQueryItemsAndTextSimilarity_diagnosis_sameDataNoOrder_perfectScore() {
        String sampleDiagnosis = "ductal breast carcinoma";
        String tumourType = "metastatic";
        String primarySite = "breast";
        String targetText = "Metastatic Breast Ductal Carcinoma";
        List<SearchQueryItem> queryItems = buildDiagnosisItems(sampleDiagnosis, tumourType, primarySite);

        double score = instance.calculateSearchQueryItemsAndTextSimilarity(queryItems, targetText);

        assertEquals(100, score);
    }

    @Test
    void testCalculateSearchQueryItemsAndTextSimilarity_diagnosis_sameDataNonMeaningfulContent_perfectScore() {
        String sampleDiagnosis = "ductal breast carcinoma";
        String tumourType = "unknown";
        String primarySite = "breast";
        String targetText = "Breast Ductal Carcinoma";
        List<SearchQueryItem> queryItems = buildDiagnosisItems(sampleDiagnosis, tumourType, primarySite);

        double score = instance.calculateSearchQueryItemsAndTextSimilarity(queryItems, targetText);

        assertEquals(100, score);
    }

    @Test
    void testCalculateSearchQueryItemsAndTextSimilarity_diagnosis_extraDataTarget_lessPerfectScore() {
        String sampleDiagnosis = "ductal breast carcinoma";
        String tumourType = "metastatic";
        String primarySite = "breast";
        String targetText = "Metastatic Breast Ductal Carcinoma xyz";
        List<SearchQueryItem> queryItems = buildDiagnosisItems(sampleDiagnosis, tumourType, primarySite);

        double score = instance.calculateSearchQueryItemsAndTextSimilarity(queryItems, targetText);
        System.out.println(score);
        assertTrue(score > 90 && score < 100);
    }
}
