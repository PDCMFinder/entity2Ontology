package org.cancer_models.entity2ontology.index.service;

import org.cancer_models.entity2ontology.index.model.RuleLocation;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RulesetExtractorTest {

    private static final String TEST_FOLDER = "src/test/resources/indexingRequest/rules/";
    private final RulesetExtractor rulesetExtractor = new RulesetExtractor();

    // Happy path
    @Test
    void shouldGetExpectedRuleset() throws IOException {
        // Given a ruleSetTarget with valid information
        RuleLocation ruleLocation = buildRuleLocation();

        // When we extract the ruleset
        List<TargetEntity> entities = rulesetExtractor.extract(ruleLocation);

        // Then we get the expected ruleset

        assertEquals(2, entities.size());

        TargetEntity entity1 = entities.get(0);
        TargetEntity entity2 = entities.get(1);

        assertEquals(
            "000af81b6bc1715523ea537847899c3c7a1f589d1404b302cfeddce2a997eaec",
            entity1.id());
        assertEquals("treatment", entity1.entityType());
        Map<String, Object> data1 = new HashMap<>();
        data1.put("DataSource", "chop");
        data1.put("TreatmentName", "cisplatin");
        assertEquals(data1, entity1.data());
        assertEquals("Cisplatin", entity1.label());
        assertEquals("http://purl.obolibrary.org/obo/NCIT_C376", entity1.url());

        assertEquals(
            "0055a7ad3bb5270aa9a6e53a95d5b730a11d0709830c13b69594a046c9a00ec5",
            entity2.id());
        assertEquals("treatment", entity2.entityType());
        Map<String, Object> data2 = new HashMap<>();
        data2.put("DataSource", "pdmr");
        data2.put("TreatmentName", "dutasteride");
        assertEquals(data2, entity2.data());
        assertEquals("Dutasteride", entity2.label());
        assertEquals("http://purl.obolibrary.org/obo/NCIT_C47503", entity2.url());
    }

    @Test
    void shouldFailIfRuleSetTargetNull() {
        // Given a null ruleSetTarget
        // When we try to extract the ruleset
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            rulesetExtractor.extract(null));

        // Then we get an IOException
        assertEquals("Invalid ruleset target. It must not be null.", exception.getMessage());
    }

    @Test
    void shouldFailIfRuleSetNullFilePath() {
        // Given a ruleSetTarget without filePath
        RuleLocation ruleLocation = buildRuleLocation();
        ruleLocation.setFilePath(null);

        // When we try to extract the ruleset
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            rulesetExtractor.extract(ruleLocation));

        // Then we get an IOException
        assertEquals("Invalid ruleset target. File path is empty.", exception.getMessage());
    }

    @Test
    void shouldFailIfRuleSetFilePathNotFound() {
        // Given a ruleSetTarget without filePath
        RuleLocation ruleLocation = buildRuleLocation();
        ruleLocation.setFilePath("non_existent_file");

        // When we try to extract the ruleset
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            rulesetExtractor.extract(ruleLocation));

        // Then we get an IOException
        assertEquals("non_existent_file (No such file or directory)", exception.getMessage());
    }

    @Test
    void shouldFailIfRuleSetNullName() {
        // Given a ruleSetTarget without name
        RuleLocation ruleLocation = buildRuleLocation();
        ruleLocation.setName(null);

        // When we try to extract the ruleset
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            rulesetExtractor.extract(ruleLocation));

        // Then we get an IOException
        assertEquals("Invalid ruleset target. Name is empty.", exception.getMessage());
    }

    @Test
    void shouldFailIfRuleSetNullFieldsConversion() {
        // Given a ruleSetTarget without fields conversion
        RuleLocation ruleLocation = buildRuleLocation();
        ruleLocation.setFieldsConversion(null);

        // When we try to extract the ruleset
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            rulesetExtractor.extract(ruleLocation));

        // Then we get an IOException
        assertEquals("Invalid ruleset target. FieldsConversion is empty.", exception.getMessage());
    }

    @Test
    void shouldFailIfRuleSetEmptyFieldsConversion() {
        // Given a ruleSetTarget without fields conversion
        RuleLocation ruleLocation = buildRuleLocation();
        ruleLocation.setFieldsConversion(new HashMap<>());

        // When we try to extract the ruleset
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            rulesetExtractor.extract(ruleLocation));

        // Then we get an IOException
        assertEquals("Invalid ruleset target. FieldsConversion is empty.", exception.getMessage());
    }

    @Test
    void shouldFailIfRuleSetFieldsConversionWithoutIdField() {
        checkFieldInFieldsConversion("id");
    }

    @Test
    void shouldFailIfRuleSetFieldsConversionWithoutEntityTypeField() {
        checkFieldInFieldsConversion("entityType");
    }

    @Test
    void shouldFailIfRuleSetFieldsConversionWithoutDataField() {
        checkFieldInFieldsConversion("data");
    }

    @Test
    void shouldFailIfRuleSetFieldsConversionWithoutLabelField() {
        checkFieldInFieldsConversion("label");
    }

    @Test
    void shouldFailIfRuleSetFieldsConversionWithoutUrlField() {
        checkFieldInFieldsConversion("url");
    }

    // Errors in the rule

    @Test
    void shouldFailIfRuleWithoutEquivalentId() {
        checkMissingFieldInRule(
            "id", "mappingKey", "missing_id_treatment_mappings.json");
    }

    @Test
    void shouldFailIfRuleWithoutEquivalentEntityType() {
        checkMissingFieldInRule(
            "entityType", "entityType", "missing_entityType_treatment_mappings.json");
    }

    @Test
    void shouldFailIfRuleWithoutEquivalentData() {
        checkMissingFieldInRule(
            "data", "mappingValues", "missing_data_treatment_mappings.json");
    }

    @Test
    void shouldFailIfRuleWithoutEquivalentUrl() {
        checkMissingFieldInRule(
            "url", "mappedTermUrl", "missing_url_treatment_mappings.json");
    }

    @Test
    void shouldFailIfRuleWithoutEquivalentLabel() {
        checkMissingFieldInRule(
            "label", "mappedTermLabel", "missing_label_treatment_mappings.json");
    }

    private void checkFieldInFieldsConversion(String fieldName) {
        // Given a ruleSetTarget without an expected field in the FieldsConversion section
        RuleLocation ruleLocation = buildRuleLocation();
        ruleLocation.getFieldsConversion().remove(fieldName);

        // When we try to extract the ruleset
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            rulesetExtractor.extract(ruleLocation));

        // Then we get an IOException
        assertEquals(
            "Invalid ruleset target. Field conversion section is missing an entry for '" + fieldName + "'",
            exception.getMessage());
    }

    private void checkMissingFieldInRule(String fieldName, String equivalentName, String rulesetFileName) {
        // Given a ruleSetTarget without an expected field in the FieldsConversion section
        RuleLocation ruleLocation = buildRuleLocation();
        ruleLocation.setFilePath(TEST_FOLDER + rulesetFileName);

        // When we try to extract the ruleset
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            rulesetExtractor.extract(ruleLocation));

        // Then we get an IOException
        assertEquals(
            "The rule does not have a property called '" +
                equivalentName + "' (the mapping for the field " + fieldName + ")",
            exception.getMessage());
    }

    // Creates a valid RuleLocation to use in the tests
    private RuleLocation buildRuleLocation() {
        RuleLocation ruleLocation = new RuleLocation();
        ruleLocation.setName("name");
        Map<String, String> fieldsConversion = buildFieldsConversion();
        ruleLocation.setFieldsConversion(fieldsConversion);
        ruleLocation.setIgnore(false);
        ruleLocation.setFilePath(TEST_FOLDER + "/correct_treatment_mappings.json");
        return ruleLocation;
    }

    private Map<String, String> buildFieldsConversion() {
        Map<String, String> fieldsConversion = new HashMap<>();
        fieldsConversion.put("id", "mappingKey");
        fieldsConversion.put("entityType", "entityType");
        fieldsConversion.put("data", "mappingValues");
        fieldsConversion.put("label", "mappedTermLabel");
        fieldsConversion.put("url", "mappedTermUrl");
        return fieldsConversion;
    }
}