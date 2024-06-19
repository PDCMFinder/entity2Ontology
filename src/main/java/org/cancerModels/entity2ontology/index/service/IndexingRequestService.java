package org.cancerModels.entity2ontology.index.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cancerModels.entity2ontology.index.model.IndexingRequest;
import org.cancerModels.entity2ontology.index.model.IndexingResponse;
import org.cancerModels.entity2ontology.index.model.RuleSetTarget;

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
 * @see org.cancerModels.entity2ontology.index.model.IndexingRequest
 * @see org.apache.lucene.index.IndexWriter
 */
public class IndexingRequestService {

    private static final Logger logger = LogManager.getLogger(IndexingRequestService.class);
    private final IndexingService indexingService = new IndexingService();

    /**
     * Reads a {@link IndexingRequest} from the specified JSON file, performs the indexing process,
     * and returns a {@link IndexingResponse} with the results of the process
     *
     * <p>This method reads the data sources specified in the {@code IndexingRequest} and indexes
     * them into a Lucene index at the path defined in the request. It handles the opening and
     * closing of the {@link IndexWriter}, ensuring that all resources are properly managed.
     *
     * @param requestFile path to a JSON file with a {@link IndexingRequest} containing the information needed
     *                    for indexing
     * @return  a {@link IndexingResponse} with the results on the indexing process
     * @throws IOException if there is an error reading the data sources or writing to the index
     */
    public IndexingResponse processRequest(String requestFile) throws IOException {
        IndexingRequest request = IndexingRequestReader.readIndexingRequest(requestFile);
        return processRequest(request);
    }

    /**
     * Processes the given {@link IndexingRequest} to create a Lucene index and returns a {@link IndexingResponse}
     * with the results of the process
     *
     * <p>This method reads the data sources specified in the {@code IndexingRequest} and indexes
     * them into a Lucene index at the path defined in the request. It handles the opening and
     * closing of the {@link IndexWriter}, ensuring that all resources are properly managed.
     *
     * @param request the {@link IndexingRequest} containing the information needed for indexing
     * @return  a {@link IndexingResponse} with the results on the indexing process
     * @throws IOException if there is an error reading the data sources or writing to the index
     */
    public IndexingResponse processRequest(IndexingRequest request) throws IOException {
        logger.info("Processing request: {}", request);
        IndexingResponse response = new IndexingResponse();
        // Set the time the mapping process starts
        response.setStart(LocalDateTime.now());
        response.setIndexPath(request.getIndexPath());

        Map<String, Integer> indexedElementsPerTarget = new HashMap<>();

        // Process the ruleset targets, if any (and excluding the ones that need to be ignored)
        request.getRuleSetTargets().forEach(s -> {
            if (!s.isIgnore()) {
                int count = processRuleSetTarget(s, request.getIndexPath());
                indexedElementsPerTarget.put(s.getName(), count);
            }
        });

        response.setIndexedElementsPerTarget(indexedElementsPerTarget);
        response.setEnd(LocalDateTime.now());
        return response;
    }

    /**
     * Processes a ruleset by reading the JSON file from the given location and indexing the data
     * @param ruleSetTarget {@link RuleSetTarget} which contains the path and an identifier for the ruleset
     * @return the number of indexed elements
     */
    private int processRuleSetTarget(RuleSetTarget ruleSetTarget, String indexPath) {
        logger.info("Processing rule set target: {} ({})", ruleSetTarget.getFilePath(), ruleSetTarget.getName());
        return 0;
    }
}
