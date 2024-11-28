package org.cancer_models.entity2ontology.index.model;

import org.cancer_models.entity2ontology.common.model.TargetEntity;

import java.util.Map;

/**
 * Represents the location of a JSON file which contains rules.
 *
 * <p>This record encapsulates the information about a rule set, including the path to the JSON file
 * that contains the rules and the name of the rule set. The JSON file specified by {@code filePath}
 * should contain the associations between entities and ontologies.
 *
 * @param filePath         Path to the JSON file containing a set of rules (association between entities and ontologies)
 * @param name             Name of the rule set
 * @param ignore           Flag that if set to true ignores this ruleset to be indexed (or re-indexed). Useful when you only need to
 *                         index specific JSON files or ontologies
 * @param fieldsConversion A map to convert the fields of a rule into those of a {@link TargetEntity}
 */
public record RuleLocation(String filePath, String name, boolean ignore, Map<String, String> fieldsConversion) {
}
