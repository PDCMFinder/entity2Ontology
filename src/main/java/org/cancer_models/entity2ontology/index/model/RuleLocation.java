package org.cancer_models.entity2ontology.index.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.cancer_models.entity2ontology.common.model.TargetEntity;

import java.util.Map;

/**
 * Represents the location of a JSON file which contains rules.
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
public class RuleLocation {
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

    /**
     * A map to convert the fields of a rule into those of a {@link TargetEntity}
     */
    private Map<String, String> fieldsConversion;
}
