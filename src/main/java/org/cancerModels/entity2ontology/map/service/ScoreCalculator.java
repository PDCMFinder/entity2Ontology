package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.map.model.SourceEntity;
import org.cancerModels.entity2ontology.map.model.Suggestion;

/**
 * A class that helps with the calculation of the score for a {@link Suggestion}. The score is a percentage that
 * informs about how close a suggestion is respect to the {@link SourceEntity} for which it was created.
 *
 * The similarity between both is calculated using a string similarity algorithm.
 */
public class ScoreCalculator {
    /**
     * Calculates the suggestion score as a percentage, based on how similar the suggestion and the sourceEntity are.
     * A string similarity comparison is used.
     * Note that {@link Suggestion} has a `rawScore` assigned by Lucene. We are not using it as that's a value that helps
     * in sorting results but doesn't say anything about how good the result by its own is.
     * @param suggestion The suggestion for the mapping
     * @param sourceEntity The entity we are trying to map
     * @return A number (percentage) representing how similar the suggestion and the source entity are.
     */
    public double calculateScoreAsPercentage(Suggestion suggestion, SourceEntity sourceEntity) {
        System.out.println("calculateScoreAsPercentage");
        System.out.println(suggestion.getTargetEntity().getData());
        System.out.println(sourceEntity.getData());
        return 0;
    }
}
