package org.cancerModels.entity2ontology.map.model;

import lombok.Data;

import java.util.List;

@Data
public class MappingConfiguration {
    private String name;
    private List<RuleConfiguration> ruleConfiguration;
    private OntologyConfiguration ontologyConfiguration;

    public RuleConfiguration getRuleConfigurationByEntityType(String entityType) {
        for (RuleConfiguration ruleConfiguration : ruleConfiguration) {
            if (ruleConfiguration.getEntityType().equals(entityType)) {
                return ruleConfiguration;
            }
        }
        return null;
    }

    @Data
    public static class RuleConfiguration {
        private String entityType;
        private List<RuleConfigurationField> fields;
    }

    @Data
    public static class RuleConfigurationField {
        private String name;
        private double weight;
        private boolean main;
    }

    @Data
    static class OntologyConfiguration {
        private List<OntologyConfigurationField> fields;
    }

    @Data
    static class OntologyConfigurationField {
        private String name;
        private double weight;
    }
}
