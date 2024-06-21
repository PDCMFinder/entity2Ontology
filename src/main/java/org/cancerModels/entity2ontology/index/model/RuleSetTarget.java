package org.cancerModels.entity2ontology.index.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a set of rules used for mapping entities to ontologies.
 *
 * <p>This class encapsulates the information about a rule set, including the path to the JSON file
 * that contains the rules and the name of the rule set. The JSON file specified by {@code filePath}
 * should contain the associations between entities and ontologies.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * RuleSet ruleSet = new RuleSetTarget();
 * ruleSet.setFilePath("/path/to/rules.json");
 * ruleSet.setName("Example Rule Set");
 * }
 * </pre>
 */
@Data
@NoArgsConstructor
public class RuleSetTarget {
    /**
     * Path to the JSON file containing a set of rules (association between entities and ontologies)
     */
    private String filePath;

    /**
     * Name of the rule set
     */
    private String name;

    /**
     * Flag that if set to true ignores this ruleset to be indexed (or re-indexed). Useful when you only need to
     * index specific JSON files or ontologies
     */
    private boolean ignore;


    Map<String, String> fieldsConversion;
}
