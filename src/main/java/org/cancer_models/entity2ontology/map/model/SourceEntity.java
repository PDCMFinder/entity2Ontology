package org.cancer_models.entity2ontology.map.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

/**
 * Represents a source entity in the mapping process.
 * <p>
 * This class is used to encapsulate the details of a source entity that needs to be mapped to a target entity.
 * It contains a id and the a map with the pairs field-value (the data)
 * </p>
 */
@Setter
@Getter
@NoArgsConstructor
public class SourceEntity {

    /**
     * Unique identifier of the entity
     */
    private String id;

    /**
     * Type of the entity. It impacts on how the search will be processed
     */
    private String type;

    /**
     * The data of the entity {@code <field, value>}
     */
    private Map<String, String> data;

    public SourceEntity(String id, Map<String, String> data) {
        this.id = id;
        this.data = data;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("id: [").append(id).append("] type: [").append(type).append("] data: {");
        String sep = "";
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result.append(sep).append(entry.getKey()).append(": ").append(entry.getValue());
                sep = ", ";
            }
        }
        result.append("}");
        return result.toString();
    }
}
