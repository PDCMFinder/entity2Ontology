package org.cancer_models.entity2ontology.common.model;

import lombok.Getter;

@Getter
public enum TargetEntityFieldName {
    ID("id"),
    ENTITY_TYPE("entityType"),
    TARGET_TYPE("targetType"),
    LABEL("label"),
    URL("url");

    private final String value;

    TargetEntityFieldName(String value) {
        this.value = value;
    }

}
