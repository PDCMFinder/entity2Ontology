package org.cancer_models.entity2ontology.map.service;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.cancer_models.entity2ontology.map.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for processing mapping requests and generating mapping suggestions for each entity.
 * <p>
 * The {@code MappingService} class provides functionality to take a {@link MappingRequest} and return mapping
 * suggestions for each entity included in the request. The suggestions are returned as a map, where the key is the
 * source entity and the value is a list of suggested mappings.
 * </p>
 */
@Component
public class MappingRequestService {

    private static final Logger logger = LogManager.getLogger(MappingRequestService.class);

    private final MappingService mappingService;

    @Autowired
    public MappingRequestService(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    /**
     * Reads a {@link MappingRequest} from the specified JSON file, performs the mapping process,
     * and writes the results to the specified output file.
     *
     * <p>This method reads the mapping request from a JSON file, processes the entities, generates mapping suggestions,
     * and writes the results to the specified output file in JSON format.</p>
     *
     * <pre>
     * Example usage:
     * MappingService mappingService = new MappingService();
     * mappingService.performMapping("request.json", "output.json");
     * </pre>
     *
     * @param requestFile the path to the JSON file containing the {@link MappingRequest}
     * @param outputFile the path to the file where the mapping results will be written
     * @throws IOException if an error occurs while reading the request file or writing to the output file
     */
    public void processMappingRequest(String requestFile, String outputFile) throws IOException {
        MappingRequest request = MappingIO.readMappingRequest(requestFile);
        validateRequest(request);
        MappingResponse response = processMappingRequest(request);
        MappingIO.writeMappingResponse(response, outputFile);
    }

    private void validateRequest(MappingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Mapping request cannot be null");
        }
        if (request.getIndexPath() == null) {
            throw new IllegalArgumentException("Index path cannot be null");
        }
        if (request.getMappingConfigurationFile() == null) {
            throw new IllegalArgumentException("Mapping configuration file cannot be null");
        }
    }

    /**
     * Performs the mapping process based on the provided {@link MappingRequest} and returns a {@link MappingResponse}
     * with the results on the mapping process
     *
     * <pre>
     * Example usage:
     * MappingService mappingService = new MappingService();
     * MappingRequest request = // initialize your MappingRequest
     * MappingResponse response = mappingService.performMapping(request);
     * </pre>
     *
     * @param request the {@link MappingRequest} containing the entities to map and other relevant parameters
     * @return A {@link MappingResponse} with the results of the mapping process
     */
    public MappingResponse processMappingRequest(MappingRequest request) throws IOException {
        logger.info("Starts processing mapping request with {} entities", request.getEntities().size());
        MappingResponse response = new MappingResponse();

        response.setStart(LocalDateTime.now());
        response.setIndexPath(request.getIndexPath());

        MappingConfiguration config = readMappingConfiguration(request.getMappingConfigurationFile());

        List<MappingResponseEntry> entries = new ArrayList<>();

        for (SourceEntity entity : request.getEntities()) {
            entries.add(processEntity(entity, config, request.getIndexPath(), request.getMaxSuggestions()));
        }

        response.setMappingsResults(entries);

        // Set the time the mapping process ends
        response.setEnd(LocalDateTime.now());

        logger.info("Ended processing mapping request");
        return response;
    }

    private MappingConfiguration readMappingConfiguration(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Mapping configuration file path cannot be null");
        }

        MappingConfiguration config = MappingIO.readMappingConfiguration(filePath);

        if (config == null) {
            String error = String.format("Mapping configuration at path %s is empty or could not be read", filePath);
            throw new IllegalArgumentException(error);
        }

        return config;
    }

    /**
     * Gets the list of suggestions for an entity and creates a MappingResponseEntry object with that information
     *
     * @param entity         Entity to map
     * @param config         Configuration with the fields to use per entity type
     * @param indexPath      The location of the Lucene index to use
     * @param maxSuggestions Maximum number of suggestions
     * @return a MappingResponseEntry object (entity - list of suggestions)
     */
    private MappingResponseEntry processEntity(
        SourceEntity entity, MappingConfiguration config, String indexPath, int maxSuggestions) throws IOException {
        MappingResponseEntry entry = new MappingResponseEntry();
        List<Suggestion> suggestions = mappingService.mapEntity(entity, indexPath, maxSuggestions, config);
        entry.setEntity(entity);
        entry.setSuggestions(suggestions);
        return entry;
    }
}
