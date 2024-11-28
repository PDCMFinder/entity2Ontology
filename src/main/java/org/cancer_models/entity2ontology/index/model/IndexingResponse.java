package org.cancer_models.entity2ontology.index.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the result of an indexing process.
 *
 * @param start                    Time when the indexing process started
 * @param end                      Time when the indexing process finished
 * @param indexPath                The path of the created index
 * @param indexedElementsPerTarget Number of indexed elements per target
 */
public record IndexingResponse(
    LocalDateTime start, LocalDateTime end, String indexPath, Map<String, Integer> indexedElementsPerTarget) {
}
