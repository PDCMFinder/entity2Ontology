package org.cancer_models.entity2ontology.index.service;

import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.cancer_models.entity2ontology.index.model.OntologyLocation;

import java.io.IOException;
import java.util.List;

/**
 * A class implementing this interface must provide logic to convert a {@link OntologyLocation} into a list of
 * {@link TargetEntity}.
 */
public interface OntologyExtractor {
    /**
     * Extracts a list of {@link TargetEntity} objects from the specified {@link OntologyLocation}.
     *
     * <p>
     * This method downloads the ontology from the given URL, processes it to extract the relevant branches,
     * and converts the extracted data into a list of {@link TargetEntity} objects.
     * </p>
     *
     * @param ontologyLocation the location of the ontology, including the URL and branches of interest
     * @return a list of {@link TargetEntity} objects representing the extracted ontology data
     */
    List<TargetEntity> extract(OntologyLocation ontologyLocation) throws IOException;
}
