package org.cancerModels.entity2ontology.index.service;

import org.cancerModels.entity2ontology.index.model.IndexingResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexingRequestServiceTest {

    private static final String DATA_DIR = "src/test/resources/indexingRequest/";

    private final IndexingRequestService instance = new IndexingRequestService();

    @Test
    public void shouldProcessIndexingRequestByFile() throws IOException {
        // Given a file that has right data
        String fileToRead = DATA_DIR + "indexingRequestTreatment.json";

        // When we process an indexing request by passing a file
        IndexingResponse response = instance.processRequest(fileToRead);

        // Then we get the expected data
        assertEquals("IndexPath1" , response.getIndexPath());
        int indexedTreatments = response.getIndexedElementsPerTarget().get("treatment");
        assertEquals(2 , indexedTreatments);
    }
}
