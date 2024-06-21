package org.cancerModels.entity2ontology.map.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Represents an entity we want to map to.
 * <p>
 * This class is used to encapsulate the details of a target entity. It represents a rule or an ontology
 * that will be compared to the source entity to see if it's a good match
 * </p>
 */
@Data
@NoArgsConstructor
public class TargetEntity {

    /**
     * Unique identifier of the entity
     */
    private String id;

    /**
     * Type of the entity. (Treatment or diagnosis, for instance)
     */
    private String entityType;

    /**
     * Rule or Ontology
     */
    private String targetType;

    /**
     * The data of the entity <field, value>
     */
    private Map<String, Object> data;

    /**
     * Label of the ontology term this entity was mapped to or represents
     */
    private String label;

    /**
     * Url of the ontology term this entity was mapped to or represents
     */
    private String url;


}
