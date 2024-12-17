package org.cancer_models.entity2ontology;

import lombok.Data;
import org.cancer_models.entity2ontology.common.model.TargetEntity;

import java.util.List;

/**
 * Utility class that represents a JSON file with target entities to be used to create an index
 * for testing purposes.
 */
@Data
public class IndexInputDataStructure {
    String name;
    String description;
    List<TargetEntity> targetEntities;
}
