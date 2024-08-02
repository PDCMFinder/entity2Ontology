package org.cancerModels.entity2ontology.index.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancerModels.entity2ontology.common.model.OntologyTerm;
import org.cancerModels.entity2ontology.index.model.OntologyLocation;
import org.cancerModels.entity2ontology.map.model.TargetEntity;

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
 *     OntologyExtractor extractor = new OntologyExtractor();
 *     List&lt;TargetEntity&gt; targetEntities = extractor.extract(location);
 * </pre>
 * </p>
 *
 * @see OntologyLocation
 * @see TargetEntity
 */
public class OntologyExtractor {

    private final OntologyDownloader ontologyDownloader = new OntologyDownloader();
    private static final Logger logger = LogManager.getLogger(OntologyExtractor.class);

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
     * @throws IOException if an I/O error occurs while downloading or processing the ontology
     */
    public List<TargetEntity> extract(OntologyLocation ontologyLocation) throws IOException {
        List<TargetEntity> targetEntities = new ArrayList<>();
        Set<OntologyTerm> ontologyTerms = new HashSet<>();
        ontologyLocation.getBranches().forEach(branch -> {
            logger.info("Processing branch {}", branch);
            try {
                Set<OntologyTerm> ontologyTermsByBranch = downloadOntologyTerms(
                    ontologyLocation.getOntoId(), branch, ontologyLocation.getName());
                if (ontologyTermsByBranch != null) {
                    ontologyTerms.addAll(ontologyTermsByBranch);
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        });
        ontologyTerms.forEach(ontologyTerm -> targetEntities.add(termToTargetEntity(ontologyTerm)));
        return targetEntities;
    }

    private TargetEntity termToTargetEntity(OntologyTerm ontologyTerm) {
        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setId(ontologyTerm.getId());
        targetEntity.setEntityType(ontologyTerm.getType());
        targetEntity.setTargetType("Ontology");
        targetEntity.setLabel(ontologyTerm.getLabel());
        targetEntity.setUrl(ontologyTerm.getUrl());
        Map<String, Object> data = new HashMap<>();
        data.put("label", ontologyTerm.getLabel());
        data.put("description", ontologyTerm.getDescription());
        data.put("synonyms", formatSynonyms(ontologyTerm));
        targetEntity.setData(data);
        return targetEntity;
    }

    private List<String> formatSynonyms(OntologyTerm ontologyTerm) {
        Set<String> uniqueValues = new HashSet<>();
        ontologyTerm.getSynonyms().forEach(e -> {
            uniqueValues.add(e.toLowerCase());
        });
        // We don't need the synonyms to contain the value that the label already has
        uniqueValues.remove(ontologyTerm.getLabel().toLowerCase());
        return new ArrayList<>(uniqueValues);
    }

    Set<OntologyTerm> downloadOntologyTerms(String ontologyId, String termId, String type) throws IOException {
        return ontologyDownloader.downloadOntologyTerms(ontologyId, termId, type);
    }
}