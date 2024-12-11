package org.cancer_models.entity2ontology.map.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a request for mapping source entities to target entities with a specified type and a limit on the number of suggestions.
 * <p>
 * This class is used to encapsulate the parameters for a mapping request, including the maximum number of
 * suggested mappings per source entity and the type of entities to be mapped. If different types of entities need to be mapped,
 * separate instances of this request should be created.
 * </p>
 */
@Getter
@NoArgsConstructor
public class MappingRequest {
    // Maximum number of suggested mappings per entity
    private int maxSuggestions;

    // The name of the index to use in the mapping process
    private String indexPath;

    // Path to the file containing the {@link MappingConfiguration} object for the mapping process
    private String mappingConfigurationFile;

    // List of entities to map
    private List<SourceEntity> entities;

    public MappingRequest(
        @JsonProperty("maxNumSuggestions") int maxSuggestions,
        @JsonProperty("indexPath") String indexPath,
        @JsonProperty("mappingConfigurationFile") String mappingConfigurationFile,
        @JsonProperty("entities") List<SourceEntity> entities) {
        this.maxSuggestions = maxSuggestions;
        this.indexPath = indexPath;
        this.mappingConfigurationFile = mappingConfigurationFile;
        this.entities = entities;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("maxNumSuggestions: ").append(maxSuggestions).append("\n");
        sb.append("entities: ").append("[").append("\n");
        String newLine = "";
        if (entities != null) {
            for (SourceEntity entity : entities) {
                sb.append(newLine).append("\t").append(entity.toString());
                newLine = System.lineSeparator();
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
