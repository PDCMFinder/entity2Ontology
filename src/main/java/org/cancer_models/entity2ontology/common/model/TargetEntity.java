package org.cancer_models.entity2ontology.common.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents an entity we want to map to.
 * <p>
 * This class is used to encapsulate the details of a target entity. It represents a rule or an ontology
 * that will be compared to the source entity to see if it's a good match.
 * </p>
 *
 * @param id         Unique identifier of the entity.
 * @param entityType Type of the entity. (Treatment or diagnosis, for instance).
 * @param targetType Rule or Ontology.
 * @param dataFields The data of the entity {@code TargetEntityDataFields}.
 * @param label      Label of the ontology term this entity was mapped to or represents.
 * @param url        Url of the ontology term this entity was mapped to or represents.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public record TargetEntity(
    String id,
    String entityType,
    TargetEntityType targetType,
    TargetEntityDataFields dataFields,
    String label,
    String url) {
}
