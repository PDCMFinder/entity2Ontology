package org.cancerModels.entity2ontology.map.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the result of a mapping process.
 */
@Data
public class MappingResponse {

    /**
     * Time when the mapping process started
     */
    private LocalDateTime start;

    /**
     * Time when the mapping process finished
     */
    private LocalDateTime end;

    /**
     * The name of the index used in the process.
     */
    private String indexPath;

    /**
     * The list of mapping response entries. Each entry contains a source entity and its list of suggestions.
     */
    private List<MappingResponseEntry> mappingsResults;
}
