package org.cancerModels.entity2ontology.index.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * IndexingRequest represents the information needed to create a Lucene index.
 *
 * <p>This class contains all necessary attributes to define an indexing request, including the path
 * where the index will be stored and the sources of information to be indexed. This can include
 * ontology data, existing mappings, and other relevant data sources.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * IndexingRequest request = new IndexingRequest("/path/to/index", List.of("ontology1.owl", "mappings.json"));
 * }
 * </pre>
 *
 * <p>The {@code IndexingRequest} class is a key component of the indexing process, ensuring that
 * all relevant data is properly included and organized for efficient indexing and retrieval.
 *
 * @see org.cancerModels.entity2ontology.index.command.IndexCommand
 * @see org.cancerModels.entity2ontology.index.service.IndexingService
 */
@Data
@NoArgsConstructor
public class IndexingRequest {
    /**
     * The path to the directory where the index will be created
     */
    private String indexPath;

    /**
     * The locations of the rules to index
     */
    private List<RuleLocation> ruleLocations;
}
