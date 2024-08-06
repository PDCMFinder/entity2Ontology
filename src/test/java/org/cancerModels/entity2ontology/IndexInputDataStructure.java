package org.cancerModels.entity2ontology;

import lombok.Data;
import org.cancerModels.entity2ontology.map.model.TargetEntity;

import java.util.List;
@Data
/**
 * Utility class that represents a JSON file with target entities to be used to create an index
 * for testing purposes.
 */
public class IndexInputDataStructure {
    String name;
    String description;
    List<TargetEntity> targetEntities;
}
