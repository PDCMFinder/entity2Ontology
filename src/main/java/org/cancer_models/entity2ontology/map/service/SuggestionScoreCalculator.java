package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;

/**
 * Defines the interface for classes that implement the calculation of the score for suggestions.
 * The score is a value between 0 and 100 (equivalent to a percentage) that indicates how good a
 * {@link Suggestion} is respect to a {@link SourceEntity}.
 */
public interface SuggestionScoreCalculator {

    /**
     * Calculates the suggestion (a rule) score as a percentage, based on how similar the suggestion and the sourceEntity are.
     * A string similarity comparison is used.
     * Note that {@link Suggestion} has a `rawScore` assigned by Lucene. We are not using it as that's a value that helps
     * in sorting results but doesn't say anything about how good the result by its own is.
     *
     * @param suggestion    The suggestion for the mapping
     * @param sourceEntity  The entity we are trying to map
     * @param configuration A configuration object with additional information about the mapping process
     * @return A number (percentage) representing how similar the suggestion and the source entity are.
     */
    double computeScoreRule(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration);

    /**
     * Calculates the suggestion (an ontology term) score as a percentage, based on how similar the suggestion and the
     * sourceEntity are.
     * A string similarity comparison is used.
     * Note that {@link Suggestion} has a `rawScore` assigned by Lucene. We are not using it as that's a value that helps
     * in sorting results but doesn't say anything about how good the result by its own is.
     * @param suggestion  The suggestion for the mapping
     * @return A number (percentage) representing how similar the suggestion and the source entity are.
     */
    double computeScoreOntology(Suggestion suggestion);
}
