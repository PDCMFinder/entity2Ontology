package org.cancer_models.entity2ontology.index.model;

import java.util.List;


/**
 * The OntologyLocation record represents the location of an ontology and specifies the branches of interest
 * for extraction.
 *
 * <p>
 * This class contains the URL of the ontology and a list of branches that are relevant for the extraction process.
 * </p>
 *
 * @param ontoId   The id of the ontology.
 * @param name     A name for the ontology. Useful if several are used, and we need to identify them.
 * @param branches If specified, only these specific branches will be downloaded. Useful if the expected matches fall into
 *                 specific categories within the ontology, and we want to narrow the search space.
 *                 Branches are ontology terms in the ontology, for instance: ["NCIT_C9305", "NCIT_C3262"]
 *                 ("Malignant Neoplasm" and "Neoplasm", respectively).
 * @param ignore   Flag that if set to true ignores this ruleset to be indexed (or re-indexed). Useful when you only need to
 *                 index specific JSON files or ontologies
 */
public record OntologyLocation(String ontoId, String name, List<String> branches, boolean ignore) {

}
