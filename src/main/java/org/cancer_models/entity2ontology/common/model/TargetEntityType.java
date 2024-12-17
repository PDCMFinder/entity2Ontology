package org.cancer_models.entity2ontology.common.model;

import lombok.Getter;

@Getter
public enum TargetEntityType {
    RULE("rule"),
    ONTOLOGY("ontology");

    private final String value;

    TargetEntityType(String value) {
        this.value = value;
    }

}
