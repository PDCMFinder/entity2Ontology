package org.cancer_models.entity2ontology.common.model;

import lombok.Data;

import java.util.List;

/**
 * Represents an ontology term with its associated metadata.
 */
@Data
public class OntologyTerm {
    /**
     * The unique identifier of the ontology term.
     */
    private String id;

    /**
     * The label or name of the ontology term.
     */
    private String label;

    /**
     * A broad category to make it easy to identify what the term represents (a treatment, a diagnosis, etc.).
     */
    private String type;

    /**
     * The URL of the ontology term.
     */
    private String url;

    /**
     * The definition of the ontology term.
     */
    private String description;

    /**
     * The list of synonyms for the ontology term.
     */
    private List<String> synonyms;

    public OntologyTerm(
        String id,
        String url,
        String label,
        String type,
        String description,
        List<String> synonyms) {

        String updatedLabel = label;

        if ("diagnosis".equalsIgnoreCase(type)) {
            updatedLabel = updateTermLabel(updatedLabel);
        }

        this.id = id;
        this.url = url;
        this.type = type;
        this.label = updatedLabel;
        this.description = description;
        this.synonyms = synonyms;
    }

    private String updateTermLabel(String termLabel){
        // Changes Malignant * Neoplasm to * Cancer
        String pattern = "(.*)Malignant(.*)Neoplasm(.*)";
        String updatedTermLabel = termLabel;

        if (termLabel.matches(pattern)) {
            updatedTermLabel = (termLabel.replaceAll(pattern, "\t$1$2Cancer$3")).trim();
        }
        return updatedTermLabel;
    }
}
