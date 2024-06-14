package org.cancerModels.entity2ontology.map.model;

import lombok.Data;

import java.util.List;

/**
 * Represents an entry in the mapping response.
 * Contains a source entity and its associated list of mapping suggestions.
 */
@Data
public class MappingResponseEntry {
    /**
     * The source entity being mapped.
     */
    private SourceEntity entity;

    /**
     * The list of suggestions for the source entity.
     */
    private List<Suggestion> suggestions;
}
