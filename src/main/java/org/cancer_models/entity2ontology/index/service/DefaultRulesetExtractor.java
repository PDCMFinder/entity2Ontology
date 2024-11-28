package org.cancer_models.entity2ontology.index.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cancer_models.entity2ontology.common.utils.FileUtils;
import org.cancer_models.entity2ontology.index.model.RuleLocation;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Extractor class for processing rule sets and transforming them into target entities.
 *
 * <p>This class is responsible for reading rules from a given {@link RuleLocation} and transforming
 * them into {@link TargetEntity} objects. These target entities are then used as documents for the
 * Lucene index.
 *
 * <p>The {@code RulesetExtractor} handles the logic of reading the rule set from the specified
 * location and converting each rule into a target entity that can be indexed by Lucene.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * RuleLocation ruleLocation = new RuleLocation();
 * ruleLocation.setFilePath("/path/to/rules.json");
 * ruleLocation.setName("Example Rule Set");
 *
 * RulesetExtractor extractor = new DefaultRulesetExtractor();
 * List<TargetEntity> targetEntities = extractor.extract(ruleLocation);
 * for (TargetEntity entity : targetEntities) {
 *     System.out.println(entity);
 * }
 * }
 * </pre>
 *
 * @see RuleLocation
 * @see TargetEntity
 */
@Component
class DefaultRulesetExtractor implements RulesetExtractor {

    /**
     * Extracts rules from the given {@link RuleLocation} and transforms them into a list of {@link TargetEntity}.
     *
     * <p>This method reads the rule set from the location specified in the {@code RuleLocation} and converts each
     * rule into a {@code TargetEntity}. The resulting list of target entities can then be used as documents for
     * the Lucene index.
     *
     * @param ruleLocation the information about the rules to be extracted
     * @return a list of {@link TargetEntity} objects transformed from the original rules
     * @throws IOException if there is an error reading the rule set from the specified location
     */
    public List<TargetEntity> extract(RuleLocation ruleLocation) throws IOException {
        List<TargetEntity> targetEntities = new ArrayList<>();
        validateRuleSetTarget(ruleLocation);
        File jsonFile = FileUtils.getNonEmptyFileFromPath(ruleLocation.filePath());
        Map<String, String> fieldsConversion = ruleLocation.fieldsConversion();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonFile);

        if (rootNode.isArray()) {
            for (JsonNode ruleNode : rootNode) {

                String id = getText(ruleNode, "id", fieldsConversion);
                String entityType = getText(ruleNode, "entityType", fieldsConversion);
                JsonNode dataNode = getObject(ruleNode, "data", fieldsConversion);
                String url = getText(ruleNode, "url", fieldsConversion);
                String label = getText(ruleNode, "label", fieldsConversion);
                Map<String, Object> data = new HashMap<>();

                // Process the mapping values
                Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    data.put(field.getKey(), field.getValue().textValue());
                }
                TargetEntity targetEntity = new TargetEntity(id, entityType, "rule", data, label, url);
                targetEntities.add(targetEntity);
            }
        } else {
            throw new IllegalArgumentException("Invalid JSON format in the ruleset file. Expected an array.");
        }
        return targetEntities;
    }

    private String getText(
        JsonNode ruleNode, String targetEntityField, Map<String, String> fieldsConversion) {
        return getObject(ruleNode, targetEntityField, fieldsConversion).asText();
    }

    private JsonNode getObject(
        JsonNode ruleNode, String targetEntityField, Map<String, String> fieldsConversion) {
        String originalRuleField = fieldsConversion.get(targetEntityField);

        if ( !ruleNode.has(originalRuleField)) {
            throw new IllegalArgumentException(
                "The rule does not have a property called '" + originalRuleField
                    + "' (the mapping for the field "+targetEntityField+")");
        }
        JsonNode node = ruleNode.path(originalRuleField);
        if (node == null) {
            throw new IllegalArgumentException(
                "Invalid JSON format in the ruleset file. " + originalRuleField + " is empty.");
        }
        return node;
    }

    private void validateRuleSetTarget(RuleLocation ruleLocation) {
        if (ruleLocation == null) {
            throw new IllegalArgumentException("Invalid ruleset target. It must not be null.");
        }
        if (ruleLocation.filePath() == null || ruleLocation.filePath().isEmpty()) {
            throw new IllegalArgumentException("Invalid ruleset target. File path is empty.");
        }
        if (ruleLocation.name() == null || ruleLocation.name().isEmpty()) {
            throw new IllegalArgumentException("Invalid ruleset target. Name is empty.");
        }

        Map<String, String> fieldsConversion = ruleLocation.fieldsConversion();

        if (fieldsConversion== null || fieldsConversion.isEmpty()) {
            throw new IllegalArgumentException("Invalid ruleset target. FieldsConversion is empty.");
        }
        validateFieldConversionField(fieldsConversion, "id");
        validateFieldConversionField(fieldsConversion, "entityType");
        validateFieldConversionField(fieldsConversion, "data");
        validateFieldConversionField(fieldsConversion, "label");
        validateFieldConversionField(fieldsConversion, "url");
    }

    private void validateFieldConversionField(Map<String, String> entry, String fieldName) {
        if (!entry.containsKey(fieldName)) {
            throw new IllegalArgumentException(
                "Invalid ruleset target. Field conversion section is missing an entry for '" + fieldName + "'");
        }
        if (entry.get(fieldName) == null || entry.get(fieldName).isEmpty()) {
            throw new IllegalArgumentException(
                "Invalid ruleset target. Field conversion section has an empty value for '" + fieldName);
        }
    }
}
