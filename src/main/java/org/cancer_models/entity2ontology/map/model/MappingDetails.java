package org.cancer_models.entity2ontology.map.model;

import lombok.Data;

import java.util.List;

/**
 * A class that contains some internal details about how the mapping was done.
 */
@Data
public class MappingDetails {
    /**
     * Indicates if the match was found using an exact search.
     */
    private boolean exactMatch;
    /**
     * If the suggestion was found on an ontology term, this stores the query items used to found
     * the suggestion.
     */
    private List<SearchQueryItem> searchQueryItems;
}
