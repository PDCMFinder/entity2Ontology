package org.cancerModels.entity2ontology.map.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a suggestion for mapping a source entity to an ontology term.
 * Each suggestion includes a label, a URL, and a score indicating the match quality.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Suggestion {

    public Suggestion(TargetEntity targetEntity) {
        this.targetEntity = targetEntity;
        this.uniqueSuggestionId = calculateUniqueSuggestionId(targetEntity);
    }

    // Unique id for the suggestion. Useful to keep unique elements in a collection. Formed as the
    // concatenation of targetEntity targetType  + "|" + targetEntity entityType  + "|" + targetEntity id
    @EqualsAndHashCode.Include
    String uniqueSuggestionId;

    // Indicates the origin of the data that lead to this suggestion. It could be directly an ontology term, or it could
    // be an already existing association between entity-ontology term (a Rule).
    //private String type;

    // The id to the ontology term or rule that represents this suggestion
    //private String targetId;

    // A reference to the {@link TargetEntity} from which this suggestion was created
    private TargetEntity targetEntity;

    // Label of the ontology term
    private String termLabel;

    // Url of the ontology term
    private String termUrl;

    // Score representing how close the suggestion is to the source entity (0 - 100)
    private double score;

    // Score given by Lucene. This is a number given by Lucene and allows to sort by relevance, but it is not
    // normalised so needs to be processed to know what the `score` of the suggestion will be.
    private double rawScore;

    public String calculateUniqueSuggestionId(TargetEntity targetEntity) {
        return targetEntity.getTargetType() + "|" + targetEntity.getEntityType() + "|" + targetEntity.getId();
    }
}
