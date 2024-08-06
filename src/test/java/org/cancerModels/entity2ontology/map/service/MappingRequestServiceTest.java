package org.cancerModels.entity2ontology.map.service;

import org.cancerModels.entity2ontology.common.utils.FileUtils;
import org.cancerModels.entity2ontology.common.utils.JsonConverter;
import org.cancerModels.entity2ontology.map.model.MappingRequest;
import org.cancerModels.entity2ontology.map.model.MappingResponse;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MappingRequestServiceTest {

    private static final String DATA_DIR = "src/test/resources/mappingRequestReader/";
    private static final String OUTPUT_DATA_DIR = "src/test/output/";

    @Test
    void shouldProcessMappingRequestWithRequestFile() throws IOException {
        // Given a file that has right data
        String fileToRead = DATA_DIR + "mappingRequest.json";

        // When we process the request
        MappingRequestService mappingRequestService = new MappingRequestService();
        String outputFileName = OUTPUT_DATA_DIR + "mapping_request_with_file_output.json";
        mappingRequestService.processMappingRequest(fileToRead, outputFileName);

        // We get an output file with the results of the mapping process
        File jsonFile = FileUtils.getNonEmptyFileFromPath(outputFileName);
        MappingResponse mappingResponse = JsonConverter.fromJsonFile(jsonFile, MappingResponse.class);
        assertEquals("IndexPath", mappingResponse.getIndexPath());
        assertNotNull(mappingResponse.getMappingsResults());
        assertEquals(1, mappingResponse.getMappingsResults().size());

        if (!jsonFile.delete()) {
            System.out.println("Failed to delete the file.");
        }
    }

    @Test
    void shouldProcessMappingRequestWithRequestObject() throws IOException {
        // Given a mapping request object
        MappingRequest request = MappingIO.readMappingRequest(DATA_DIR + "mappingRequest.json");

        // When we process the request
        MappingRequestService mappingRequestService = new MappingRequestService();
        MappingResponse mappingResponse = mappingRequestService.processMappingRequest(request);

        // We get an output file with the results of the mapping process
        assertEquals("IndexPath", mappingResponse.getIndexPath());
        assertNotNull(mappingResponse.getMappingsResults());
        assertEquals(1, mappingResponse.getMappingsResults().size());
    }
}
