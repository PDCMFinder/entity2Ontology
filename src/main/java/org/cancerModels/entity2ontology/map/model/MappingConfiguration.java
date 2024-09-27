package org.cancerModels.entity2ontology.map.model;

import lombok.Data;

import java.util.List;

/**
 * A representation of a configuration that dictates which fields in the indexed documents to use when building
 * queries.
 */
@Data
public class MappingConfiguration {
    private String name;
    private List<ConfigurationSection> ruleConfiguration;
    private List<ConfigurationSection> ontologyConfiguration;

    public ConfigurationSection getRuleConfigurationByEntityType(String entityType) {
        for (ConfigurationSection ruleConfiguration : ruleConfiguration) {
            if (ruleConfiguration.getSectionName().equals(entityType)) {
                return ruleConfiguration;
            }
        }
        return null;
    }

    /**
     * Configuration unit in a configuration file. It has a name that identifies the section to be configured
     * (for example diagnosis, treatment, ontology_id, etc.) and the list of fields belonging to that section
     */
    @Data
    public static class ConfigurationSection {
        private String sectionName;
        private List<FieldConfiguration> fields;
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
