package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.exceptions.MappingException;
import org.cancer_models.entity2ontology.map.model.MappingConfiguration;
import org.cancer_models.entity2ontology.map.model.SourceEntity;
import org.cancer_models.entity2ontology.map.model.Suggestion;

import java.io.IOException;
import java.util.List;

/**
 * Defines the interface for classes that implement a search strategy to map entities.
 * Implementing classes must provide a way to find suggestions based on the given input.
 */
public interface SuggestionsFinder {

    /**
     * Retrieves a list of suggestions for a given {@code entity} based on the provided {@code config}.
     *
     * @param entity            the source entity to be mapped
     * @param indexPath         the path of the index to use for the mapping
     * @param maxNumSuggestions the maximum number of suggestions to retrieve
     * @param config            information about how to build the queries to find matches
     * @return a list of suggestions for the source entity
     * @throws MappingException if an error occurs during the search
     */
    List<Suggestion> findSuggestions(
        SourceEntity entity,
        String indexPath,
        int maxNumSuggestions,
        MappingConfiguration config) throws MappingException;
}
