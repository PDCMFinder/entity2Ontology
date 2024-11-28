package org.cancer_models.entity2ontology.index.model;

import java.util.List;

/**
 * IndexingRequest represents the information needed to create a Lucene index.
 *
 * <p>This class contains all necessary attributes to define an indexing request, including the path
 * where the index will be stored and the sources of information to be indexed. This can include
 * ontology data, existing mappings, and other relevant data sources.
 *
 * @param indexPath         The path to the directory where the index will be created
 * @param ruleLocations     The locations of the rules to index
 * @param ontologyLocations The locations of the ontologies to index
 * @see org.cancer_models.entity2ontology.index.command.IndexCommand
 * @see org.cancer_models.entity2ontology.index.service.IndexingService
 */
public record IndexingRequest(
    String indexPath, List<RuleLocation> ruleLocations, List<OntologyLocation> ontologyLocations) {
}
