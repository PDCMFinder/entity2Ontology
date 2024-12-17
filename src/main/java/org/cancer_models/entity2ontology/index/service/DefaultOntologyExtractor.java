package org.cancer_models.entity2ontology.index.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancer_models.entity2ontology.common.model.OntologyTerm;
import org.cancer_models.entity2ontology.common.model.TargetEntityDataFields;
import org.cancer_models.entity2ontology.index.model.OntologyLocation;
import org.cancer_models.entity2ontology.common.model.TargetEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * The OntologyExtractor class is responsible for downloading and processing ontologies from specified locations.
 * It converts the downloaded ontologies into a list of {@link TargetEntity} objects based on the given branches of interest.
 *
 * <p>
 * This class uses an {@link OntologyLocation} object, which contains the URL of the ontology and the specific branches
 * that are relevant for the extraction process.
 * </p>
 *
 * <p>
 * Usage example:
 * <pre>
 *     OntologyLocation location = new OntologyLocation("http://example.com/ontology.owl", List.of("branch1", "branch2"));
 *     OntologyExtractor extractor = new DefaultOntologyExtractor();
 *     List&lt;TargetEntity&gt; targetEntities = extractor.extract(location);
 * </pre>
 * </p>
 *
 * @see OntologyLocation
 * @see TargetEntity
 */
@Component
class DefaultOntologyExtractor implements OntologyExtractor {

    private final OntologyDownloader ontologyDownloader = new OntologyDownloader();
    private static final Logger logger = LogManager.getLogger(DefaultOntologyExtractor.class);

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
    public List<TargetEntity> extract(OntologyLocation ontologyLocation) throws IOException {
        List<TargetEntity> targetEntities = new ArrayList<>();
        Set<OntologyTerm> ontologyTerms = new HashSet<>();
        for (String branch : ontologyLocation.branches()) {
            logger.info("Processing branch {}", branch);
            Set<OntologyTerm> ontologyTermsByBranch = downloadOntologyTerms(
                ontologyLocation.ontoId(), branch, ontologyLocation.name());
            if (ontologyTermsByBranch != null) {
                ontologyTerms.addAll(ontologyTermsByBranch);
            }
        }
        ontologyTerms.forEach(ontologyTerm -> targetEntities.add(termToTargetEntity(ontologyTerm)));
        return targetEntities;
    }

    private TargetEntity termToTargetEntity(OntologyTerm ontologyTerm) {

        TargetEntityDataFields dataFields = new TargetEntityDataFields();
        dataFields.addStringField("label", ontologyTerm.label());
        dataFields.addStringField("description",  ontologyTerm.description());
        dataFields.addListField("synonyms", formatSynonyms(ontologyTerm));


        return new TargetEntity(
            ontologyTerm.id(), ontologyTerm.type(), "ontology", dataFields, ontologyTerm.label(), ontologyTerm.url());
    }

    private List<String> formatSynonyms(OntologyTerm ontologyTerm) {
        Set<String> uniqueValues = new HashSet<>();
        ontologyTerm.synonyms().forEach(e -> {
            uniqueValues.add(e.toLowerCase());
        });
        // We don't need the synonyms to contain the value that the label already has
        uniqueValues.remove(ontologyTerm.label().toLowerCase());
        return new ArrayList<>(uniqueValues);
    }

    Set<OntologyTerm> downloadOntologyTerms(String ontologyId, String termId, String type) throws IOException {
        return ontologyDownloader.downloadOntologyTerms(ontologyId, termId, type);
    }
}