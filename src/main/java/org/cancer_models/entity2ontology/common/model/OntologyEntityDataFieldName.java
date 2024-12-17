package org.cancer_models.entity2ontology.common.model;

import lombok.Getter;

@Getter
public enum OntologyEntityDataFieldName {
    LABEL("label"),
    DESCRIPTION("description"),
    SYNONYMS("synonyms");

    private final String value;

    OntologyEntityDataFieldName(String value) {
        this.value = value;
    }

}
