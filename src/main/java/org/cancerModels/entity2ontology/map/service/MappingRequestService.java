package org.cancerModels.entity2ontology.map.service;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.cancerModels.entity2ontology.map.model.*;

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
public class MappingRequestService {

    private static Logger logger = LogManager.getLogger(MappingRequestService.class);

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
        MappingResponse response = processMappingRequest(request);
        MappingIO.writeMappingResponse(response, outputFile);
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
     * @throws IOException if an error occurs while writing to the output file
     */
    public MappingResponse processMappingRequest(MappingRequest request) {
        logger.info("Processing mapping request");
        MappingResponse response = new MappingResponse();
        // Set the time the mapping process starts
        response.setStart(LocalDateTime.now());

        List<MappingResponseEntry> entries = new ArrayList<>();

        request.getEntities().forEach(e -> entries.add(processEntity(e)));

        response.setMappingsResults(entries);

        // Set the time the mapping process ends
        response.setEnd(LocalDateTime.now());

        logger.info("Ended processing mapping request");
        return response;
    }

    /**
     * Gets the list of suggestions for an entity and creates a MappingResponseEntry object with that information
     * @param entity Entity to map
     * @return a MappingResponseEntry object (entity - list of suggestions)
     */
    private MappingResponseEntry processEntity(SourceEntity entity) {
        logger.info("Processing entity: {}", entity);
        MappingResponseEntry entry = new MappingResponseEntry();
        // For now let's assign a list with a dummy result
        List<Suggestion> dummy = new ArrayList<>();
        Suggestion suggestion1 = new Suggestion();
        suggestion1.setTargetId("reference_1");
        suggestion1.setType("type_1");
        //suggestion1.setSourceEntity(entity);
        suggestion1.setScore(5);
        suggestion1.setTermLabel("label_term_1");
        suggestion1.setTermUrl("term_url_1");
        dummy.add(suggestion1);

        Suggestion suggestion2 = new Suggestion();
        suggestion2.setTargetId("reference_2");
        suggestion2.setType("type_2");
        //suggestion2.setSourceEntity(entity);
        suggestion2.setScore(0);
        suggestion2.setTermLabel("label_term_2");
        suggestion2.setTermUrl("term_url_2");
        dummy.add(suggestion2);

        entry.setEntity(entity);
        entry.setSuggestions(dummy);
        return entry;
    }
}
