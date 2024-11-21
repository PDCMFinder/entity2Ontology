package org.cancer_models.entity2ontology.index.service;

import org.cancer_models.entity2ontology.common.utils.FileUtils;
import org.cancer_models.entity2ontology.index.model.IndexingRequest;
import org.cancer_models.entity2ontology.index.model.IndexingResponse;
import org.cancer_models.entity2ontology.index.model.OntologyLocation;
import org.cancer_models.entity2ontology.index.model.RuleLocation;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        FileUtils.deleteRecursively(new File(response.getIndexPath()));
    }

    @Test
    public void shouldReturnIndexingRequestWhenFileHasCorrectData() throws IOException {
        // Given a file that has right data
        String fileToRead = DATA_DIR + "indexingRequest.json";

        // When we read the file
        IndexingRequest indexingRequest = IndexingRequestService.readIndexingRequest(fileToRead);

        // Then we get the expected data

        assertNotNull(indexingRequest);
        assertEquals("IndexPath1", indexingRequest.getIndexPath());

        // Validate rule locations

        List<RuleLocation> ruleLocations = indexingRequest.getRuleLocations();

        assertNotNull(ruleLocations, "Expected to have rule locations");
        assertEquals(3, ruleLocations.size(), "unexpected number of rule locations");

        RuleLocation ruleLocation1 = ruleLocations.get(0);
        assertEquals("/path/file/treatments.json", ruleLocation1.getFilePath());
        assertEquals("treatment", ruleLocation1.getName());
        assertFalse(ruleLocation1.isIgnore());

        RuleLocation ruleLocation2 = ruleLocations.get(1);
        assertEquals("/path/file/diagnosis.json", ruleLocation2.getFilePath());
        assertEquals("diagnosis", ruleLocation2.getName());
        assertFalse(ruleLocation2.isIgnore());

        RuleLocation ruleLocation3 = ruleLocations.get(2);
        assertEquals("/path/file/to_be_ignored.json", ruleLocation3.getFilePath());
        assertEquals("to_be_ignored", ruleLocation3.getName());
        assertTrue(ruleLocation3.isIgnore());

        // Validate ontology locations

        List<OntologyLocation> ontologyLocations = indexingRequest.getOntologyLocations();

        assertNotNull(ontologyLocations, "Expected to have ontology locations");
        assertEquals(1, ontologyLocations.size(), "unexpected number of ontology locations");

        OntologyLocation ontLocation1 = ontologyLocations.get(0);
        assertEquals("ncit", ontLocation1.getOntoId());
        assertEquals("ncit ontology diagnosis", ontLocation1.getName());
        assertEquals(Arrays.asList("NCIT_C9305", "NCIT_C3262"), ontLocation1.getBranches());
        assertFalse(ontLocation1.isIgnore());
    }

    @Test
    void shouldFailWhenFileDoesNotExist() {
        // Given a file that doesn't exist
        String fileToRead = DATA_DIR + "NonExisting.json";

        // When we try to read the file
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            IndexingRequestService.readIndexingRequest(fileToRead));
        // Then we get an IOException
        assertEquals(fileToRead + " (No such file or directory)", exception.getMessage());
    }

    @Test
    public void shouldFailWhenFileIsEmpty() {
        // Given a file that is empty
        String fileToRead = DATA_DIR + "emptyFile.json";

        // When we try to read the file
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            IndexingRequestService.readIndexingRequest(fileToRead));
        // Then we get an IOException
        assertEquals("File is empty: " + fileToRead, exception.getMessage());
    }
}
