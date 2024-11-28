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

    private final RulesetExtractor rulesetExtractor = new DefaultRulesetExtractor();
    private final OntologyExtractor ontologyExtractor = new DefaultOntologyExtractor();
    private final Indexer indexer = new Indexer();
    private final IndexingService indexingService = new IndexingService(rulesetExtractor, ontologyExtractor, indexer);
    private final IndexingRequestService instance = new IndexingRequestService(indexingService);

    @Test
    void shouldProcessIndexingRequestByFile() throws IOException {
        // Given a file that has right data
        String fileToRead = DATA_DIR + "indexingRequestTreatment.json";

        // When we process an indexing request by passing a file
        IndexingResponse response = instance.processRequest(fileToRead);

        // Then we get the expected data
        assertEquals("IndexPath1" , response.indexPath());
        int indexedTreatments = response.indexedElementsPerTarget().get("treatment");
        assertEquals(2 , indexedTreatments);

        FileUtils.deleteRecursively(new File(response.indexPath()));
    }

    @Test
    void shouldReturnIndexingRequestWhenFileHasCorrectData() throws IOException {
        // Given a file that has right data
        String fileToRead = DATA_DIR + "indexingRequest.json";

        // When we read the file
        IndexingRequest indexingRequest = IndexingRequestService.readIndexingRequest(fileToRead);

        // Then we get the expected data

        assertNotNull(indexingRequest);
        assertEquals("IndexPath1", indexingRequest.indexPath());

        // Validate rule locations

        List<RuleLocation> ruleLocations = indexingRequest.ruleLocations();

        assertNotNull(ruleLocations, "Expected to have rule locations");
        assertEquals(3, ruleLocations.size(), "unexpected number of rule locations");

        RuleLocation ruleLocation1 = ruleLocations.get(0);
        assertEquals("/path/file/treatments.json", ruleLocation1.filePath());
        assertEquals("treatment", ruleLocation1.name());
        assertFalse(ruleLocation1.ignore());

        RuleLocation ruleLocation2 = ruleLocations.get(1);
        assertEquals("/path/file/diagnosis.json", ruleLocation2.filePath());
        assertEquals("diagnosis", ruleLocation2.name());
        assertFalse(ruleLocation2.ignore());

        RuleLocation ruleLocation3 = ruleLocations.get(2);
        assertEquals("/path/file/to_be_ignored.json", ruleLocation3.filePath());
        assertEquals("to_be_ignored", ruleLocation3.name());
        assertTrue(ruleLocation3.ignore());

        // Validate ontology locations

        List<OntologyLocation> ontologyLocations = indexingRequest.ontologyLocations();

        assertNotNull(ontologyLocations, "Expected to have ontology locations");
        assertEquals(1, ontologyLocations.size(), "unexpected number of ontology locations");

        OntologyLocation ontLocation1 = ontologyLocations.get(0);
        assertEquals("ncit", ontLocation1.ontoId());
        assertEquals("ncit ontology diagnosis", ontLocation1.name());
        assertEquals(Arrays.asList("NCIT_C9305", "NCIT_C3262"), ontLocation1.branches());
        assertFalse(ontLocation1.ignore());
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
    void shouldFailWhenFileIsEmpty() {
        // Given a file that is empty
        String fileToRead = DATA_DIR + "emptyFile.json";

        // When we try to read the file
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            IndexingRequestService.readIndexingRequest(fileToRead));
        // Then we get an IOException
        assertEquals("File is empty: " + fileToRead, exception.getMessage());
    }
}
