package org.cancerModels.entity2ontology.index.service;

import org.cancerModels.entity2ontology.index.model.IndexingRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IndexingRequestReaderTest {

    private static final String DATA_DIR = "src/test/resources/indexingRequest/";

    @Test
    public void shouldReturnIndexingRequestWhenFileHasCorrectData() throws IOException {
        // Given a file that has right data
        String fileToRead = DATA_DIR + "indexingRequest.json";

        // When we read the file
        IndexingRequest indexingRequest = IndexingRequestReader.readIndexingRequest(fileToRead);

        // Then we get the expected data
        assertNotNull(indexingRequest);
        assertEquals("IndexPath1", indexingRequest.getIndexPath());
        assertNotNull(indexingRequest.getRuleLocations(), "Expected to have ruleSet sources");
        assertEquals(3, indexingRequest.getRuleLocations().size(), "unexpected number of ruleset sources");
        assertEquals("/path/file/treatments.json", indexingRequest.getRuleLocations().get(0).getFilePath());
        assertEquals("treatment", indexingRequest.getRuleLocations().get(0).getName());
        assertFalse(indexingRequest.getRuleLocations().get(0).isIgnore());
        assertEquals("/path/file/diagnosis.json", indexingRequest.getRuleLocations().get(1).getFilePath());
        assertEquals("diagnosis", indexingRequest.getRuleLocations().get(1).getName());
        assertFalse(indexingRequest.getRuleLocations().get(1).isIgnore());
        assertEquals("/path/file/to_be_ignored.json", indexingRequest.getRuleLocations().get(2).getFilePath());
        assertEquals("to_be_ignored", indexingRequest.getRuleLocations().get(2).getName());
        assertTrue(indexingRequest.getRuleLocations().get(2).isIgnore());
    }

    @Test
    void shouldFailWhenFileDoesNotExist() {
        // Given a file that doesn't exist
        String fileToRead = DATA_DIR + "NonExisting.json";

        // When we try to read the file
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            IndexingRequestReader.readIndexingRequest(fileToRead));
        // Then we get an IOException
        assertEquals(fileToRead + " (No such file or directory)", exception.getMessage());
    }

    @Test
    public void shouldFailWhenFileIsEmpty() {
        // Given a file that is empty
        String fileToRead = DATA_DIR + "emptyFile.json";

        // When we try to read the file
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            IndexingRequestReader.readIndexingRequest(fileToRead));
        // Then we get an IOException
        assertEquals("File is empty: " + fileToRead, exception.getMessage());
    }
}