package org.cancer_models.entity2ontology.map.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A representation of a configuration that dictates which fields in the indexed documents to use when building
 * queries.
 */
@Getter
public class MappingConfiguration {

    /**
     * Name of the configuration. For tracking, not used for logic.
     */
    private String name;

    /**
     * List of {@code ConfigurationPerType} describing the configuration per entity type.
     */
    private final List<ConfigurationPerType> configurations = new ArrayList<>();

    public Map<String, Double> getFieldsWeightsByEntityType(String entityType) {
        Map<String, Double> weights = new HashMap<>();
        ConfigurationPerType configurationPerType = getConfigurationByEntityType(entityType);
        if (configurationPerType != null) {
            configurationPerType.fields.forEach(x -> weights.put(x.getName(), x.getWeight()));
        }
        return weights;
    }

    /**
     * Configuration unit in a configuration file. It has the entity name to be configured
     * (for example diagnosis, treatment) and the list of fields and weights belonging to that section
     */
    @Getter
    public static class ConfigurationPerType {
        /**
         * Entity type to be configured.
         */
        private String entityType;
        /**
         * Configuration of the fields in the entity.
         */
        private List<FieldConfiguration> fields;
        @Setter(AccessLevel.PACKAGE) private List<String> ontologyTemplates;

        /**
         * Utility to get the weights for the fields.
         * @return a map where the key is the name of the field and the value is the weight of the field
         */
        public Map<String, Double> getWeightsMap() {
            Map<String, Double> weights = new HashMap<>();
            fields.forEach(x -> weights.put(x.getName(), x.getWeight()));
            return weights;
        }
    }

    /**
     * Returns the configuration associated to an entity type.
     * @param entityType String indicating the entity type for which we want to find the configuration
     * @return {@link ConfigurationPerType} corresponding to the {@code entityType}
     */
    public ConfigurationPerType getConfigurationByEntityType(String entityType) {
        for (ConfigurationPerType configuration : configurations) {
            if (configuration.getEntityType().equals(entityType)) {
                return configuration;
            }
        }
        throw new IllegalArgumentException("No configuration found for entity type [" + entityType + "]");
    }

    /**
     * This class contains the name of the field in the indexed documents which will be used in a query. It also
     * contains the weight, which determines how important the field is respect of other fields in a query.
     */
    @Getter
    public static class FieldConfiguration {
        /**
         * Name of the field.
         */
        private String name;
        /**
         * Weight of the field, reflecting how relevant it is respect to others.
         */
        private double weight;
    }
}
