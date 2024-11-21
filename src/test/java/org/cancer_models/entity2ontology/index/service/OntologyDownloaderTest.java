package org.cancer_models.entity2ontology.index.service;

import org.cancer_models.entity2ontology.common.model.OntologyTerm;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OntologyDownloaderTest {

    @Test
    void shouldFailWhenNullOntologyName() {
        // Given a null ontologyName
        String ontologyName = null;
        String termId = "";

        // When we try to download the branch
        OntologyDownloader ontologyDownloader = new OntologyDownloader();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            ontologyDownloader.downloadOntologyTerms(ontologyName, termId, "type"));

        // Then we get an exception explaining the ontology name cannot be null
        assertEquals("Ontology name cannot be null", exception.getMessage());
    }

    @Test
    void shouldFailWhenNullTermId() {
        // Given a null termId
        String ontologyName = "";
        String termId = null;

        // When we try to download the branch
        OntologyDownloader ontologyDownloader = new OntologyDownloader();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            ontologyDownloader.downloadOntologyTerms(ontologyName, termId, "type"));

        // Then we get an exception explaining the termId cannot be null
        assertEquals("termId cannot be null", exception.getMessage());
    }

    @Test
    void shouldFailWhenUnknownOntologyName() {
        // Given an unknown ontologyName
        String ontologyName = "unknown";
        String termId = "NCIT_C12218";

        // When we try to download the branch
        OntologyDownloader ontologyDownloader = new OntologyDownloader();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            ontologyDownloader.downloadOntologyTerms(ontologyName, termId, "type"));

        // Then we get an IOException
        assertEquals(String.format("The ontology %s does not exist in OLS", ontologyName) , exception.getMessage());
    }

    @Test
    void shouldGetOneTermIfBranchHasNoChildren() throws IOException {
        // Given a term that represents a branch without any children (a "leaf" in the ontology tree)
        String ontologyName = "ncit";
        String termId = "NCIT_C67515";

        // When we download the branch
        OntologyDownloader ontologyDownloader = new OntologyDownloader();
        Set<OntologyTerm> ontologyTermSet = ontologyDownloader.downloadOntologyTerms(ontologyName, termId, "regimen");

        // Then we get the expected ontology term
        assertEquals(1, ontologyTermSet.size());
        OntologyTerm ontologyTerm = ontologyTermSet.iterator().next();
        assertEquals("NCIT_C67515", ontologyTerm.getId());
        assertEquals("PEBA Regimen", ontologyTerm.getLabel());
        assertNotNull(ontologyTerm.getDescription());
        assertNotNull(ontologyTerm.getSynonyms());
        assertFalse(ontologyTerm.getSynonyms().isEmpty(), "Synonyms should not be empty");
    }

    @Test
    void shouldGetSeveralTermsIfBranchHasChildren() throws IOException {
        // Given a term that represents a branch with children
        String ontologyName = "ncit";
        String termId = "NCIT_C158908";

        // When we download the branch
        OntologyDownloader ontologyDownloader = new OntologyDownloader();
        Set<OntologyTerm> ontologyTermSet = ontologyDownloader.downloadOntologyTerms(
            ontologyName, termId, "diagnosis");

        // Then we get a collection of ontology terms
        assertTrue(ontologyTermSet.size() > 1);
    }
}
