package org.cancerModels.entity2ontology.map.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A representation of a configuration that dictates which fields in the indexed documents to use when building
 * queries.
 */
@Data
public class MappingConfiguration {
    private String name;
    private List<ConfigurationPerType> configurations;

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
    @Data
    public static class ConfigurationPerType {
        private String entityType;
        private List<FieldConfiguration> fields;
        private List<String> ontologyTemplates;

        public Map<String, Double> getWeightsMap() {
            Map<String, Double> weights = new HashMap<>();
            fields.forEach(x -> weights.put(x.getName(), x.getWeight()));
            return weights;
        }
    }

    public ConfigurationPerType getConfigurationByEntityType(String entityType) {
        for (ConfigurationPerType configuration : configurations) {
            if (configuration.getEntityType().equals(entityType)) {
                return configuration;
            }
        }
        return null;
    }



    /**
     * This class contains the name of the field in the indexed documents which will be used in a query. It also
     * contains the weight, which determines how important the field is respect of other fields in a query.
     */
    @Data
    public static class FieldConfiguration {
        private String name;
        private double weight;
    }
}
