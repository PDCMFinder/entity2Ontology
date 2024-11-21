package org.cancer_models.entity2ontology.index.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the result of an indexing process.
 */
@Data
public class IndexingResponse {
    /**
     * Time when the indexing process started
     */
    private LocalDateTime start;

    /**
     * Time when the indexing process finished
     */
    private LocalDateTime end;

    /**
     * The path of the created index
     */
    private String indexPath;

    /**
     * Number of indexed elements per target
     */
    private Map<String, Integer> indexedElementsPerTarget;
}
