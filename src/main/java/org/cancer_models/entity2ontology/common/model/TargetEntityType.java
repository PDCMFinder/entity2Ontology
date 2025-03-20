package org.cancer_models.entity2ontology.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TargetEntityType {
    RULE("rule"),
    ONTOLOGY("ontology");

    private final String value;

    TargetEntityType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {  // Explicitly define the getter to add @JsonValue
        return value;
    }

    @JsonCreator
    public static TargetEntityType fromString(String value) {
        return Arrays.stream(values())
            .filter(e -> e.value.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No enum constant for value: " + value));
    }

}
