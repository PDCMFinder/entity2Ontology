package org.cancer_models.entity2ontology.common.model;

import java.util.List;

/**
 * Represents an ontology term with its associated metadata.
 *
 * @param id          The unique identifier of the ontology term.
 * @param label       The label or name of the ontology term.
 * @param type        A broad category to make it easy to identify what the term represents (a treatment, a diagnosis, etc.).
 * @param url         The URL of the ontology term.
 * @param description The definition of the ontology term.
 * @param synonyms    The list of synonyms for the ontology term.
 */
public record OntologyTerm(
    String id, String url, String label, String type, String description, List<String> synonyms) {
}
