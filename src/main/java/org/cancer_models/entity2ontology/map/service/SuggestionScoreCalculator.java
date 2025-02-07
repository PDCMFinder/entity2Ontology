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
     * Calculates the score of a {@code suggestion} based on how similar it is respect to a {@code sourceEntity},
     * when the suggestion was obtained using an exact match (so it's more "strict" when
     * evaluating the similarity). {@code configuration} is used to provide additional information like the
     * relevance of fields.
     * @param suggestion The suggestion found in the mapping process
     * @param sourceEntity The entity for which the suggestion was found
     * @param configuration A configuration object with additional information about the mapping process
     * @return a number between 0 and 100 indicating how good the suggestion is.
     */
    double computeScoreExactMatch(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration);

    /**
     * Calculates the score of a {@code suggestion} based on how similar it is respect to a {@code sourceEntity},
     * when the suggestion was obtained using a similar match (so it's less "strict" when
     * evaluating the similarity). {@code configuration} is used to provide additional information like the
     * relevance of fields.
     * @param suggestion The suggestion found in the mapping process
     * @param sourceEntity The entity for which the suggestion was found
     * @param configuration A configuration object with additional information about the mapping process
     * @return a number between 0 and 100 indicating how good the suggestion is.
     */
    double computeScoreSimilarMatch(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration);

    // THIS COULD REPLACE THE OTHER METHODS
    /**
     * Calculates the score of a {@code suggestion} based on how similar it is respect to a {@code sourceEntity}.
     * The object {@code configuration} is used to provide additional information like the
     * relevance of fields.
     * @param suggestion The suggestion found in the mapping process
     * @param sourceEntity The entity for which the suggestion was found
     * @param configuration A configuration object with additional information about the mapping process
     * @return a number between 0 and 100 indicating how good the suggestion is.
     */
    double computeScore(Suggestion suggestion, SourceEntity sourceEntity, MappingConfiguration configuration);

}
