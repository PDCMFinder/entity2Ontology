package org.cancer_models.entity2ontology.index.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancer_models.entity2ontology.common.utils.FileUtils;
import org.cancer_models.entity2ontology.common.utils.JsonConverter;
import org.cancer_models.entity2ontology.index.model.IndexingRequest;
import org.cancer_models.entity2ontology.index.model.IndexingResponse;
import org.cancer_models.entity2ontology.index.model.OntologyLocation;
import org.cancer_models.entity2ontology.index.model.RuleLocation;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for processing {@link IndexingRequest} objects.
 *
 * <p>This class provides methods to handle the indexing process based on the information contained
 * in an {@code IndexingRequest}. It uses the specified data sources to create a Lucene index at the
 * defined path. This includes reading ontology files, existing mappings, and other data sources,
 * and then indexing them to facilitate efficient searching and retrieval.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * IndexingRequest request = new IndexingRequest("/path/to/index", List.of("ontology1.owl", "mappings.json"));
 * IndexingRequestService service = new IndexingRequestService();
 * service.processRequest(request);
 * }
 * </pre>
 *
 * @see org.cancer_models.entity2ontology.index.model.IndexingRequest
 * @see org.apache.lucene.index.IndexWriter
 */
@Component
public class IndexingRequestService {

    private static final Logger logger = LogManager.getLogger(IndexingRequestService.class);
    private final IndexingService indexingService;

    public IndexingRequestService(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    /**
     * Reads a {@link IndexingRequest} from the specified JSON file, performs the indexing process,
     * and returns a {@link IndexingResponse} with the results of the process
     *
     * <p>This method reads the data sources specified in the {@code IndexingRequest} and indexes
     * them into a Lucene index at the path defined in the request.
     *
     * @param requestFile path to a JSON file with a {@link IndexingRequest} containing the information needed
     *                    for indexing
     * @return  a {@link IndexingResponse} with the results on the indexing process
     * @throws IOException if there is an error reading the data sources or writing to the index
     */
    public IndexingResponse processRequest(String requestFile) throws IOException {
        IndexingRequest request = readIndexingRequest(requestFile);
        return processRequest(request);
    }

    /**
     * Processes the given {@link IndexingRequest} to create a Lucene index and returns a {@link IndexingResponse}
     * with the results of the process
     *
     * <p>This method reads the data sources specified in the {@code IndexingRequest} and indexes
     * them into a Lucene index at the path defined in the request.
     *
     * @param request the {@link IndexingRequest} containing the information needed for indexing
     * @return  a {@link IndexingResponse} with the results on the indexing process
     * @throws IOException if there is an error reading the data sources or writing to the index
     */
    public IndexingResponse processRequest(IndexingRequest request) throws IOException {
        logger.info("Processing request: {}", request);

        // Set the time the mapping process starts
        LocalDateTime start = LocalDateTime.now();

        Map<String, Integer> indexedElementsPerLocation = new HashMap<>();

        // Process the rules defined in the rule locations, if any (and excluding the ones that need to be ignored)
        if (request.ruleLocations() != null) {
            for (RuleLocation ruleLocation : request.ruleLocations()) {
                if (!ruleLocation.ignore()) {
                    int count = processRuleLocation(ruleLocation, request.indexPath());
                    indexedElementsPerLocation.put(ruleLocation.name(), count);
                }
            }
        }

        // Process the ontologies defined in the ontology locations, if any (and excluding the ones that need to be ignored)
        if (request.ontologyLocations() != null) {
            for (OntologyLocation ontologyLocation : request.ontologyLocations()) {
                if (!ontologyLocation.ignore()) {
                    int count = processOntologyLocation(ontologyLocation, request.indexPath());
                    indexedElementsPerLocation.put(ontologyLocation.name(), count);
                }
            }
        }

        LocalDateTime end = LocalDateTime.now();
        return new IndexingResponse(
            start, end, request.indexPath(), indexedElementsPerLocation
        );
    }

    /**
     * Processes a ruleset by reading the JSON file from the given location and indexing the data
     * @param ruleLocation {@link RuleLocation} which contains the path and an identifier for the ruleset
     * @param indexPath Path to the Lucene index
     * @return the number of indexed elements
     */
    private int processRuleLocation(RuleLocation ruleLocation, String indexPath) throws IOException {
        return indexingService.indexRules(ruleLocation, indexPath);
    }

    /**
     * Processes a ruleset by reading the JSON file from the given location and indexing the data
     * @param ontologyLocation {@link OntologyLocation} which contains the path and an identifier for the ontologies
     * @param indexPath Path to the Lucene index
     * @return the number of indexed elements
     */
    private int processOntologyLocation(OntologyLocation ontologyLocation, String indexPath) throws IOException {
        return indexingService.indexOntologies(ontologyLocation, indexPath);
    }

    /**
     * Reads a {@link IndexingRequest} from a JSON file.
     *
     * @param jsonFilePath the JSON file path
     * @return the {@link IndexingRequest}
     * @throws IOException if an error occurs while reading the file
     */
    public static IndexingRequest readIndexingRequest(String jsonFilePath) throws IOException {
        File jsonFile = FileUtils.getNonEmptyFileFromPath(jsonFilePath);
        return JsonConverter.fromJsonFile(jsonFile, IndexingRequest.class);
    }
}
