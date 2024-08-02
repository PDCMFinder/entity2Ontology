package org.cancerModels.entity2ontology.index.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cancerModels.entity2ontology.index.service.OntologyExtractor;

import java.util.List;


/**
 * The OntologyLocation class represents the location of an ontology and specifies the branches of interest
 * for extraction.
 *
 * <p>
 * This class contains the URL of the ontology and a list of branches that are relevant for the extraction process.
 * It is used by the {@link OntologyExtractor} to download and process the ontology data.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 *     OntologyLocation location = new OntologyLocation("http://example.com/ontology.owl", List.of("branch1", "branch2"));
 * </pre>
 * </p>
 *
 * @see OntologyExtractor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyLocation {

    /**
     * The id of the ontology.
     */
    private String ontoId;

    /**
     * A name for the ontology. Useful if several are used and we need to identify them.
     */
    private String name;

    /**
     * If specified, only these specific branches will be downloaded. Useful if the expected matches fall into
     * specific categories within the ontology and we want to narrow the search space.
     * Branches are ontology terms in the ontology, for instance: ["NCIT_C9305", "NCIT_C3262"]
     * ("Malignant Neoplasm" and "Neoplasm", respectively).
     */
    private List<String> branches;

    /**
     * Flag that if set to true ignores this ruleset to be indexed (or re-indexed). Useful when you only need to
     * index specific JSON files or ontologies
     */
    private boolean ignore;
}
