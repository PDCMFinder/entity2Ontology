package org.cancer_models.entity2ontology.index.model;

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
 *
 * @see org.cancer_models.entity2ontology.index.command.IndexCommand
 * @see org.cancer_models.entity2ontology.index.service.IndexingService
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

    /**
     * The locations of the ontologies to index
     */
    private List<OntologyLocation> ontologyLocations;
}
