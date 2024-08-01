package org.cancerModels.entity2ontology.index.service;

import org.cancerModels.entity2ontology.common.model.OntologyTerm;
import org.cancerModels.entity2ontology.index.model.OntologyLocation;
import org.cancerModels.entity2ontology.map.model.TargetEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OntologyExtractorTest {

    @Spy
    OntologyExtractor instance = new OntologyExtractor();

    @Test
    void dummy() throws IOException {
        // Given we have an OntologyLocation with 3 branches (with no descendants, to simplify)
        OntologyLocation ontologyLocation = createOntologyLocation();

        // When we extract target entities
        doReturn(createMockedTermsBranch1())
            .when(instance).downloadOntologyTerms("ncit", "NCIT_C9305", "ncit ontology diagnosis");
        doReturn(createMockedTermsBranch2())
            .when(instance).downloadOntologyTerms("ncit", "NCIT_C3262", "ncit ontology diagnosis");
        doReturn(createMockedTermsBranch3())
            .when(instance).downloadOntologyTerms("ncit", "NCIT_C35814", "ncit ontology diagnosis");
        List<TargetEntity> targetEntities = instance.extract(ontologyLocation);

        // Then we obtain 3 target entities with the expected data
        assertEquals(3, targetEntities.size());
    }

    private OntologyLocation createOntologyLocation() {
        OntologyLocation ontologyLocation = new OntologyLocation();
        ontologyLocation.setOntoId("ncit");
        ontologyLocation.setName("ncit ontology diagnosis");

        List<String> diagnosis = new ArrayList<>();
        diagnosis.add("NCIT_C9305");
        diagnosis.add("NCIT_C3262");
        diagnosis.add("NCIT_C35814");

        ontologyLocation.setBranches(diagnosis);

        return ontologyLocation;
    }

    private Set<OntologyTerm> createMockedTermsBranch1() {
        Set<OntologyTerm> ontologyTerms = new HashSet<>();
        OntologyTerm ontologyTerm = new OntologyTerm(
            "NCIT_C9305",
            "http://purl.obolibrary.org/obo/NCIT_C9305",
            "Malignant Neoplasm",
            "ncit ontology diagnosis",
            "A neoplasm composed of atypical neoplastic...",
            new HashSet<>(Arrays.asList("malignancy", "Malignant Growth", "Malignant Neoplasm"))
        );
        ontologyTerms.add(ontologyTerm);
        return ontologyTerms;
    }

    private Set<OntologyTerm> createMockedTermsBranch2() {
        Set<OntologyTerm> ontologyTerms = new HashSet<>();
        OntologyTerm ontologyTerm = new OntologyTerm(
            "NCIT_C3262",
            "http://purl.obolibrary.org/obo/NCIT_C3262",
            "Neoplasm",
            "ncit ontology diagnosis",
            "A benign or malignant tissue growth resulting from uncontrolled cell proliferation...",
            new HashSet<>(Arrays.asList("neoplasia", "Neoplasia", "Neoplasm, NOS"))
        );
        ontologyTerms.add(ontologyTerm);
        return ontologyTerms;
    }

    private Set<OntologyTerm> createMockedTermsBranch3() {
        Set<OntologyTerm> ontologyTerms = new HashSet<>();
        OntologyTerm ontologyTerm = new OntologyTerm(
            "NCIT_C35814",
            "http://purl.obolibrary.org/obo/NCIT_C35814",
            "Hematopoietic and Lymphatic System Disorder",
            "ncit ontology diagnosis",
            "A non-neoplastic or neoplastic disorder that affects the hematopoietic and lymphatic system.",
            new HashSet<>(Arrays.asList("Hematopoietic and Lymphoid System Disorder"))
        );
        ontologyTerms.add(ontologyTerm);
        return ontologyTerms;
    }

    private Set<OntologyTerm> createMockedOntologyTerms() {
        Set<OntologyTerm> ontologyTerms = new HashSet<>();
        OntologyTerm ontologyTerm1 = new OntologyTerm(
            "NCIT_C9305",
            "http://purl.obolibrary.org/obo/NCIT_C9305",
            "Malignant Neoplasm",
            "ncit ontology diagnosis",
            "A neoplasm composed of atypical neoplastic...",
            new HashSet<>(Arrays.asList("malignancy", "Malignant Growth", "Malignant Neoplasm"))
        );
        OntologyTerm ontologyTerm2 = new OntologyTerm(
            "NCIT_C3262",
            "http://purl.obolibrary.org/obo/NCIT_C3262",
            "Neoplasm",
            "ncit ontology diagnosis",
            "A benign or malignant tissue growth resulting from uncontrolled cell proliferation...",
            new HashSet<>(Arrays.asList("neoplasia", "Neoplasia", "Neoplasm, NOS"))
        );
        OntologyTerm ontologyTerm3 = new OntologyTerm(
            "NCIT_C35814",
            "http://purl.obolibrary.org/obo/NCIT_C35814",
            "Hematopoietic and Lymphatic System Disorder",
            "ncit ontology diagnosis",
            "A non-neoplastic or neoplastic disorder that affects the hematopoietic and lymphatic system.",
            new HashSet<>(Arrays.asList("Hematopoietic and Lymphoid System Disorder"))
        );
        ontologyTerms.add(ontologyTerm1);
        ontologyTerms.add(ontologyTerm2);
        ontologyTerms.add(ontologyTerm3);

        return ontologyTerms;
    }

}
