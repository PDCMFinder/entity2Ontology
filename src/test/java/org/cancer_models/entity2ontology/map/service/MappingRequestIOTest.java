package org.cancer_models.entity2ontology.map.service;

import org.cancer_models.entity2ontology.map.model.MappingRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MappingRequestIOTest {

    private static final String DATA_DIR = "src/test/resources/mappingRequestReader/";

    @Test
    void shouldCreateMappingRequestWhenFileHasCorrectData() throws IOException {
        // Given a file that has right data
        String fileToRead = DATA_DIR + "mappingRequest.json";

        // When we read the file
        MappingRequest mappingRequest = MappingIO.readMappingRequest(fileToRead);

        // Then we get the expected data
        assertNotNull(mappingRequest);
        assertEquals(5, mappingRequest.getMaxSuggestions());
        assertNotNull(mappingRequest.getEntities());
        assertEquals(1, mappingRequest.getEntities().size());
        assertEquals("key_1", mappingRequest.getEntities().getFirst().getId());
        assertNotNull(mappingRequest.getEntities().getFirst().getData());
        assertEquals("bladder", mappingRequest.getEntities().getFirst().getData().get("OriginTissue"));
        assertEquals("recurrent", mappingRequest.getEntities().getFirst().getData().get("TumorType"));
        assertEquals(
            "t2 transitional cell carcinoma",
            mappingRequest.getEntities().getFirst().getData().get("SampleDiagnosis"));
        assertEquals("jax", mappingRequest.getEntities().getFirst().getData().get("DataSource"));
    }

    @Test
    void shouldFailWhenFileDoesNotExist() {
        // Given a file that doesn't exist
        String fileToRead = DATA_DIR + "NonExisting.json";

        // When we try to read the file
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            MappingIO.readMappingRequest(fileToRead);
        });
        // Then we get an IOException
        assertEquals(fileToRead + " (No such file or directory)", exception.getMessage());
    }

    @Test
    void shouldFailWhenFileIsEmpty() {
        // Given a file that is empty
        String fileToRead = DATA_DIR + "emptyFile.json";

        // When we try to read the file
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        {
            MappingIO.readMappingRequest(fileToRead);
        });
        // Then we get an IOException
        assertEquals("File is empty: " + fileToRead, exception.getMessage());
    }

}
