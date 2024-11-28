package org.cancer_models.entity2ontology.index.service;

import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.cancer_models.entity2ontology.index.model.RuleLocation;

import java.io.IOException;
import java.util.List;

/**
 * A class implementing this interface must provide logic to convert a {@link RuleLocation} into a list of
 * {@link TargetEntity}.
 */
public interface RulesetExtractor {

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
    List<TargetEntity> extract(RuleLocation ruleLocation) throws IOException;
}
