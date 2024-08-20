package org.cancerModels.entity2ontology.map.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a suggestion for mapping a source entity to an ontology term.
 * Each suggestion includes a label, a URL, and a score indicating the match quality.
 */
@Data
@NoArgsConstructor
public class Suggestion {

    // Indicates the origin of the data that lead to this suggestion. It could be directly an ontology term, or it could
    // be an already existing association between entity-ontology term (a Rule).
    private String type;

    // The id to the ontology term or rule that represents this suggestion
    private String targetId;

    // A reference to the {@link SourceEntity} for which the suggestion was created
    //private SourceEntity sourceEntity;

    // Label of the ontology term
    private String termLabel;

    // Url of the ontology term
    private String termUrl;

    // Score representing how close the suggestion is to the source entity (0 - 100)
    private double score;
}
